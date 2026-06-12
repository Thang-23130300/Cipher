package nlu.fit.web.souvenirecommerce.features.signature.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSignedDataDTO {
    private Long orderId;
    private String orderCode;

    private Long userId;
    private String buyerEmail;
    private String buyerName;

    private Long addressId;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;

    private List<OrderSignedItemDTO> items;

    private BigDecimal totalAmount;
    private String orderDate;
}