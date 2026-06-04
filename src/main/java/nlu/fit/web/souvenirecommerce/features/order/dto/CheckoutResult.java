package nlu.fit.web.souvenirecommerce.features.order.dto;

import lombok.Builder;
import lombok.Getter;
import nlu.fit.web.souvenirecommerce.model.entity.Order;

@Getter
@Builder
public class CheckoutResult {
    private final Order order;
    private final String orderCode;
    private final String paymentUrl;
    private final String qrPayload;

    public boolean requiresExternalPayment() {
        return paymentUrl != null && !paymentUrl.isBlank();
    }
}
