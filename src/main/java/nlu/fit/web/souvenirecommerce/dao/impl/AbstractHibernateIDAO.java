package nlu.fit.web.souvenirecommerce.dao.impl;

import nlu.fit.web.souvenirecommerce.dao.IDAO;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public abstract class AbstractHibernateIDAO<K, T> implements IDAO<K, T> {
    private final Class<T> entityClass;

    protected AbstractHibernateIDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Optional<T> save(T entity) {
        Transaction transaction = null;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return Optional.of(entity);
        } catch (RuntimeException e) {
            rollback(transaction);
            throw e;
        }
    }

    @Override
    public void update(T entity) {
        Transaction transaction = null;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            rollback(transaction);
            throw e;
        }
    }

    @Override
    public List<T> findAll() {
        String hql = "from " + entityClass.getSimpleName();

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, entityClass).getResultList();
        }
    }

    @Override
    public Optional<T> findById(K id) {
        if (id == null) {
            return Optional.empty();
        }

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(entityClass, id));
        }
    }

    @Override
    public void delete(K id) {
        if (id == null) {
            return;
        }

        Transaction transaction = null;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T entity = session.find(entityClass, id);

            if (entity != null) {
                session.remove(entity);
            }

            transaction.commit();
        } catch (RuntimeException e) {
            rollback(transaction);
            throw e;
        }
    }

    protected void rollback(Transaction transaction) {
        if (transaction == null) {
            return;
        }

        try {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } catch (RuntimeException e) {
            // Ignore rollback failures when the JDBC connection is already closed or the transaction is no longer usable.
        }
    }
}
