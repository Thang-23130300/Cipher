package nlu.fit.web.souvenirecommerce.features.order.dto;

public class CheckoutException extends RuntimeException {
    public CheckoutException(String message) {
        super(message);
    }
}
