package nlu.fit.web.souvenirecommerce.features.product.service.impl;

import nlu.fit.web.souvenirecommerce.legacy.dao.ICategoryEntityIRepository;
import nlu.fit.web.souvenirecommerce.legacy.dao.impl.ICategoryIRepositoryImpl2;
import nlu.fit.web.souvenirecommerce.features.product.dto.HeaderCategoryDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.features.product.service.ICategoryService;

import java.util.List;

public class CategoryServiceImpl implements ICategoryService {

    private final ICategoryEntityIRepository categoryDAO;

    public CategoryServiceImpl() {
        this.categoryDAO = new ICategoryIRepositoryImpl2();
    }

    public CategoryServiceImpl(ICategoryEntityIRepository categoryDAO) {
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