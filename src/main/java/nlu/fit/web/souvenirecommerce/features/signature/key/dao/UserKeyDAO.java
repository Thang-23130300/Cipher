package nlu.fit.web.souvenirecommerce.features.signature.key.dao;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserKeyDAO {

    public Optional<UserKeyDTO> findActiveKeyByUserId(Long userId) {
        return findActiveByUserId(userId);
    }

    public Optional<UserKeyDTO> findActiveByUserId(Long userId) {
        String sql = """
                SELECT id, user_id, public_key, key_algorithm, key_size,
                       signature_algorithm, key_status, created_at, revoked_at
                FROM user_keys
                WHERE user_id = :userId
                  AND key_status = 'ACTIVE'
                ORDER BY id DESC
                LIMIT 1
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        Object[] row = (Object[]) session.createNativeQuery(sql)
                .setParameter("userId", userId)
                .uniqueResult();

        return row == null ? Optional.empty() : Optional.of(mapRow(row));
    }

    public List<UserKeyDTO> findAllByUserId(Long userId) {
        String sql = """
                SELECT id, user_id, public_key, key_algorithm, key_size,
                       signature_algorithm, key_status, created_at, revoked_at
                FROM user_keys
                WHERE user_id = :userId
                ORDER BY id DESC
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        List<Object[]> rows = session.createNativeQuery(sql)
                .setParameter("userId", userId)
                .list();

        return rows.stream()
                .map(this::mapRow)
                .toList();
    }

    public void revokeActiveKeys(Long userId) {
        String sql = """
                UPDATE user_keys
                SET key_status = 'REVOKED',
                    revoked_at = NOW()
                WHERE user_id = :userId
                  AND key_status = 'ACTIVE'
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        session.createNativeMutationQuery(sql)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    public void savePublicKey(Long userId, String publicKey) {
        String sql = """
                INSERT INTO user_keys
                    (user_id, public_key, key_algorithm, key_size,
                     signature_algorithm, key_status, created_at)
                VALUES
                    (:userId, :publicKey, 'RSA', 2048,
                     'SHA256withRSA', 'ACTIVE', NOW())
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        session.createNativeMutationQuery(sql)
                .setParameter("userId", userId)
                .setParameter("publicKey", publicKey)
                .executeUpdate();
    }

    public void revokeById(Long keyId, Long userId) {
        String sql = """
                UPDATE user_keys
                SET key_status = 'REVOKED',
                    revoked_at = NOW()
                WHERE id = :keyId
                  AND user_id = :userId
                  AND key_status = 'ACTIVE'
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        session.createNativeMutationQuery(sql)
                .setParameter("keyId", keyId)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    private UserKeyDTO mapRow(Object[] row) {
        return UserKeyDTO.builder()
                .id(((Number) row[0]).longValue())
                .userId(((Number) row[1]).longValue())
                .publicKey((String) row[2])
                .keyAlgorithm((String) row[3])
                .keySize(row[4] == null ? null : ((Number) row[4]).intValue())
                .signatureAlgorithm((String) row[5])
                .keyStatus((String) row[6])
                .createdAt(toLocalDateTime(row[7]))
                .revokedAt(toLocalDateTime(row[8]))
                .build();
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }

        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

        throw new IllegalArgumentException("Không thể chuyển đổi kiểu thời gian: " + value.getClass().getName());
    }
}
