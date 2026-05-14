package nlu.fit.web.souvenirecommerce.dao.impl;

import nlu.fit.web.souvenirecommerce.dao.IPermissionEntityIDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Permission;
import nlu.fit.web.souvenirecommerce.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class IPermissionIDAOImpl extends AbstractHibernateIDAO<Long, Permission> implements IPermissionEntityIDAO {

    public IPermissionIDAOImpl() {
        super(Permission.class);
    }

    @Override
    public List<Permission> findAll() {
        String hql = "select p from Permission p order by p.resource, p.action";

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Permission.class).getResultList();
        }
    }

    @Override
    public Optional<Permission> findByResourceAndAction(String resource, String action) {
        if (resource == null || resource.isBlank() || action == null || action.isBlank()) {
            return Optional.empty();
        }

        String hql = """
                select p from Permission p
                where p.resource = :resource
                  and p.action = :action
                """;

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Permission.class)
                    .setParameter("resource", resource.trim())
                    .setParameter("action", action.trim())
                    .uniqueResultOptional();
        }
    }
}
