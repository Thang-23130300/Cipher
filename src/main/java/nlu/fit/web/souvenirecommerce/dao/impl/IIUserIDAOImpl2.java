package nlu.fit.web.souvenirecommerce.dao.impl;

import nlu.fit.web.souvenirecommerce.dao.IIUserIDAO;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;
import nlu.fit.web.souvenirecommerce.util.PasswordUtil;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class IIUserIDAOImpl2 extends AbstractHibernateIDAO<Long, User> implements IIUserIDAO {

    public IIUserIDAOImpl2() {
        super(User.class);
    }

    @Override
    public boolean hasEmailExist(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        String hql = """
                select count(u.id) from User u
                where lower(u.email) = lower(:email)
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(hql, Long.class)
                    .setParameter("email", email.trim())
                    .uniqueResult();
            return total != null && total > 0;
        }
    }

    @Override
    public Optional<User> findByUserEmailAndPassword(String userEmail, String password) {
        if (userEmail == null || userEmail.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        return findByUserEmail(userEmail)
                .filter(user -> user.isActive()
                        && user.getCredentials() != null
                        && PasswordUtil.checkPassword(password, user.getCredentials().getPasswordHash()));
    }

    @Override
    public List<User> findAll() {
        String hql = """
                select distinct u from User u
                left join fetch u.credentials
                left join fetch u.roles r
                left join fetch r.permissions
                left join fetch u.oauthAccounts
                order by u.id desc
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, User.class).getResultList();
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        String hql = """
                select distinct u from User u
                left join fetch u.credentials
                left join fetch u.roles r
                left join fetch r.permissions
                left join fetch u.oauthAccounts
                where u.id = :id
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()){
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public Optional<User> findByUserEmail(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            return Optional.empty();
        }

        String hql = """
                select distinct u from User u
                left join fetch u.credentials
                left join fetch u.roles r
                left join fetch r.permissions
                left join fetch u.oauthAccounts
                where lower(u.email) = lower(:email)
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, User.class)
                    .setParameter("email", userEmail.trim())
                    .uniqueResultOptional();
        }
    }
}
