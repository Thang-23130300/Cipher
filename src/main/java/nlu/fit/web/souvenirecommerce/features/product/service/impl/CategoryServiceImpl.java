package nlu.fit.web.souvenirecommerce.features.product.service.impl;

import nlu.fit.web.souvenirecommerce.features.product.dto.CategoryAdminDTO;
import nlu.fit.web.souvenirecommerce.features.product.dto.HeaderCategoryDTO;
import nlu.fit.web.souvenirecommerce.features.product.repository.CategoryRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.features.product.service.ICategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ICategoryService
 * Handles both client-side and admin CRUD operations for categories
 * Uses CategoryRepository for data access with Hibernate ORM
 * 
 * @author Development Team
 * @version 1.0
 */
public class CategoryServiceImpl implements ICategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);
    
    private final CategoryRepository categoryRepository;
    // Keep legacy DAO for backward compatibility with client-side operations
    private final nlu.fit.web.souvenirecommerce.legacy.dao.ICategoryEntityIRepository legacyDAO;

    public CategoryServiceImpl() {
        this.categoryRepository = new CategoryRepository();
        this.legacyDAO = new nlu.fit.web.souvenirecommerce.legacy.dao.impl.ICategoryIRepositoryImpl2();
    }

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.legacyDAO = new nlu.fit.web.souvenirecommerce.legacy.dao.impl.ICategoryIRepositoryImpl2();
    }

    // ==================== CLIENT-SIDE OPERATIONS ====================

    @Override
    public List<HeaderCategoryDTO> getHeaderCategories() {
        try {
            return legacyDAO.findHeaderCategories().stream()
                    .map(this::toHeaderCategoryDTO)
                    .toList();
        } catch (Exception e) {
            log.error("Service getHeaderCategories failed", e);
            throw new RuntimeException("Failed to fetch header categories", e);
        }
    }

    @Override
    public List<HeaderCategoryDTO> getTopSellingHeaderCategories(int limit) {
        try {
            return legacyDAO.findTopSellingCategories(limit).stream()
                    .map(this::toHeaderCategoryDTO)
                    .toList();
        } catch (Exception e) {
            log.error("Service getTopSellingHeaderCategories failed with limit={}", limit, e);
            throw new RuntimeException("Failed to fetch top selling categories", e);
        }
    }

    // ==================== ADMIN CRUD OPERATIONS ====================

    @Override
    public List<CategoryAdminDTO> getAllCategoriesForAdmin() {
        try {
            log.info("Fetching all categories for admin panel");
            return categoryRepository.findAll().stream()
                    .map(this::toCategoryAdminDTO)
                    .toList();
        } catch (Exception e) {
            log.error("Service getAllCategoriesForAdmin failed", e);
            throw new RuntimeException("Failed to fetch categories", e);
        }
    }

    @Override
    public Optional<CategoryAdminDTO> getCategoryById(Long id) {
        try {
            log.info("Fetching category by id={}", id);
            return categoryRepository.findById(id)
                    .map(this::toCategoryAdminDTO);
        } catch (Exception e) {
            log.error("Service getCategoryById failed for id={}", id, e);
            throw new RuntimeException("Failed to fetch category", e);
        }
    }

    @Override
    public CategoryAdminDTO createCategory(CategoryAdminDTO dto) {
        try {
            // Validate input
            if (dto.getCategoryName() == null || dto.getCategoryName().trim().isEmpty()) {
                throw new IllegalArgumentException("Category name is required");
            }

            // Check for duplicate name
            if (categoryNameExists(dto.getCategoryName())) {
                throw new IllegalArgumentException("Category name already exists");
            }

            log.info("Creating new category with name={}", dto.getCategoryName());
            
            Category category = new Category();
            category.setCategoryName(dto.getCategoryName().trim());
            category.setImage(dto.getImage());

            Optional<Category> saved = categoryRepository.save(category);
            return saved.map(this::toCategoryAdminDTO)
                    .orElseThrow(() -> new RuntimeException("Failed to save category"));
        } catch (IllegalArgumentException e) {
            log.warn("Service createCategory validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service createCategory failed", e);
            throw new RuntimeException("Failed to create category", e);
        }
    }

    @Override
    public CategoryAdminDTO updateCategory(Long id, CategoryAdminDTO dto) {
        try {
            // Validate input
            if (dto.getCategoryName() == null || dto.getCategoryName().trim().isEmpty()) {
                throw new IllegalArgumentException("Category name is required");
            }

            log.info("Updating category id={}", id);
            
            Category existing = categoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));

            // Check for duplicate name (excluding current category)
            Optional<Category> duplicate = categoryRepository.findByName(dto.getCategoryName());
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException("Category name already exists");
            }

            existing.setCategoryName(dto.getCategoryName().trim());
            existing.setImage(dto.getImage());

            categoryRepository.update(existing);
            return toCategoryAdminDTO(existing);
        } catch (IllegalArgumentException e) {
            log.warn("Service updateCategory validation failed for id={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service updateCategory failed for id={}", id, e);
            throw new RuntimeException("Failed to update category", e);
        }
    }

    @Override
    public boolean deleteCategory(Long id) {
        try {
            log.info("Attempting to delete category id={}", id);
            
            // Check if category exists
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));

            // Check if category has products
            if (!categoryRepository.canDeleteCategory(id)) {
                long productCount = categoryRepository.countProductsByCategory(id);
                throw new IllegalArgumentException("Cannot delete category with " + productCount + " products");
            }

            categoryRepository.delete(id);
            log.info("Category id={} deleted successfully", id);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Service deleteCategory failed for id={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Service deleteCategory failed for id={}", id, e);
            throw new RuntimeException("Failed to delete category", e);
        }
    }

    @Override
    public boolean categoryNameExists(String categoryName) {
        try {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                return false;
            }
            return categoryRepository.findByName(categoryName.trim()).isPresent();
        } catch (Exception e) {
            log.error("Service categoryNameExists failed for categoryName={}", categoryName, e);
            throw new RuntimeException("Failed to check category name", e);
        }
    }

    @Override
    public boolean canDeleteCategory(Long id) {
        try {
            return categoryRepository.canDeleteCategory(id);
        } catch (Exception e) {
            log.error("Service canDeleteCategory failed for id={}", id, e);
            throw new RuntimeException("Failed to check delete eligibility", e);
        }
    }

    // ==================== HELPER METHODS ====================

    private HeaderCategoryDTO toHeaderCategoryDTO(Category category) {
        return new HeaderCategoryDTO(
                category.getId(),
                category.getCategoryName(),
                category.getImage(),
                legacyDAO.countProductsByCategory(category.getId())
        );
    }

    private CategoryAdminDTO toCategoryAdminDTO(Category category) {
        long productCount = categoryRepository.countProductsByCategory(category.getId());
        return CategoryAdminDTO.fromEntity(category, (int) productCount);
    }
}