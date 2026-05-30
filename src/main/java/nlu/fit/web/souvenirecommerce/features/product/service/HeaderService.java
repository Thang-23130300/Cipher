package nlu.fit.web.souvenirecommerce.features.product.service;

import nlu.fit.web.souvenirecommerce.legacy.dao.CategoryDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Category;

import java.util.List;

public class HeaderService {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    public List<Category> getTopCategories(int limit) {
        return categoryDAO.getTopSellingCategories(limit);
    }
}
