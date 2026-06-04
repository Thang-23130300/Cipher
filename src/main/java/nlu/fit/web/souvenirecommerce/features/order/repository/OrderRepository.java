package nlu.fit.web.souvenirecommerce.features.order.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Order;

import java.util.List;

public class OrderRepository extends AbsBaseRepository<Long, Order> {
    public OrderRepository() {
        super(Order.class);
    }

    @Override
    public List<Order> findAll() {
        return getSession()
                .createQuery("from CustomerOrder o order by o.id desc", Order.class)
                .getResultList();
    }

    public List<Order> findByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return getSession()
                .createQuery("""
                        select distinct o
                        from CustomerOrder o
                        left join fetch o.status
                        where o.user.id = :userId
                        order by o.id desc
                        """, Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
