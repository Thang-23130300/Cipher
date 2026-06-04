package nlu.fit.web.souvenirecommerce.features.order.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.OrderStatus;

import java.util.Optional;

public class OrderStatusRepository extends AbsBaseRepository<Long, OrderStatus> {
    public OrderStatusRepository() {
        super(OrderStatus.class);
    }

    public Optional<OrderStatus> findByDescription(String description) {
        if (description == null || description.isBlank()) {
            return Optional.empty();
        }
        return getSession()
                .createQuery("from OrderStatus s where s.description = :description", OrderStatus.class)
                .setParameter("description", description)
                .uniqueResultOptional();
    }
}
