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

    public SigningPreparation prepareForSigning(Long orderId, String endpoint) {
        if (orderId == null) {
            throw new IllegalArgumentException("Mã đơn hàng không hợp lệ.");
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Order order = loadOrderForUpdate(session, orderId);
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng #" + orderId + ".");
        }

        String oldOrderStatus = order.getStatusDescription();
        String signatureStatus = order.getSignatureStatus();
        if (normalize(CANCELLED).equals(normalize(oldOrderStatus))) {
            if (hasExplicitCancellation(session, orderId)) {
                logTransition(orderId, oldOrderStatus, oldOrderStatus, signatureStatus,
                        signatureStatus, endpoint, "Chặn ký: có lịch sử hủy đơn hợp lệ");
                return new SigningPreparation(true, false, false, oldOrderStatus);
            }

            String restoredStatus = SIGNED.equals(normalize(signatureStatus))
                    ? WAITING_CONFIRMATION
                    : WAITING_SIGNATURE;
            order.setStatus(resolveStatus(session, restoredStatus));
            insertRecoveryAudit(session, orderId, oldOrderStatus, restoredStatus);
            logTransition(orderId, oldOrderStatus, restoredStatus, signatureStatus,
                    signatureStatus, endpoint,
                    "Khôi phục trạng thái hủy mồ côi: không có lịch sử thao tác hủy");
            return new SigningPreparation(false, SIGNED.equals(normalize(signatureStatus)),
                    true, restoredStatus);
        }

        return new SigningPreparation(false, SIGNED.equals(normalize(signatureStatus)),
                false, oldOrderStatus);
    }

    public TransitionResult applyVerificationResult(Long orderId,
                                                    String newSignatureStatus,
                                                    String endpoint,
                                                    String reason) {
        if (orderId == null || newSignatureStatus == null || newSignatureStatus.isBlank()) {
            throw new IllegalArgumentException("Thiếu dữ liệu cập nhật trạng thái chữ ký.");
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Order order = loadOrderForUpdate(session, orderId);
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng #" + orderId + ".");
        }

        String oldOrderStatus = order.getStatusDescription();
        String oldSignatureStatus = order.getSignatureStatus();
        if (normalize(CANCELLED).equals(normalize(oldOrderStatus))) {
            if (hasExplicitCancellation(session, orderId)) {
                logTransition(orderId, oldOrderStatus, oldOrderStatus, oldSignatureStatus,
                        oldSignatureStatus, endpoint, "Từ chối ký: có lịch sử hủy đơn hợp lệ");
                throw new IllegalStateException("Đơn hàng đã được hủy hợp lệ, không thể ký lại.");
            }
            String restoredStatus = SIGNED.equals(normalize(newSignatureStatus))
                    ? WAITING_CONFIRMATION
                    : WAITING_SIGNATURE;
            order.setStatus(resolveStatus(session, restoredStatus));
            insertRecoveryAudit(session, orderId, oldOrderStatus, restoredStatus);
            logTransition(orderId, oldOrderStatus, restoredStatus, oldSignatureStatus,
                    oldSignatureStatus, endpoint,
                    "Khôi phục trạng thái hủy mồ côi: không có lịch sử thao tác hủy");
            oldOrderStatus = restoredStatus;
        }
        if (SIGNED.equals(normalize(oldSignatureStatus))) {
            logTransition(orderId, oldOrderStatus, oldOrderStatus, oldSignatureStatus,
                    oldSignatureStatus, endpoint, "Bỏ qua callback lặp: chữ ký đã hợp lệ");
            return new TransitionResult(oldOrderStatus, oldOrderStatus,
                    oldSignatureStatus, oldSignatureStatus, true);
        }

        String normalizedNewSignatureStatus = normalize(newSignatureStatus);
        String newOrderStatus = oldOrderStatus;
        if (SIGNED.equals(normalizedNewSignatureStatus)) {
            if (normalize(WAITING_SIGNATURE).equals(normalize(oldOrderStatus))) {
                order.setStatus(resolveStatus(session, WAITING_CONFIRMATION));
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

    private Order loadOrderForUpdate(Session session, Long orderId) {
        return session.createQuery("""
                        select o
                        from CustomerOrder o
                        join fetch o.status
                        where o.id = :orderId
                        """, Order.class)
                .setParameter("orderId", orderId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .uniqueResult();
    }

    private OrderStatus resolveStatus(Session session, String description) {
        OrderStatus status = session.createQuery(
                        "from OrderStatus s where s.description = :description", OrderStatus.class)
                .setParameter("description", description)
                .uniqueResult();
        if (status == null) {
            throw new IllegalStateException(
                    "Thiếu trạng thái '" + description + "' trong bảng order_status."
            );
        }
        return status;
    }

    private boolean hasExplicitCancellation(Session session, Long orderId) {
        Number count = (Number) session.createNativeQuery("""
                        select count(*)
                        from order_audit_logs
                        where order_id = :orderId
                          and field_name = 'status'
                          and upper(trim(coalesce(new_value, ''))) = upper(:cancelledStatus)
                          and actor_id is not null
                        """)
                .setParameter("orderId", orderId)
                .setParameter("cancelledStatus", CANCELLED)
                .uniqueResult();
        return count != null && count.longValue() > 0;
    }

    private void insertRecoveryAudit(Session session,
                                     Long orderId,
                                     String oldStatus,
                                     String newStatus) {
        session.createNativeMutationQuery("""
                        insert into order_audit_logs
                            (order_id, actor_id, actor_role, action_type, field_name,
                             old_value, new_value, reason, is_signed_field, created_at)
                        values
                            (:orderId, null, 'SYSTEM', 'LEGACY_STATUS_RECOVERED', 'status',
                             :oldStatus, :newStatus,
                             'Khôi phục trạng thái hủy mồ côi vì không có lịch sử thao tác hủy', 0, now())
                        """)
                .setParameter("orderId", orderId)
                .setParameter("oldStatus", oldStatus)
                .setParameter("newStatus", newStatus)
                .executeUpdate();
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

    public record SigningPreparation(boolean blockedByCancellation,
                                     boolean alreadyValid,
                                     boolean restoredLegacyCancellation,
                                     String orderStatus) {
    }
}
