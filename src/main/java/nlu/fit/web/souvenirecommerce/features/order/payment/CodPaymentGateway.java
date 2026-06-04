package nlu.fit.web.souvenirecommerce.features.order.payment;

import nlu.fit.web.souvenirecommerce.common.enums.PaymentMethod;
import nlu.fit.web.souvenirecommerce.common.enums.PaymentProvider;
import nlu.fit.web.souvenirecommerce.common.enums.PaymentStatus;
import nlu.fit.web.souvenirecommerce.features.order.dto.PaymentPreparation;
import nlu.fit.web.souvenirecommerce.model.entity.Order;

public class CodPaymentGateway implements PaymentGateway {
    @Override
    public PaymentMethod method() {
        return PaymentMethod.COD;
    }

    @Override
    public PaymentPreparation prepare(Order order) {
        return PaymentPreparation.builder()
                .provider(PaymentProvider.COD)
                .status(PaymentStatus.NOT_REQUIRED)
                .build();
    }
}
