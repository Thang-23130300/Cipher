package nlu.fit.web.souvenirecommerce.features.signature.dao;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import org.hibernate.Session;

public class OrderSignedDataDAO {

    public void saveOrUpdate(Long orderId, String signedDataJson, String hashValue) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId không được null");
        }
        if (signedDataJson == null || signedDataJson.isBlank()) {
            throw new IllegalArgumentException("signedDataJson không được rỗng");
        }
        if (hashValue == null || hashValue.isBlank()) {
            throw new IllegalArgumentException("hashValue không được rỗng");
        }

        String sql = """
                INSERT INTO order_signed_data
                    (order_id, signed_data_json, hash_algorithm, hash_value, created_at)
                VALUES
                    (:orderId, :signedDataJson, 'SHA-256', :hashValue, NOW())
                ON DUPLICATE KEY UPDATE
                    signed_data_json = VALUES(signed_data_json),
                    hash_algorithm = VALUES(hash_algorithm),
                    hash_value = VALUES(hash_value),
                    created_at = NOW()
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        session.createNativeMutationQuery(sql)
                .setParameter("orderId", orderId)
                .setParameter("signedDataJson", signedDataJson)
                .setParameter("hashValue", hashValue)
                .executeUpdate();
    }
}