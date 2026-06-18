package nlu.fit.web.souvenirecommerce.features.order.service;

import jakarta.persistence.LockModeType;
import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.features.notification.service.AdminNotificationService;
import nlu.fit.web.souvenirecommerce.model.entity.Order;
import nlu.fit.web.souvenirecommerce.model.entity.OrderStatus;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.model.enums.PaymentStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Set;

public class OrderManagementService {
    private static final Logger log = LoggerFactory.getLogger(OrderManagementService.class);
    public static final String STATUS_WAITING_SIGNATURE = "Chờ ký số";
    public static final String STATUS_WAITING_SIGN_CONFIRMATION = "Chờ ký xác nhận";
    public static final String STATUS_WAITING_PROCESSING = "Chờ xử lý";
    public static final String STATUS_CONFIRMED = "Đã xác nhận";
    public static final String STATUS_LEGACY_PROCESSING = "Đang xử lý";
    public static final String STATUS_CANCELLED = "Đã hủy";

    private static final Set<String> ACCEPTABLE_STATUSES = Set.of(
            normalize(STATUS_WAITING_SIGNATURE),
            normalize(STATUS_WAITING_SIGN_CONFIRMATION),
            normalize(STATUS_WAITING_PROCESSING)
    );
    private static final Set<String> CANCELLABLE_STATUSES = Set.of(
            normalize(STATUS_WAITING_SIGNATURE),
            normalize(STATUS_WAITING_SIGN_CONFIRMATION),
            normalize(STATUS_WAITING_PROCESSING),
            normalize(STATUS_CONFIRMED),
            normalize(STATUS_LEGACY_PROCESSING),
            normalize("Chờ thanh toán"),
            normalize("Thanh toán thất bại")
    );

    private final AdminNotificationService notificationService = new AdminNotificationService();

    public ActionResult acceptOrder(Long orderId, User actor, String actorRole) {
        return execute(orderId, actor, actorRole, Action.ACCEPT, null);
    }

    public ActionResult cancelOrder(Long orderId, User actor, String actorRole, String reason) {
        String safeReason = reason == null ? "" : reason.trim();
        if (safeReason.isEmpty()) {
            throw new OrderActionException("Vui lòng nhập lý do hủy đơn hàng.");
        }
        if (safeReason.length() > 500) {
            throw new OrderActionException("Lý do hủy không được vượt quá 500 ký tự.");
        }
        return execute(orderId, actor, actorRole, Action.CANCEL, safeReason);
    }

    public static boolean canAcceptStatus(String status, String signatureStatus) {
        return "SIGNED".equals(normalize(signatureStatus))
                && ACCEPTABLE_STATUSES.contains(normalize(status));
    }

    public static boolean canCancelStatus(String status) {
        return CANCELLABLE_STATUSES.contains(normalize(status));
    }

    private ActionResult execute(Long orderId,
                                 User actor,
                                 String actorRole,
                                 Action action,
                                 String reason) {
        validateActorAndOrder(orderId, actor);

        Transaction transaction = null;
        Session session = null;
        RuntimeException toThrow = null;
        try {
            log.info("Order action START. action={}, orderId={}, actorId={}, actorRole={}",
                    action, orderId, actor.getId(), actorRole);
            log.debug("Opening Hibernate session for order action. action={}, orderId={}", action, orderId);
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            log.debug("Loading order with lock. action={}, orderId={}", action, orderId);
            Order order = session.createQuery("""
                            select o
                            from CustomerOrder o
                            join fetch o.status
                            left join fetch o.paymentTransaction
                            where o.id = :orderId
                            """, Order.class)
                    .setParameter("orderId", orderId)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .uniqueResult();

            if (order == null) {
                throw new OrderActionException("Không tìm thấy đơn hàng #" + orderId + ".");
            }

            String previousStatus = order.getStatusDescription();
            log.info("Loaded order for action. action={}, orderId={}, currentStatus={}, signatureStatus={}, paymentStatus={}",
                    action,
                    orderId,
                    previousStatus,
                    order.getSignatureStatus(),
                    order.getPaymentTransaction() == null ? null : order.getPaymentTransaction().getStatus());
            String targetStatus;
            String actionText;

            if (action == Action.ACCEPT) {
                if (!canAcceptStatus(previousStatus, order.getSignatureStatus())) {
                    if (!"SIGNED".equals(normalize(order.getSignatureStatus()))) {
                        throw new OrderActionException("Đơn hàng chưa có chữ ký hợp lệ, không thể chấp nhận.");
                    }
                    throw new OrderActionException("Không thể chấp nhận đơn ở trạng thái " + previousStatus + ".");
                }
                targetStatus = STATUS_CONFIRMED;
                actionText = "đã chấp nhận";
            } else {
                if (!canCancelStatus(previousStatus)) {
                    throw new OrderActionException("Không thể hủy đơn ở trạng thái " + previousStatus + ".");
                }
                if (order.getPaymentTransaction() != null
                        && order.getPaymentTransaction().getStatus() == PaymentStatus.PAID) {
                    throw new OrderActionException("Đơn hàng đã thanh toán, cần xử lý hoàn tiền trước khi hủy.");
                }
                targetStatus = STATUS_CANCELLED;
                actionText = "đã hủy";
            }

            OrderStatus target = session.createQuery(
                            "from OrderStatus s where s.description = :description", OrderStatus.class)
                    .setParameter("description", targetStatus)
                    .uniqueResult();
            if (target == null) {
                throw new OrderActionException(
                        "Thiếu trạng thái '" + targetStatus + "' trong bảng order_status."
                );
            }

            log.info("Updating order status. action={}, orderId={}, oldStatus={}, newStatus={}",
                    action, orderId, previousStatus, targetStatus);
            order.setStatus(target);
            log.debug("Creating admin notification in same transaction. action={}, orderId={}", action, orderId);
            notificationService.notifyOrderAction(
                    session,
                    orderId,
                    actor.getId(),
                    actor.getFullName(),
                    actorRole,
                    actionText,
                    targetStatus,
                    reason
            );
            log.debug("Flushing Hibernate session. action={}, orderId={}", action, orderId);
            session.flush();
            transaction.commit();
            log.info("Order action END. action={}, orderId={}, oldStatus={}, newStatus={}",
                    action, orderId, previousStatus, targetStatus);

            String message = action == Action.ACCEPT
                    ? "Đã chấp nhận đơn hàng #" + orderId + "."
                    : "Đã hủy đơn hàng #" + orderId + ".";
            return new ActionResult(orderId, previousStatus, targetStatus, message);
        } catch (RuntimeException exception) {
            // Log original exception with context to help debugging root cause
            Long actorId = actor == null ? null : actor.getId();
            log.error("Order update failed. action={}, orderId={}, actorId={}", action, orderId, actorId, exception);

            // attempt rollback only if active; if rollback fails, attach as suppressed to original
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException rbEx) {
                    exception.addSuppressed(rbEx);
                }
            }
            // preserve specific domain exception
            if (exception instanceof OrderActionException) {
                toThrow = (OrderActionException) exception;
            } else {
                toThrow = exception;
            }
        } finally {
            if (session != null && session.isOpen()) {
                try {
                    session.close();
                } catch (RuntimeException closeEx) {
                    if (toThrow != null) {
                        toThrow.addSuppressed(closeEx);
                    } else {
                        // no prior exception, but closing failed -> wrap and throw
                        throw new OrderActionException("Không thể đóng Hibernate session.", closeEx);
                    }
                }
            }
        }
        if (toThrow != null) throw toThrow;
        // should not reach here
        throw new OrderActionException("Không thể cập nhật đơn hàng do lỗi không xác định.");
    }

    private void validateActorAndOrder(Long orderId, User actor) {
        if (orderId == null || orderId <= 0) {
            throw new OrderActionException("Mã đơn hàng không hợp lệ.");
        }
        if (actor == null || actor.getId() == null) {
            throw new OrderActionException("Phiên đăng nhập không hợp lệ.");
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private enum Action {
        ACCEPT,
        CANCEL
    }

    public record ActionResult(Long orderId,
                               String previousStatus,
                               String newStatus,
                               String message) {
    }

    public static class OrderActionException extends RuntimeException {
        public OrderActionException(String message) {
            super(message);
        }

        public OrderActionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
