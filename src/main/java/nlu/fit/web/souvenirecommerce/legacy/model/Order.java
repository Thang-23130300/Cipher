package nlu.fit.web.souvenirecommerce.legacy.model;

import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private int id;
    private int userId;
    private String customerName;
    private String customerEmail;
    private Date orderDate;
    private double totalAmount;
    private String status;
    private String shippingAddress;
    private String paymentMethod;
    private String signatureStatus;
    private String verifyStatus;
    private Date signedAt;
    private List<OrderItem> items;
    private String paymentUrl;
    private String paymentStatus;

    public String getOrderCode() {
        Date codeDate = orderDate == null ? new Date() : orderDate;
        return "ORD-" + new SimpleDateFormat("yyyyMMdd").format(codeDate)
                + "-" + String.format("%05d", id);
    }

    public Date getCreatedAt() {
        return orderDate;
    }
}
