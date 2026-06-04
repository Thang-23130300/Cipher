package nlu.fit.web.souvenirecommerce.features.product.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.ProductSpecification;

import java.util.List;

public class ProductSpecificationRepository extends AbsBaseRepository<Long, ProductSpecification> {
    public ProductSpecificationRepository() {
        super(ProductSpecification.class);
    }

    public List<ProductSpecification> findByProductId(Long productId) {
        if (productId == null) {
            return List.of();
        }
        return getSession()
                .createQuery("""
                        from ProductSpecification s
                        where s.product.id = :productId
                        order by s.id asc
                        """, ProductSpecification.class)
                .setParameter("productId", productId)
                .getResultList();
    }
}
