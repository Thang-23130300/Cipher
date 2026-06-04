package nlu.fit.web.souvenirecommerce.features.product.service;

import nlu.fit.web.souvenirecommerce.features.product.dto.CategoryAdminDTO;
import nlu.fit.web.souvenirecommerce.features.product.dto.HeaderCategoryDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    // Client-side operations
    List<HeaderCategoryDTO> getHeaderCategories();

    List<HeaderCategoryDTO> getTopSellingHeaderCategories(int limit);

    // Admin CRUD operations
    /**
     * Get all categories for admin panel
     * @return List of CategoryAdminDTO with product counts
     */
    List<CategoryAdminDTO> getAllCategoriesForAdmin();

    /**
     * Get category by ID
     * @param id Category ID
     * @return Optional containing CategoryAdminDTO
     */
    Optional<CategoryAdminDTO> getCategoryById(Long id);

    /**
     * Create new category
     * @param dto CategoryAdminDTO with category data
     * @return Created CategoryAdminDTO
     */
    CategoryAdminDTO createCategory(CategoryAdminDTO dto);

    /**
     * Update existing category
     * @param id Category ID
     * @param dto Updated CategoryAdminDTO
     * @return Updated CategoryAdminDTO
     */
    CategoryAdminDTO updateCategory(Long id, CategoryAdminDTO dto);

    /**
     * Delete category if no products are associated
     * @param id Category ID
     * @return true if deletion successful, false if category has products
     */
    boolean deleteCategory(Long id);

    /**
     * Check if category name already exists
     * @param categoryName Category name
     * @return true if name exists, false otherwise
     */
    boolean categoryNameExists(String categoryName);

    /**
     * Check if category can be deleted
     * @param id Category ID
     * @return true if category has no products, false otherwise
     */
    boolean canDeleteCategory(Long id);
}