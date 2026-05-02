package nlu.fit.web.souvenirecommerce.dao;

import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.model.entity.Product;

import java.util.List;

public class HomeDAO {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public List<Category> getBannerCategories(int limit) {
        return categoryDAO.getTopSellingCategories(limit);
    }

    public List<Category> getTopCategoriesWithProducts(int categoryLimit, int productLimit) {

        List<Category> categories =
                categoryDAO.getTopSellingCategories(categoryLimit);

        for (Category category : categories) {
            List<Product> products =
                    productDAO.getTopSellingByCategory(category.getId(), productLimit);

            category.setProducts(products);
        }
        return categories;
    }

    public List<Category> getExtensionCategories(int limit) {
        List<Integer> topIds = categoryDAO.getTopSellingCategoryIds(5);
        List<Category> remain = categoryDAO.getCategoriesNotIn(topIds);
        return remain.subList(0, Math.min(limit, remain.size()));
    }

}
