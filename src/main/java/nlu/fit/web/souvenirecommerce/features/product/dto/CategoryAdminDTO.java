package nlu.fit.web.souvenirecommerce.features.product.dto;

import lombok.*;
import nlu.fit.web.souvenirecommerce.model.entity.Category;

/**
 * DTO for Category admin operations
 * Used for transferring category data in admin panel (Create, Read, Update, Delete)
 * 
 * @author Development Team
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAdminDTO {
    private Long id;
    private String categoryName;
    private String image;
    private Integer productCount;
    private Boolean canDelete;

    /**
     * Convert Category entity to CategoryAdminDTO
     * @param category Category entity
     * @param productCount Number of products in category
     * @return CategoryAdminDTO with populated data
     */
    public static CategoryAdminDTO fromEntity(Category category, Integer productCount) {
        return CategoryAdminDTO.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .image(category.getImage())
                .productCount(productCount != null ? productCount : 0)
                .canDelete(productCount != null && productCount == 0)
                .build();
    }

    /**
     * Convert to Category entity for persistence
     * @return Category entity
     */
    public Category toEntity() {
        return Category.builder()
                .id(this.id)
                .categoryName(this.categoryName)
                .image(this.image)
                .build();
    }
}
