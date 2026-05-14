package nlu.fit.web.souvenirecommerce.dao.impl;

import nlu.fit.web.souvenirecommerce.dao.ICategoryEntityIDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class ICategoryIDAOImpl2 extends AbstractHibernateIDAO<Long, Category> implements ICategoryEntityIDAO {

    public ICategoryIDAOImpl2() {
        super(Category.class);
    }

    @Override
    public List<Category> findAll() {
        String hql = """
                select distinct c from Category c
                left join fetch c.products
                order by c.id
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Category.class).getResultList();
        }
    }

    @Override
    public Optional<Category> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        String hql = """
                select distinct c from Category c
                left join fetch c.products
                where c.id = :id
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Category.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }
}
