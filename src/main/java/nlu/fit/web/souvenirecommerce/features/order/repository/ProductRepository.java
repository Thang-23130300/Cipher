package nlu.fit.web.souvenirecommerce.features.order.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Product;

public class ProductRepository extends AbsBaseRepository<Long, Product> {
    public ProductRepository() {
        super(Product.class);
    }
}
