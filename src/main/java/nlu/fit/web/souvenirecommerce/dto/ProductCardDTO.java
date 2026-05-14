package nlu.fit.web.souvenirecommerce.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCardDTO {
    private Long id;
    private String name;
    private String image;

    private double price;
    private Double originalPrice;
    private Double discountedPrice;
    private Integer discountPercent;

    private int totalSold;
    private double avgRating;
    private int reviewCount;
    public boolean hasDiscount() {
        return discountPercent != null
                && discountPercent > 0
                && discountedPrice != null;
    }
}
