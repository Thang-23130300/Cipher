package nlu.fit.web.souvenirecommerce.auth.dao;

import nlu.fit.web.souvenirecommerce.dao.IUserDAO;
import nlu.fit.web.souvenirecommerce.dao.impl.AbstractHibernateIDAO;
import nlu.fit.web.souvenirecommerce.enums.Gender;
import nlu.fit.web.souvenirecommerce.model.entity.Role;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.model.entity.UserCredential;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;
import nlu.fit.web.souvenirecommerce.util.PasswordUtil;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class AuthDAO extends AbstractHibernateIDAO<Long, User> implements IUserDAO {

    public AuthDAO() {
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

    @Override
    public Optional<User> createUser(String email, String password, String firstName, String lastName, String phone) {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()
                || firstName == null || firstName.isBlank()
                || lastName == null || lastName.isBlank()
                || phone == null || phone.isBlank()) {
            return Optional.empty();
        }

        Transaction transaction = null;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Long existingUsers = session.createQuery("""
                            select count(u.id) from User u
                            where lower(u.email) = lower(:email)
                            """, Long.class)
                    .setParameter("email", email.trim())
                    .uniqueResult();

            if (existingUsers != null && existingUsers > 0) {
                rollback(transaction);
                return Optional.empty();
            }

            String hashedPassword = PasswordUtil.hashPassword(password);
            User user = User.builder()
                    .email(email.trim().toLowerCase())
                    .password(hashedPassword)
                    .firstName(firstName.trim())
                    .lastName(lastName.trim())
                    .phone(phone.trim())
                    .gender(Gender.OTHER)
                    .avatarUrl("default-avatar.png")
                    .isActive(true)
                    .roles(new HashSet<>())
                    .build();

            UserCredential credential = UserCredential.builder()
                    .user(user)
                    .passwordHash(hashedPassword)
                    .emailVerified(true)
                    .build();
            user.setCredentials(credential);

            session.createQuery("""
                            select r from Role r
                            where lower(r.name) = lower(:name)
                            """, Role.class)
                    .setParameter("name", "User")
                    .uniqueResultOptional()
                    .ifPresent(role -> user.getRoles().add(role));

            session.persist(user);
            transaction.commit();
            return Optional.of(user);
        } catch (RuntimeException e) {
            rollback(transaction);
            throw e;
        }
    }
}
