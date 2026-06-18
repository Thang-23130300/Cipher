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
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class OrderManagementService {
    private static final Logger log = LoggerFactory.getLogger(OrderManagementService.class);

    public static final String STATUS_WAITING_SIGNATURE = "Chờ ký số";
    public static final String STATUS_WAITING_SIGN_CONFIRMATION = "Chờ ký xác nhận";
    public static final String STATUS_WAITING_PROCESSING = "Chờ xử lý";
    public static final String STATUS_CONFIRMED = "Đã xác nhận";
    public static final String STATUS_LEGACY_PROCESSING = "Đang xử lý";
    public static final String STATUS_SHIPPING = "Đang giao hàng";
    public static final String STATUS_DELIVERED = "Đã giao hàng";
    public static final String STATUS_COMPLETED = "Hoàn thành";
    public static final String STATUS_PENDING_PAYMENT = "Chờ thanh toán";
    public static final String STATUS_PAYMENT_FAILED = "Thanh toán thất bại";
    public static final String STATUS_PAID = "Đã thanh toán";
    public static final String STATUS_CANCELLED = "Đã hủy";

    private static final Set<String> ACCEPTABLE_STATUSES = normalizedSet(
            STATUS_WAITING_SIGNATURE, STATUS_WAITING_SIGN_CONFIRMATION, STATUS_WAITING_PROCESSING
    );
    private static final Set<String> CANCELLABLE_STATUSES = normalizedSet(
            STATUS_WAITING_SIGNATURE, STATUS_WAITING_SIGN_CONFIRMATION, STATUS_WAITING_PROCESSING,
            STATUS_CONFIRMED, STATUS_LEGACY_PROCESSING, STATUS_PENDING_PAYMENT, STATUS_PAYMENT_FAILED
    );
    private static final Set<String> ADMIN_ROLES = Set.of("ADMIN", "SUPERADMIN");
    private static final Set<String> STAFF_ROLES = Set.of("SALE", "SALES", "STAFF");
    private static final Set<String> SIGNATURE_GATED_TARGETS = normalizedSet(
            STATUS_CONFIRMED, STATUS_LEGACY_PROCESSING, STATUS_SHIPPING, STATUS_DELIVERED,
            STATUS_COMPLETED, STATUS_PAID
    );
    private static final Map<String, Set<String>> STANDARD_TRANSITIONS = Map.ofEntries(
            transition(STATUS_WAITING_SIGNATURE, STATUS_CONFIRMED, STATUS_CANCELLED),
            transition(STATUS_WAITING_SIGN_CONFIRMATION, STATUS_CONFIRMED, STATUS_CANCELLED),
            transition(STATUS_WAITING_PROCESSING, STATUS_CONFIRMED, STATUS_CANCELLED),
            transition(STATUS_CONFIRMED, STATUS_LEGACY_PROCESSING, STATUS_CANCELLED),
            transition(STATUS_LEGACY_PROCESSING, STATUS_SHIPPING, STATUS_CANCELLED),
            transition(STATUS_SHIPPING, STATUS_DELIVERED),
            transition(STATUS_DELIVERED, STATUS_COMPLETED),
            transition(STATUS_PENDING_PAYMENT, STATUS_PAID, STATUS_PAYMENT_FAILED, STATUS_CANCELLED),
            transition(STATUS_PAYMENT_FAILED, STATUS_PENDING_PAYMENT, STATUS_CANCELLED),
            transition(STATUS_PAID, STATUS_CONFIRMED, STATUS_LEGACY_PROCESSING)
    );

    private final AdminNotificationService notificationService = new AdminNotificationService();

    public ActionResult acceptOrder(Long orderId, User actor, String actorRole) {
        return execute(orderId, actor, actorRole, Action.ACCEPT, null, null);
    }

    public ActionResult cancelOrder(Long orderId, User actor, String actorRole, String reason) {
        return execute(orderId, actor, actorRole, Action.CANCEL, null, validateReason(reason, true));
    }

    public ActionResult updateStatus(Long orderId,
                                     User actor,
                                     String actorRole,
                                     String targetStatus,
                                     String reason) {
        if (targetStatus == null || targetStatus.isBlank()) {
            throw new OrderActionException("Vui lòng chọn trạng thái mới.");
        }
        return execute(orderId, actor, actorRole, Action.ADMIN_UPDATE,
                targetStatus.trim(), validateReason(reason, false));
    }

    public static boolean canAcceptStatus(String status, String signatureStatus) {
        return "SIGNED".equals(normalize(signatureStatus))
                && ACCEPTABLE_STATUSES.contains(normalize(status));
    }

    public static boolean canCancelStatus(String status) {
        return CANCELLABLE_STATUSES.contains(normalize(status));
    }

    public static boolean isStandardTransition(String oldStatus, String newStatus) {
        return STANDARD_TRANSITIONS.getOrDefault(normalize(oldStatus), Set.of())
                .contains(normalize(newStatus));
    }

    private ActionResult execute(Long orderId,
                                 User actor,
                                 String actorRole,
                                 Action action,
                                 String requestedStatus,
                                 String reason) {
        ActorType actorType = validateActorAndOrder(orderId, actor, actorRole);
        if (action == Action.ADMIN_UPDATE && actorType != ActorType.ADMIN) {
            throw new OrderActionException("Sale/Staff chỉ được chấp nhận hoặc hủy đơn hàng.");
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Order order = loadOrderForUpdate(session, orderId);
            if (order == null) {
                throw new OrderActionException("Không tìm thấy đơn hàng #" + orderId + ".");
            }

            String previousStatus = order.getStatusDescription();
            String targetStatus = resolveTargetStatus(action, requestedStatus);
            String effectiveReason = validateTransition(order, action, actorType,
                    previousStatus, targetStatus, reason);

            OrderStatus target = session.createQuery(
                            "from OrderStatus s where s.description = :description", OrderStatus.class)
                    .setParameter("description", targetStatus)
                    .uniqueResult();
            if (target == null) {
                throw new OrderActionException(
                        "Trạng thái '" + targetStatus + "' không tồn tại trong bảng order_status."
                );
            }

            log.info("Updating order status. action={}, orderId={}, oldStatus={}, newStatus={}, actorId={}, actorRole={}",
                    action, orderId, previousStatus, targetStatus, actor.getId(), actorRole);
            order.setStatus(target);
            insertStatusHistory(session, orderId, previousStatus, targetStatus,
                    actor.getId(), actorRole, effectiveReason);

            if (actorType == ActorType.STAFF) {
                notificationService.notifyOrderAction(
                        session,
                        orderId,
                        actor.getId(),
                        actor.getFullName(),
                        actorRole,
                        action == Action.CANCEL ? "đã hủy" : "đã chấp nhận",
                        targetStatus,
                        effectiveReason
                );
            }

            session.flush();
            transaction.commit();
            String message = action == Action.CANCEL
                    ? "Đã hủy đơn hàng #" + orderId + "."
                    : action == Action.ACCEPT
                    ? "Đã chấp nhận đơn hàng #" + orderId + "."
                    : "Đã cập nhật trạng thái đơn hàng #" + orderId + ".";
            return new ActionResult(orderId, previousStatus, targetStatus, message);
        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
            }
            log.error("Order status change failed. action={}, orderId={}, actorId={}",
                    action, orderId, actor == null ? null : actor.getId(), exception);
            if (exception instanceof OrderActionException orderActionException) {
                throw orderActionException;
            }
            throw new OrderActionException("Không thể cập nhật trạng thái đơn hàng.", exception);
        }
    }

    private Order loadOrderForUpdate(Session session, Long orderId) {
        return session.createQuery("""
                        select o
                        from CustomerOrder o
                        join fetch o.status
                        left join fetch o.paymentTransaction
                        where o.id = :orderId
                        """, Order.class)
                .setParameter("orderId", orderId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .uniqueResult();
    }

    private String validateTransition(Order order,
                                      Action action,
                                      ActorType actorType,
                                      String previousStatus,
                                      String targetStatus,
                                      String reason) {
        if (normalize(previousStatus).equals(normalize(targetStatus))) {
            throw new OrderActionException("Đơn hàng đã ở trạng thái " + targetStatus + ".");
        }
        if (action == Action.ACCEPT && !canAcceptStatus(previousStatus, order.getSignatureStatus())) {
            if (!"SIGNED".equals(normalize(order.getSignatureStatus()))) {
                throw new OrderActionException("Đơn hàng chưa có chữ ký hợp lệ, không thể chấp nhận.");
            }
            throw new OrderActionException("Không thể chấp nhận đơn ở trạng thái " + previousStatus + ".");
        }
        if (action == Action.CANCEL && !canCancelStatus(previousStatus)) {
            throw new OrderActionException("Không thể hủy đơn ở trạng thái " + previousStatus + ".");
        }
        if (SIGNATURE_GATED_TARGETS.contains(normalize(targetStatus))
                && !"SIGNED".equals(normalize(order.getSignatureStatus()))) {
            throw new OrderActionException("Đơn hàng chưa có chữ ký hợp lệ, không thể xử lý.");
        }
        if (normalize(STATUS_CANCELLED).equals(normalize(targetStatus))
                && order.getPaymentTransaction() != null
                && order.getPaymentTransaction().getStatus() == PaymentStatus.PAID) {
            throw new OrderActionException("Đơn hàng đã thanh toán, cần xử lý hoàn tiền trước khi hủy.");
        }

        boolean standardTransition = isStandardTransition(previousStatus, targetStatus);
        if (!standardTransition && actorType != ActorType.ADMIN) {
            throw new OrderActionException("Không được chuyển đơn từ '" + previousStatus
                    + "' sang '" + targetStatus + "'.");
        }
        if (!standardTransition && (reason == null || reason.isBlank())) {
            throw new OrderActionException("ADMIN phải nhập lý do khi can thiệp chuyển trạng thái ngoài luồng chuẩn.");
        }
        if (action == Action.ACCEPT) {
            return "Chấp nhận đơn hàng";
        }
        return reason;
    }

    private void insertStatusHistory(Session session,
                                     Long orderId,
                                     String oldStatus,
                                     String newStatus,
                                     Long actorId,
                                     String actorRole,
                                     String reason) {
        String sql = """
                INSERT INTO order_audit_logs
                    (order_id, actor_id, actor_role, action_type, field_name,
                     old_value, new_value, reason, is_signed_field, created_at)
                VALUES
                    (:orderId, :actorId, :actorRole, 'ORDER_STATUS_CHANGED', 'status',
                     :oldStatus, :newStatus, :reason, 0, NOW())
                """;
        session.createNativeMutationQuery(sql)
                .setParameter("orderId", orderId, StandardBasicTypes.LONG)
                .setParameter("actorId", actorId, StandardBasicTypes.LONG)
                .setParameter("actorRole", actorRole, StandardBasicTypes.STRING)
                .setParameter("oldStatus", oldStatus, StandardBasicTypes.STRING)
                .setParameter("newStatus", newStatus, StandardBasicTypes.STRING)
                .setParameter("reason", reason, StandardBasicTypes.STRING)
                .executeUpdate();
    }

    private ActorType validateActorAndOrder(Long orderId, User actor, String actorRole) {
        if (orderId == null || orderId <= 0) {
            throw new OrderActionException("Mã đơn hàng không hợp lệ.");
        }
        if (actor == null || actor.getId() == null) {
            throw new OrderActionException("Phiên đăng nhập không hợp lệ.");
        }
        String normalizedRole = normalizeRole(actorRole);
        if (ADMIN_ROLES.contains(normalizedRole)) {
            return ActorType.ADMIN;
        }
        if (STAFF_ROLES.contains(normalizedRole)) {
            return ActorType.STAFF;
        }
        throw new OrderActionException("Bạn không có quyền thay đổi trạng thái đơn hàng.");
    }

    private String resolveTargetStatus(Action action, String requestedStatus) {
        return switch (action) {
            case ACCEPT -> STATUS_CONFIRMED;
            case CANCEL -> STATUS_CANCELLED;
            case ADMIN_UPDATE -> requestedStatus;
        };
    }

    private static String validateReason(String reason, boolean required) {
        String safeReason = reason == null ? "" : reason.trim();
        if (required && safeReason.isEmpty()) {
            throw new OrderActionException("Vui lòng nhập lý do hủy đơn hàng.");
        }
        if (safeReason.length() > 500) {
            throw new OrderActionException("Lý do không được vượt quá 500 ký tự.");
        }
        return safeReason.isEmpty() ? null : safeReason;
    }

    private static Map.Entry<String, Set<String>> transition(String source, String... targets) {
        return Map.entry(normalize(source), normalizedSet(targets));
    }

    private static Set<String> normalizedSet(String... values) {
        return java.util.Arrays.stream(values).map(OrderManagementService::normalize).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private static String normalizeRole(String role) {
        return role == null ? "" : role.replaceAll("[^A-Za-z]", "").toUpperCase(Locale.ROOT);
    }

    private enum Action {
        ACCEPT,
        CANCEL,
        ADMIN_UPDATE
    }

    private enum ActorType {
        ADMIN,
        STAFF
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
