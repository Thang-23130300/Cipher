package nlu.fit.web.souvenirecommerce.legacy.dao.impl;

import nlu.fit.web.souvenirecommerce.legacy.dao.ISessionEntityIDAO;
import nlu.fit.web.souvenirecommerce.model.entity.UserSession;
import nlu.fit.web.souvenirecommerce.core.config.HibernateUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SessionDAOImpl extends AbstractHibernateIDAO<String, UserSession> implements ISessionEntityIDAO {

    public SessionDAOImpl() {
        super(UserSession.class);
    }

    @Override
    public List<UserSession> findAll() {
        String hql = """
                select s from UserSession s
                join fetch s.user
                order by s.createdAt desc
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, UserSession.class)
                    .getResultList();
        }
    }

    @Override
    public Optional<UserSession> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        String hql = """
                select s from UserSession s
                join fetch s.user
                where s.sessionId = :id
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, UserSession.class)
                    .setParameter("id", id.trim())
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<UserSession> findExpired(LocalDateTime now) {
        if (now == null) {
            return List.of();
        }

        String hql = """
                select s from UserSession s
                join fetch s.user
                where s.expiresAt <= :now
                order by s.expiresAt
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, UserSession.class)
                    .setParameter("now", now)
                    .getResultList();
        }
    }
}
