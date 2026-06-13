package nlu.fit.web.souvenirecommerce.features.signature.dao;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import org.hibernate.Session;

public class OrderSignatureDAO {

    public void saveOrUpdate(Long orderId,
                             Long userId,
                             Long keyId,
                             String signatureValue,
                             String verifyStatus) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId không được null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId không được null");
        }
        if (keyId == null) {
            throw new IllegalArgumentException("keyId không được null");
        }
        if (signatureValue == null || signatureValue.isBlank()) {
            throw new IllegalArgumentException("signatureValue không được rỗng");
        }
        if (verifyStatus == null || verifyStatus.isBlank()) {
            throw new IllegalArgumentException("verifyStatus không được rỗng");
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Long existingId = findIdByOrderId(session, orderId);

        if (existingId == null) {
            String insertSql = """
                    INSERT INTO order_signatures
                        (order_id, user_id, key_id, signature_value,
                         signature_algorithm, signed_at, verify_status, verified_at)
                    VALUES
                        (:orderId, :userId, :keyId, :signatureValue,
                         'SHA256withRSA', NOW(), :verifyStatus, NOW())
                    """;

            session.createNativeMutationQuery(insertSql)
                    .setParameter("orderId", orderId)
                    .setParameter("userId", userId)
                    .setParameter("keyId", keyId)
                    .setParameter("signatureValue", signatureValue)
                    .setParameter("verifyStatus", verifyStatus)
                    .executeUpdate();
            return;
        }

        String updateSql = """
                UPDATE order_signatures
                SET user_id = :userId,
                    key_id = :keyId,
                    signature_value = :signatureValue,
                    signature_algorithm = 'SHA256withRSA',
                    verify_status = :verifyStatus,
                    verified_at = NOW()
                WHERE id = :id
                """;

        session.createNativeMutationQuery(updateSql)
                .setParameter("userId", userId)
                .setParameter("keyId", keyId)
                .setParameter("signatureValue", signatureValue)
                .setParameter("verifyStatus", verifyStatus)
                .setParameter("id", existingId)
                .executeUpdate();
    }

    private Long findIdByOrderId(Session session, Long orderId) {
        Object result = session.createNativeQuery("""
                        SELECT id
                        FROM order_signatures
                        WHERE order_id = :orderId
                        LIMIT 1
                        """)
                .setParameter("orderId", orderId)
                .uniqueResult();

        return result == null ? null : ((Number) result).longValue();
    }
}
