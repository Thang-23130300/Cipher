package nlu.fit.web.souvenirecommerce.features.order.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nlu.fit.web.souvenirecommerce.features.order.dto.OrderSignedDataDTO;
import nlu.fit.web.souvenirecommerce.features.order.dto.OrderSignedItemDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Order;
import nlu.fit.web.souvenirecommerce.model.entity.OrderItem;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class OrderSnapshotService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public String createSnapshotJson(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Đơn hàng không được null");
        }

        // 1. Chuyển đổi thông tin người mua và thông tin chung của đơn hàng
        OrderSignedDataDTO dto = OrderSignedDataDTO.builder()
                .orderId(order.getId())
                .buyerEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                .buyerPhone(order.getUser() != null ? order.getUser().getPhone() : null)
                .receiverName(order.getAddress() != null ? order.getAddress().getReceiverName() : null)
                .receiverPhone(order.getAddress() != null ? order.getAddress().getReceiverPhone() : null)
                .shippingAddress(buildFullAddress(order))
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getOrderDate() != null ? order.getOrderDate().format(DATE_FORMATTER) : null)
                .items(new ArrayList<>())
                .build();

        // 2. Chuyển đổi danh sách chi tiết các sản phẩm đặt mua
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                OrderSignedItemDTO itemDto = OrderSignedItemDTO.builder()
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .build();
                dto.getItems().add(itemDto);
            }
        }

        // 3. Sắp xếp danh sách sản phẩm theo Product ID tăng dần để đảm bảo chuỗi JSON luôn nhất quán
        dto.getItems().sort(Comparator.comparing(OrderSignedItemDTO::getProductId));

        // 4. Chuyển đổi sang chuỗi JSON tĩnh
        return gson.toJson(dto);
    }

    private String buildFullAddress(Order order) {
        if (order.getAddress() == null) {
            return null;
        }
        var addr = order.getAddress();
        StringBuilder sb = new StringBuilder();
        if (addr.getAddressDetail() != null) sb.append(addr.getAddressDetail()).append(", ");
        if (addr.getWard() != null) sb.append(addr.getWard()).append(", ");
        if (addr.getDistrict() != null) sb.append(addr.getDistrict()).append(", ");
        if (addr.getProvince() != null) sb.append(addr.getProvince());
        return sb.toString().trim();
    }
}