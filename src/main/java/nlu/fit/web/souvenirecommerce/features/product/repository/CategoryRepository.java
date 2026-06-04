package nlu.fit.web.souvenirecommerce.features.product.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Category Repository - Hibernate-based CRUD operations for Category entity
 * Follows the Feature-Based Architecture with Session-per-request pattern
 * 
 * @author Development Team
 * @version 1.0
 */
public class CategoryRepository extends AbsBaseRepository<Long, Category> {
    private static final Logger log = LoggerFactory.getLogger(CategoryRepository.class);

    public CategoryRepository() {
        super(Category.class);
    }

    /**
     * Find all categories that have at least one product
     * @return List of categories with products
     */
    public List<Category> findCategoriesWithProducts() {
        try {
            String hql = "SELECT DISTINCT c FROM Category c LEFT JOIN c.products p WHERE p IS NOT NULL";
            return getSession().createQuery(hql, Category.class).getResultList();
        } catch (RuntimeException e) {
            log.error("Repository findCategoriesWithProducts failed", e);
            throw e;
        }
    }

    /**
     * Find category by name
     * @param categoryName Name of the category
     * @return Optional containing category if found
     */
    public Optional<Category> findByName(String categoryName) {
        try {
            String hql = "FROM Category WHERE categoryName = :name";
            var result = getSession().createQuery(hql, Category.class)
                    .setParameter("name", categoryName)
                    .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } catch (RuntimeException e) {
            log.error("Repository findByName failed for categoryName={}", categoryName, e);
            throw e;
        }
    }

    /**
     * Count products in a category
     * @param categoryId Category ID
     * @return Number of products in the category
     */
    public long countProductsByCategory(Long categoryId) {
        try {
            String hql = "SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId";
            var result = getSession().createQuery(hql, Long.class)
                    .setParameter("categoryId", categoryId)
                    .getSingleResult();
            return result != null ? result : 0;
        } catch (RuntimeException e) {
            log.error("Repository countProductsByCategory failed for categoryId={}", categoryId, e);
            throw e;
        }
    }

    /**
     * Check if category can be deleted (no products associated)
     * @param categoryId Category ID
     * @return true if category has no products, false otherwise
     */
    public boolean canDeleteCategory(Long categoryId) {
        return countProductsByCategory(categoryId) == 0;
    }

    /**
     * Find top selling categories
     * @param limit Number of top categories to fetch
     * @return List of top selling categories
     */
    public List<Category> findTopSellingCategories(int limit) {
        try {
            String hql = "SELECT c FROM Category c LEFT JOIN c.products p GROUP BY c.id ORDER BY COUNT(p) DESC";
            return getSession().createQuery(hql, Category.class)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (RuntimeException e) {
            log.error("Repository findTopSellingCategories failed with limit={}", limit, e);
            throw e;
        }
    }
}
