package nlu.fit.web.souvenirecommerce.dao.impl;

import nlu.fit.web.souvenirecommerce.dao.IProductEntityIDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class ProductIDAOImpl2 extends AbstractHibernateIDAO<Long, Product> implements IProductEntityIDAO {

    public ProductIDAOImpl2() {
        super(Product.class);
    }

    @Override
    public List<Product> findAll() {
        String hql = """
                select p from Product p
                left join fetch p.category
                order by p.id desc
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Product.class).getResultList();
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        String hql = """
                select p from Product p
                left join fetch p.category
                where p.id = :id
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Product.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return List.of();
        }

        String hql = """
                select p from Product p
                left join fetch p.category c
                where c.id = :categoryId
                order by p.id desc
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Product.class)
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        }
    }
}
