package nlu.fit.web.souvenirecommerce.features.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSignedDataDTO {
    private Long orderId;
    private String buyerEmail;
    private String buyerPhone;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private String createdAt; // Định dạng yyyy-MM-dd HH:mm:ss cố định
    private List<OrderSignedItemDTO> items;
}