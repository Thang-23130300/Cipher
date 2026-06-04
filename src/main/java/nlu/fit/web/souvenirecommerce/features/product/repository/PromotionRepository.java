package nlu.fit.web.souvenirecommerce.features.product.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Promotion;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PromotionRepository extends AbsBaseRepository<Long, Promotion> {
    public PromotionRepository() {
        super(Promotion.class);
    }

    public Optional<Promotion> findBestActiveByProductId(Long productId) {
        if (productId == null) {
            return Optional.empty();
        }
        LocalDateTime now = LocalDateTime.now();
        return getSession()
                .createQuery("""
                        from Promotion p
                        where p.product.id = :productId
                          and (p.startDate is null or p.startDate <= :now)
                          and (p.endDate is null or p.endDate >= :now)
                        order by p.discountPercent desc
                        """, Promotion.class)
                .setParameter("productId", productId)
                .setParameter("now", now)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    public Map<Long, Promotion> findBestActiveByProductIds(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }
        LocalDateTime now = LocalDateTime.now();
        List<Promotion> promotions = getSession()
                .createQuery("""
                        select p
                        from Promotion p
                        join fetch p.product product
                        where product.id in (:productIds)
                          and (p.startDate is null or p.startDate <= :now)
                          and (p.endDate is null or p.endDate >= :now)
                        order by product.id asc, p.discountPercent desc
                        """, Promotion.class)
                .setParameter("productIds", productIds)
                .setParameter("now", now)
                .getResultList();

        Map<Long, Promotion> result = new HashMap<>();
        for (Promotion promotion : promotions) {
            result.putIfAbsent(promotion.getProduct().getId(), promotion);
        }
        return result;
    }
}
