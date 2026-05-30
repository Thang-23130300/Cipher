package nlu.fit.web.souvenirecommerce.features.product.dto;

import lombok.*;
import nlu.fit.web.souvenirecommerce.legacy.model.*;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.model.entity.Product;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDTO {

    private Product product;
    private Category category;
    private Promotion promotion;

    private Double discountedPrice;

    private List<ProductSpecification> specifications;

    private double avgRating;
    private int totalReviews;
    private Map<Integer, Integer> ratingCount;

    private List<ProductCardDTO> relatedProductCards;
}
