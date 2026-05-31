package nlu.fit.web.souvenirecommerce.features.product.service;

import nlu.fit.web.souvenirecommerce.features.product.dto.HeaderCategoryDTO;

import java.util.List;

public interface ICategoryService {

    List<HeaderCategoryDTO> getHeaderCategories();

    List<HeaderCategoryDTO> getTopSellingHeaderCategories(int limit);
}