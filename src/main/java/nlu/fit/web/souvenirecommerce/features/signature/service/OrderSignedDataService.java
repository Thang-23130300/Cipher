package nlu.fit.web.souvenirecommerce.features.signature.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nlu.fit.web.souvenirecommerce.features.signature.dao.OrderSignedDataDAO;
import nlu.fit.web.souvenirecommerce.features.signature.dto.OrderSignedDataDTO;
import nlu.fit.web.souvenirecommerce.features.signature.dto.OrderSignedItemDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Address;
import nlu.fit.web.souvenirecommerce.model.entity.Order;
import nlu.fit.web.souvenirecommerce.model.entity.OrderItem;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class OrderSignedDataService {
    private final OrderSignedDataDAO orderSignedDataDAO = new OrderSignedDataDAO();
    private final HashService hashService = new HashService();

    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    public void createForOrder(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order không hợp lệ để tạo dữ liệu ký");
        }

        OrderSignedDataDTO dto = buildSignedData(order);
        String signedDataJson = gson.toJson(dto);
        String hashValue = hashService.sha256Hex(signedDataJson);

        orderSignedDataDAO.saveOrUpdate(order.getId(), signedDataJson, hashValue);
    }

    private OrderSignedDataDTO buildSignedData(Order order) {
        Address address = order.getAddress();
        User user = order.getUser();

        List<OrderSignedItemDTO> items = order.getItems()
                .stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getId()))
                .map(this::toSignedItem)
                .toList();

        return OrderSignedDataDTO.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .userId(user == null ? null : user.getId())
                .buyerEmail(user == null ? null : user.getEmail())
                .buyerName(user == null ? null : user.getFullName())
                .addressId(address == null ? null : address.getId())
                .receiverName(address == null ? null : address.getReceiverName())
                .receiverPhone(address == null ? null : address.getReceiverPhone())
                .shippingAddress(buildAddressText(address))
                .items(items)
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate() == null
                        ? null
                        : order.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    private OrderSignedItemDTO toSignedItem(OrderItem item) {
        return OrderSignedItemDTO.builder()
                .productId(item.getProduct() == null ? null : item.getProduct().getId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subTotal(item.getSubTotal())
                .build();
    }

    private String buildAddressText(Address address) {
        if (address == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        appendPart(builder, address.getAddressDetail());
        appendPart(builder, address.getWard());
        appendPart(builder, address.getDistrict());
        appendPart(builder, address.getProvince());
        appendPart(builder, address.getCity());

        return builder.toString();
    }

    private void appendPart(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        if (!builder.isEmpty()) {
            builder.append(", ");
        }

        builder.append(value.trim());
    }
}