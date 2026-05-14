package nlu.fit.web.souvenirecommerce.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private int orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private int quantity;
    private double priceAtPurchase;
    private double subTotal;
}
