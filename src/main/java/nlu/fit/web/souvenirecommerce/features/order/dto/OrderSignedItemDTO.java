package nlu.fit.web.souvenirecommerce.features.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSignedItemDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal priceAtPurchase;
}