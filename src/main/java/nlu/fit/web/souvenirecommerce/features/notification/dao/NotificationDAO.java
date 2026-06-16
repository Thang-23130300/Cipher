package nlu.fit.web.souvenirecommerce.features.notification.dao;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.features.notification.dto.NotificationDTO;
import org.hibernate.Session;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationDAO {

    public List<NotificationDTO> findAll() {
        String sql = """
                SELECT n.id, n.recipient_user_id, u.full_name as recipient_name,
                       n.order_id, n.type, n.title, n.message, n.is_read, n.created_at
                FROM notifications n
                LEFT JOIN users u ON n.recipient_user_id = u.id
                ORDER BY n.id DESC
                """;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Object[]> rows = session.createNativeQuery(sql).list();

        return rows.stream()
                .map(row -> {
                    Long id = ((Number) row[0]).longValue();
                    Long recipientUserId = row[1] == null ? null : ((Number) row[1]).longValue();
                    String recipientName = (String) row[2];
                    Long orderId = row[3] == null ? null : ((Number) row[3]).longValue();
                    String type = (String) row[4];
                    String title = (String) row[5];
                    String message = (String) row[6];
                    boolean isRead = ((Number) row[7]).intValue() == 1;
                    LocalDateTime createdAt = toLocalDateTime(row[8]);
                    String orderCode = null;
                    if (orderId != null && createdAt != null) {
                        orderCode = "ORD-" + createdAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                                + "-" + String.format("%05d", orderId);
                    }
                    return NotificationDTO.builder()
                            .id(id)
                            .recipientUserId(recipientUserId)
                            .recipientName(recipientName)
                            .orderId(orderId)
                            .orderCode(orderCode)
                            .type(type)
                            .title(title)
                            .message(message)
                            .isRead(isRead)
                            .createdAt(createdAt)
                            .build();
                })
                .toList();
    }

    public void markAsRead(Long id) {
        String sql = "UPDATE notifications SET is_read = 1, read_at = NOW() WHERE id = :id";
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.createNativeMutationQuery(sql).setParameter("id", id).executeUpdate();
    }

    public void save(Long recipientUserId, Long orderId, String type, String title, String message) {
        String sql = """
                INSERT INTO notifications (recipient_user_id, order_id, type, title, message, is_read, created_at)
                VALUES (:recipientUserId, :orderId, :type, :title, :message, 0, NOW())
                """;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.createNativeMutationQuery(sql)
                .setParameter("recipientUserId", recipientUserId)
                .setParameter("orderId", orderId)
                .setParameter("type", type)
                .setParameter("title", title)
                .setParameter("message", message)
                .executeUpdate();
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDateTime localDateTime) return localDateTime;
        if (value instanceof java.sql.Timestamp timestamp) return timestamp.toLocalDateTime();
        throw new IllegalArgumentException("Không thể chuyển đổi kiểu thời gian: " + value.getClass().getName());
    }
}