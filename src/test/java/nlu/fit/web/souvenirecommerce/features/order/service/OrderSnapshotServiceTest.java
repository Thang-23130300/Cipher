package nlu.fit.web.souvenirecommerce.features.order.service;

import nlu.fit.web.souvenirecommerce.model.entity.Address;
import nlu.fit.web.souvenirecommerce.model.entity.Order;
import nlu.fit.web.souvenirecommerce.model.entity.OrderItem;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class OrderSnapshotServiceTest {

    private final OrderSnapshotService snapshotService = new OrderSnapshotService();
    private final HashService hashService = new HashService();

    @Test
    public void testHashLengthAndStability() {
        // Mock User
        User user = User.builder()
                .id(1L)
                .email("buyer@gmail.com")
                .phone("0123456789")
                .build();

        // Mock Address
        Address address = Address.builder()
                .addressDetail("123 Street")
                .ward("Ward 1")
                .district("District 2")
                .city("HCM City")
                .province("HCM Province")
                .receiverName("Receiver Name")
                .receiverPhone("0987654321")
                .build();

        // Mock Products
        Product p1 = Product.builder().id(101L).name("Product A").build();
        Product p2 = Product.builder().id(102L).name("Product B").build();

        // Mock Order
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .address(address)
                .totalAmount(new BigDecimal("150000"))
                .orderDate(LocalDateTime.of(2026, 6, 12, 10, 0, 0))
                .items(new ArrayList<>())
                .build();

        // Mock Items (Chèn không theo thứ tự ID để test tính năng tự sắp xếp của SnapshotService)
        OrderItem item2 = OrderItem.builder()
                .product(p2)
                .productName("Product B")
                .quantity(1)
                .priceAtPurchase(new BigDecimal("100000"))
                .build();
        OrderItem item1 = OrderItem.builder()
                .product(p1)
                .productName("Product A")
                .quantity(1)
                .priceAtPurchase(new BigDecimal("50000"))
                .build();

        order.addItem(item2);
        order.addItem(item1);

        // 1. Kiểm tra mã băm sinh ra và độ dài (phải đúng 64 ký tự hex)
        String json1 = snapshotService.createSnapshotJson(order);
        String hash1 = hashService.sha256Hex(json1);

        assertNotNull(hash1);
        assertEquals(64, hash1.length(), "Mã băm phải có độ dài chính xác là 64 ký tự hex");

        // 2. Kiểm tra tính ổn định: Cùng một đơn hàng, băm nhiều lần phải ra kết quả giống hệt nhau
        String json2 = snapshotService.createSnapshotJson(order);
        String hash2 = hashService.sha256Hex(json2);
        assertEquals(hash1, hash2, "Hai lần băm cùng một đơn hàng phải cho ra mã băm trùng khớp nhau");

        // 3. Kiểm tra tính thay đổi: Thay đổi thuộc tính đơn hàng (ví dụ: tổng tiền) thì mã băm phải thay đổi
        order.setTotalAmount(new BigDecimal("160000")); // thay đổi tổng tiền
        String json3 = snapshotService.createSnapshotJson(order);
        String hash3 = hashService.sha256Hex(json3);
        assertNotEquals(hash1, hash3, "Khi thay đổi thông tin đơn hàng, mã băm sinh ra phải thay đổi");
    }
}