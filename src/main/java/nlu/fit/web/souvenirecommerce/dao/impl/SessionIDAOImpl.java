package nlu.fit.web.souvenirecommerce.dao.impl;

import nlu.fit.web.souvenirecommerce.dao.ISessionEntityIDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Session;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SessionIDAOImpl extends AbstractHibernateIDAO<String, Session> implements ISessionEntityIDAO {

    public SessionIDAOImpl() {
        super(Session.class);
    }

    @Override
    public List<Session> findAll() {
        String hql = """
                select s from Session s
                join fetch s.user
                order by s.createdAt desc
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Session.class)
                    .getResultList();
        }
    }

    @Override
    public Optional<Session> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        String hql = """
                select s from Session s
                join fetch s.user
                where s.sessionId = :id
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Session.class)
                    .setParameter("id", id.trim())
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Session> findExpired(LocalDateTime now) {
        if (now == null) {
            return List.of();
        }

        String hql = """
                select s from Session s
                join fetch s.user
                where s.expiresAt <= :now
                order by s.expiresAt
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Session.class)
                    .setParameter("now", now)
                    .getResultList();
        }
    }
}
