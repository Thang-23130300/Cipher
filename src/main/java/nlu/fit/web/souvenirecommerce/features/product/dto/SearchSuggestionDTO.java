package nlu.fit.web.souvenirecommerce.features.product.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSuggestionDTO {
    private Long id;
    private String name;
    private Double originalPrice;
    private Double salePrice;
    private Double price;
    private String image;
}
