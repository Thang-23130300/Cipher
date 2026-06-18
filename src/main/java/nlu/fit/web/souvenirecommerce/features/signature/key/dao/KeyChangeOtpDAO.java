package nlu.fit.web.souvenirecommerce.features.signature.key.dao;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.model.entity.KeyChangeOtp;
import org.hibernate.Session;
import java.util.Optional;

public class KeyChangeOtpDAO {

    public void save(KeyChangeOtp otp) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(otp);
    }

    public void update(KeyChangeOtp otp) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.merge(otp);
    }

    public Optional<KeyChangeOtp> findLatestPendingOtp(Long userId) {
        String hql = """
                FROM KeyChangeOtp k
                WHERE k.userId = :userId
                  AND k.purpose = 'KEY_CHANGE'
                  AND k.consumedAt IS NULL
                ORDER BY k.id DESC
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(hql, KeyChangeOtp.class)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .uniqueResultOptional();
    }
}
