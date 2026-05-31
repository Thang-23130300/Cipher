package nlu.fit.web.souvenirecommerce.features.auth.dao;

import jakarta.persistence.LockModeType;
import nlu.fit.web.souvenirecommerce.common.enums.VerificationCodePurpose;
import nlu.fit.web.souvenirecommerce.model.entity.VerificationCode;
import nlu.fit.web.souvenirecommerce.core.config.HibernateUtil;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.Optional;

public class SignupVerificationCodeDAO {

    public void createCode(String email, String code, LocalDateTime expiresAt) {
        Transaction transaction = null;
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            VerificationCode verificationCode = VerificationCode.builder()
                    .email(email.trim().toLowerCase())
                    .code(code)
                    .purpose(VerificationCodePurpose.SIGNUP)
                    .expiresAt(expiresAt)
                    .build();
            session.persist(verificationCode);
            transaction.commit();
        } catch (RuntimeException e) {
            rollback(transaction);
            throw e;
        }
    }

    public boolean verifySignupCode(String email, String code) {
        Transaction transaction = null;
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            LocalDateTime now = LocalDateTime.now();

            Optional<VerificationCode> verificationCode = session.createQuery("""
                            select vc from VerificationCode vc
                            where lower(vc.email) = lower(:email)
                              and vc.code = :code
                              and vc.purpose = :purpose
                              and vc.verifiedAt is null
                              and vc.expiresAt >= :now
                            order by vc.createdAt desc
                            """, VerificationCode.class)
                    .setParameter("email", email.trim().toLowerCase())
                    .setParameter("code", code)
                    .setParameter("purpose", VerificationCodePurpose.SIGNUP)
                    .setParameter("now", now)
                    .setMaxResults(1)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .uniqueResultOptional();

            if (verificationCode.isEmpty()) {
                transaction.commit();
                return false;
            }

            verificationCode.get().setVerifiedAt(now);
            session.merge(verificationCode.get());
            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            rollback(transaction);
            throw e;
        }
    }

    public long countSentToday(String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("""
                            select count(vc.id) from VerificationCode vc
                            where lower(vc.email) = lower(:email)
                              and vc.purpose = :purpose
                              and vc.createdAt >= :startOfDay
                              and vc.createdAt < :endOfDay
                            """, Long.class)
                    .setParameter("email", email.trim().toLowerCase())
                    .setParameter("purpose", VerificationCodePurpose.SIGNUP)
                    .setParameter("startOfDay", startOfDay)
                    .setParameter("endOfDay", endOfDay)
                    .uniqueResult();
            return count == null ? 0 : count;
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        try {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } catch (RuntimeException ignored) {
            // Ignore rollback failure to avoid hiding the original exception.
        }
    }
}
