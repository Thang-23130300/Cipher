package nlu.fit.web.souvenirecommerce.service.impl;

import nlu.fit.web.souvenirecommerce.dao.ICategoryEntityIDAO;
import nlu.fit.web.souvenirecommerce.dao.impl.ICategoryIDAOImpl2;
import nlu.fit.web.souvenirecommerce.dto.HeaderCategoryDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.service.ICategoryService;

import java.util.List;

public class CategoryServiceImpl implements ICategoryService {

    private final ICategoryEntityIDAO categoryDAO;

    public CategoryServiceImpl() {
        this.categoryDAO = new ICategoryIDAOImpl2();
    }

    public CategoryServiceImpl(ICategoryEntityIDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    @Override
    public List<HeaderCategoryDTO> getHeaderCategories() {
        return categoryDAO.findHeaderCategories().stream().map(this::toHeaderCategoryDTO).toList();
    }

    @Override
    public List<HeaderCategoryDTO> getTopSellingHeaderCategories(int limit) {
        return categoryDAO.findTopSellingCategories(limit).stream().map(this::toHeaderCategoryDTO).toList();
    }

    private HeaderCategoryDTO toHeaderCategoryDTO(Category category) {
        return new HeaderCategoryDTO(category.getId(), category.getCategoryName(), category.getImage(), categoryDAO.countProductsByCategory(category.getId()));
    }
}