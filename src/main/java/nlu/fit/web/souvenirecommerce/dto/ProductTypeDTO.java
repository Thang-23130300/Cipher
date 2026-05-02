package nlu.fit.web.souvenirecommerce.dto;

import lombok.*;
import nlu.fit.web.souvenirecommerce.Enums.ProductSort;
import nlu.fit.web.souvenirecommerce.model.entity.Category;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTypeDTO {

    private Category category;
    private List<ProductCardDTO> products;

    private int currentPage;
    private int totalPages;
    private int totalProducts;
    private int totalReviews;

    private Integer minPrice;
    private Integer maxPrice;
    private Integer rating;
    private ProductSort sort;
    private String sortParam;
}
