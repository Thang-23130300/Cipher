package nlu.fit.web.souvenirecommerce.features.order.repository;

import nlu.fit.web.souvenirecommerce.common.base.AbsBaseRepository;
import nlu.fit.web.souvenirecommerce.model.entity.PaymentTransaction;

public class PaymentTransactionRepository extends AbsBaseRepository<Long, PaymentTransaction> {
    public PaymentTransactionRepository() {
        super(PaymentTransaction.class);
    }
}
