package nlu.fit.web.souvenirecommerce.legacy.dao;

import nlu.fit.web.souvenirecommerce.common.base.IRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Category;

import java.util.List;

public interface ICategoryEntityIRepository extends IRepository<Long, Category> {

    List<Category> findHeaderCategories();

    List<Category> findTopSellingCategories(int limit);

    List<Long> findTopSellingCategoryIds(int limit);

    List<Category> findCategoriesNotIn(List<Long> usedIds);

    boolean insertCategory(Category category);

    boolean updateCategory(Category category);

    boolean deleteCategory(Long id);

    int countProductsByCategory(Long categoryId);
}