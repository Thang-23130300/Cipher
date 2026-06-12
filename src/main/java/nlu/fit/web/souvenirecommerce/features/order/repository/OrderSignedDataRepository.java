package nlu.fit.web.souvenirecommerce.features.order.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.OrderSignedData;

public class OrderSignedDataRepository extends AbsBaseRepository<Long, OrderSignedData> {
    public OrderSignedDataRepository() {
        super(OrderSignedData.class);
    }
}