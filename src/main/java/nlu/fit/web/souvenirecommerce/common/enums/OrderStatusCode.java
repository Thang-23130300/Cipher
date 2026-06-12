package nlu.fit.web.souvenirecommerce.common.enums;

public enum OrderStatusCode {
    WAITING_SIGNATURE("Chờ ký số"),
    PENDING("Đang xử lý"),
    AWAITING_PAYMENT("Chờ thanh toán"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy"),
    COMPLETED("Hoàn thành");

    private final String description;

    OrderStatusCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
