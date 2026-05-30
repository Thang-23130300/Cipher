package nlu.fit.web.souvenirecommerce.legacy.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSpecification  {
    private int id;
    private Long productId;
    private String specName;
    private String specValue;
}
