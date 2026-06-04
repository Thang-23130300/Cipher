package nlu.fit.web.souvenirecommerce.features.order.payment;

import nlu.fit.web.souvenirecommerce.common.enums.PaymentMethod;
import nlu.fit.web.souvenirecommerce.features.order.dto.PaymentPreparation;
import nlu.fit.web.souvenirecommerce.model.entity.Order;

public interface PaymentGateway {
    PaymentMethod method();

    PaymentPreparation prepare(Order order);
}
