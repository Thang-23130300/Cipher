package nlu.fit.web.souvenirecommerce.features.signature.dao;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import org.hibernate.Session;

public class OrderAuditLogDAO {

    public boolean existsLog(Long orderId, String actionType, String fieldName, String oldValue, String newValue) {
        String sql = """
                SELECT COUNT(*)
                FROM order_audit_logs
                WHERE order_id = :orderId
                  AND action_type = :actionType
                  AND field_name = :fieldName
                  AND COALESCE(old_value, '') = COALESCE(:oldValue, '')
                  AND COALESCE(new_value, '') = COALESCE(:newValue, '')
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        Number count = (Number) session.createNativeQuery(sql)
                .setParameter("orderId", orderId)
                .setParameter("actionType", actionType)
                .setParameter("fieldName", fieldName)
                .setParameter("oldValue", oldValue)
                .setParameter("newValue", newValue)
                .uniqueResult();

        return count != null && count.longValue() > 0;
    }

    public void insertLog(Long orderId,
                          Long actorId,
                          String actorRole,
                          String actionType,
                          String fieldName,
                          String oldValue,
                          String newValue,
                          boolean signedField) {
        if (existsLog(orderId, actionType, fieldName, oldValue, newValue)) {
            return;
        }

        String sql = """
                INSERT INTO order_audit_logs
                    (order_id, actor_id, actor_role, action_type, field_name,
                     old_value, new_value, is_signed_field, created_at)
                VALUES
                    (:orderId, :actorId, :actorRole, :actionType, :fieldName,
                     :oldValue, :newValue, :signedField, NOW())
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        session.createNativeMutationQuery(sql)
                .setParameter("orderId", orderId)
                .setParameter("actorId", actorId)
                .setParameter("actorRole", actorRole)
                .setParameter("actionType", actionType)
                .setParameter("fieldName", fieldName)
                .setParameter("oldValue", oldValue)
                .setParameter("newValue", newValue)
                .setParameter("signedField", signedField)
                .executeUpdate();
    }
}