package nlu.fit.web.souvenirecommerce.service;

import nlu.fit.web.souvenirecommerce.dto.HeaderCategoryDTO;

import java.util.List;

public interface ICategoryService {

    List<HeaderCategoryDTO> getHeaderCategories();

    List<HeaderCategoryDTO> getTopSellingHeaderCategories(int limit);
}