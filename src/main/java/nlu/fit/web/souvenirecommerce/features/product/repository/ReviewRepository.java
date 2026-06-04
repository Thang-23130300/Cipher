package nlu.fit.web.souvenirecommerce.features.product.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewRepository extends AbsBaseRepository<Long, Review> {
    public ReviewRepository() {
        super(Review.class);
    }

    public long countByProductId(Long productId) {
        if (productId == null) {
            return 0;
        }
        Long count = getSession()
                .createQuery("select count(r.id) from Review r where r.product.id = :productId", Long.class)
                .setParameter("productId", productId)
                .uniqueResult();
        return count == null ? 0 : count;
    }

    public double avgRatingByProductId(Long productId) {
        if (productId == null) {
            return 0.0;
        }
        Double avg = getSession()
                .createQuery("select avg(r.rating) from Review r where r.product.id = :productId", Double.class)
                .setParameter("productId", productId)
                .uniqueResult();
        return avg == null ? 0.0 : Math.round(avg * 10.0) / 10.0;
    }

    public Map<Integer, Integer> countByRating(Long productId) {
        Map<Integer, Integer> result = new HashMap<>();
        for (int rating = 1; rating <= 5; rating++) {
            result.put(rating, 0);
        }
        if (productId == null) {
            return result;
        }
        List<Object[]> rows = getSession()
                .createQuery("""
                        select r.rating, count(r.id)
                        from Review r
                        where r.product.id = :productId
                        group by r.rating
                        """, Object[].class)
                .setParameter("productId", productId)
                .getResultList();
        for (Object[] row : rows) {
            result.put((Integer) row[0], ((Long) row[1]).intValue());
        }
        return result;
    }
}
