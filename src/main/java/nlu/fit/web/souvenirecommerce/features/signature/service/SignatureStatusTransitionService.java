package nlu.fit.web.souvenirecommerce.features.signature.service;

import jakarta.persistence.LockModeType;
import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.features.order.service.OrderManagementService;
import nlu.fit.web.souvenirecommerce.model.entity.Order;
import nlu.fit.web.souvenirecommerce.model.entity.OrderStatus;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Locale;

public class SignatureStatusTransitionService {
    private static final Logger log = LoggerFactory.getLogger(SignatureStatusTransitionService.class);
    private static final String SIGNED = "SIGNED";
    private static final String CANCELLED = OrderManagementService.STATUS_CANCELLED;
    private static final String WAITING_SIGNATURE = OrderManagementService.STATUS_WAITING_SIGNATURE;
    private static final String WAITING_CONFIRMATION = OrderManagementService.STATUS_WAITING_SIGN_CONFIRMATION;

    public TransitionResult applyVerificationResult(Long orderId,
                                                    String newSignatureStatus,
                                                    String endpoint,
                                                    String reason) {
        if (orderId == null || newSignatureStatus == null || newSignatureStatus.isBlank()) {
            throw new IllegalArgumentException("Thiếu dữ liệu cập nhật trạng thái chữ ký.");
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Order order = session.createQuery("""
                        select o
                        from CustomerOrder o
                        join fetch o.status
                        where o.id = :orderId
                        """, Order.class)
                .setParameter("orderId", orderId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .uniqueResult();
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng #" + orderId + ".");
        }

        String oldOrderStatus = order.getStatusDescription();
        String oldSignatureStatus = order.getSignatureStatus();
        if (SIGNED.equals(normalize(oldSignatureStatus))) {
            logTransition(orderId, oldOrderStatus, oldOrderStatus, oldSignatureStatus,
                    oldSignatureStatus, endpoint, "Bỏ qua callback lặp: chữ ký đã hợp lệ");
            return new TransitionResult(oldOrderStatus, oldOrderStatus,
                    oldSignatureStatus, oldSignatureStatus, true);
        }
        if (normalize(CANCELLED).equals(normalize(oldOrderStatus))) {
            logTransition(orderId, oldOrderStatus, oldOrderStatus, oldSignatureStatus,
                    oldSignatureStatus, endpoint, "Từ chối ký đơn đã hủy");
            throw new IllegalStateException("Đơn hàng đã hủy, không thể ký lại.");
        }

        String normalizedNewSignatureStatus = normalize(newSignatureStatus);
        String newOrderStatus = oldOrderStatus;
        if (SIGNED.equals(normalizedNewSignatureStatus)) {
            if (normalize(WAITING_SIGNATURE).equals(normalize(oldOrderStatus))) {
                OrderStatus waitingConfirmation = session.createQuery(
                                "from OrderStatus s where s.description = :description", OrderStatus.class)
                        .setParameter("description", WAITING_CONFIRMATION)
                        .uniqueResult();
                if (waitingConfirmation == null) {
                    throw new IllegalStateException(
                            "Thiếu trạng thái '" + WAITING_CONFIRMATION + "' trong bảng order_status."
                    );
                }
                order.setStatus(waitingConfirmation);
                newOrderStatus = WAITING_CONFIRMATION;
            }
            order.setSignatureStatus(SIGNED);
            order.setSignedAt(LocalDateTime.now());
        } else {
            order.setSignatureStatus(newSignatureStatus);
            order.setSignedAt(null);
            // Verify lỗi, user đóng trang hoặc tool lỗi không được thay đổi order_status.
        }

        logTransition(orderId, oldOrderStatus, newOrderStatus, oldSignatureStatus,
                newSignatureStatus, endpoint, reason);
        return new TransitionResult(oldOrderStatus, newOrderStatus,
                oldSignatureStatus, newSignatureStatus, false);
    }

    private void logTransition(Long orderId,
                               String oldOrderStatus,
                               String newOrderStatus,
                               String oldSignatureStatus,
                               String newSignatureStatus,
                               String endpoint,
                               String reason) {
        log.info("Order/signature status transition: orderId={}, oldOrderStatus={}, newOrderStatus={}, "
                        + "oldSignatureStatus={}, newSignatureStatus={}, endpoint={}, reason={}",
                orderId, oldOrderStatus, newOrderStatus, oldSignatureStatus,
                newSignatureStatus, endpoint, reason);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    public record TransitionResult(String oldOrderStatus,
                                   String newOrderStatus,
                                   String oldSignatureStatus,
                                   String newSignatureStatus,
                                   boolean alreadyValid) {
    }
}
