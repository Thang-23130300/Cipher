package nlu.fit.web.souvenirecommerce.features.signature.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.features.signature.dao.OrderAuditLogDAO;
import nlu.fit.web.souvenirecommerce.features.signature.dto.OrderSignedDataDTO;
import nlu.fit.web.souvenirecommerce.features.signature.dto.OrderSignedItemDTO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.KeyRiskService;
import nlu.fit.web.souvenirecommerce.model.entity.Address;
import nlu.fit.web.souvenirecommerce.model.entity.Order;
import nlu.fit.web.souvenirecommerce.model.entity.OrderItem;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OrderAuditService {
    private static final String ACTION_SIGNED_DATA_CHANGED = "SIGNED_DATA_CHANGED";
    private static final String DATA_TAMPERED = "DATA_TAMPERED";

    private final SignatureVerifyService signatureVerifyService = new SignatureVerifyService();
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();
    private final KeyRiskService keyRiskService = new KeyRiskService();
    private final OrderAuditLogDAO orderAuditLogDAO = new OrderAuditLogDAO();

    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    public String auditOrderSignature(Long orderId) {
        return auditOrderSignature(orderId, null, "SYSTEM");
    }

    public String auditOrderSignature(Long orderId, Long actorId, String actorRole) {
        if (orderId == null) {
            return "WAITING_SIGNATURE";
        }

        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();

            Object[] sigRow = findSignatureRow(session, orderId);
            if (sigRow == null) {
                return getCurrentSignatureStatus(session, orderId);
            }

            Long keyId = ((Number) sigRow[0]).longValue();
            String signatureValue = (String) sigRow[1];
            LocalDateTime signedAt = toLocalDateTime(sigRow[2]);

            Optional<UserKeyDTO> keyDtoOpt = userKeyDAO.findById(keyId);
            if (keyDtoOpt.isEmpty()) {
                return "SIGNATURE_INVALID";
            }

            Object[] signedDataRow = findSignedDataRow(session, orderId);
            if (signedDataRow == null) {
                return "SIGNATURE_INVALID";
            }

            String storedHash = (String) signedDataRow[0];
            String storedJson = (String) signedDataRow[1];

            Order order = session.find(Order.class, orderId);
            if (order == null) {
                return "SIGNATURE_INVALID";
            }

            OrderSignedDataDTO storedDto = gson.fromJson(storedJson, OrderSignedDataDTO.class);
            List<FieldDiff> diffs = findSignedDataDiffs(storedDto, order);

            if (!diffs.isEmpty()) {
                writeTamperLogs(orderId, actorId, actorRole, diffs);
                markOrderDataTampered(session, orderId);
                return DATA_TAMPERED;
            }

            boolean sigValid;
            try {
                sigValid = signatureVerifyService.verify(storedHash, signatureValue, keyDtoOpt.get().getPublicKey());
            } catch (Exception e) {
                sigValid = false;
            }

            if (!sigValid) {
                return "SIGNATURE_INVALID";
            }

            String riskStatus = keyRiskService.checkKeyRisk(keyId, signedAt);
            if ("KEY_COMPROMISED_REVIEW".equals(riskStatus)) {
                return "KEY_COMPROMISED_REVIEW";
            }

            return "SIGNED";
        } catch (Exception e) {
            e.printStackTrace();
            return "SIGNATURE_INVALID";
        }
    }

    private Object[] findSignatureRow(Session session, Long orderId) {
        String sql = """
                SELECT key_id, signature_value, signed_at
                FROM order_signatures
                WHERE order_id = :orderId
                """;

        return (Object[]) session.createNativeQuery(sql)
                .setParameter("orderId", orderId)
                .uniqueResult();
    }

    private Object[] findSignedDataRow(Session session, Long orderId) {
        String sql = """
                SELECT hash_value, signed_data_json
                FROM order_signed_data
                WHERE order_id = :orderId
                """;

        return (Object[]) session.createNativeQuery(sql)
                .setParameter("orderId", orderId)
                .uniqueResult();
    }

    private String getCurrentSignatureStatus(Session session, Long orderId) {
        String sql = """
                SELECT signature_status
                FROM orders
                WHERE id = :orderId
                """;

        String currentStatus = (String) session.createNativeQuery(sql)
                .setParameter("orderId", orderId)
                .uniqueResult();

        return currentStatus != null ? currentStatus : "WAITING_SIGNATURE";
    }

    private List<FieldDiff> findSignedDataDiffs(OrderSignedDataDTO storedDto, Order order) {
        List<FieldDiff> diffs = new ArrayList<>();

        if (storedDto == null || order == null) {
            diffs.add(new FieldDiff("signed_data_json", "VALID_JSON", "INVALID_OR_NULL"));
            return diffs;
        }

        User user = order.getUser();
        Address address = order.getAddress();

        addDiffIfChanged(diffs, "order_id", storedDto.getOrderId(), order.getId());
        addDiffIfChanged(diffs, "order_code", storedDto.getOrderCode(), order.getOrderCode());
        addDiffIfChanged(diffs, "user_id", storedDto.getUserId(), user == null ? null : user.getId());
        addDiffIfChanged(diffs, "buyer_email", storedDto.getBuyerEmail(), user == null ? null : user.getEmail());
        addDiffIfChanged(diffs, "buyer_name", storedDto.getBuyerName(), user == null ? null : user.getFullName());
        addDiffIfChanged(diffs, "address_id", storedDto.getAddressId(), address == null ? null : address.getId());
        addDiffIfChanged(diffs, "receiver_name", storedDto.getReceiverName(), address == null ? null : address.getReceiverName());
        addDiffIfChanged(diffs, "receiver_phone", storedDto.getReceiverPhone(), address == null ? null : address.getReceiverPhone());
        addDiffIfChanged(diffs, "shipping_address", storedDto.getShippingAddress(), buildAddressText(address));
        addDiffIfChanged(diffs, "total_amount", storedDto.getTotalAmount(), order.getTotalAmount());
        // Không audit order_date để tránh lệch format thời gian giữa signed_data_json và LocalDateTime từ DB.
       // addDiffIfChanged(diffs, "order_date", storedDto.getOrderDate(), formatOrderDate(order));

        compareItems(diffs, storedDto.getItems(), order.getItems());

        return diffs;
    }

    private void compareItems(List<FieldDiff> diffs,
                              List<OrderSignedItemDTO> storedItems,
                              List<OrderItem> currentItems) {
        List<OrderSignedItemDTO> safeStoredItems = storedItems == null ? List.of() : storedItems;

        List<OrderItem> safeCurrentItems = currentItems == null
                ? List.of()
                : currentItems.stream()
                .sorted(Comparator.comparing(item -> item.getProduct() == null ? Long.MIN_VALUE : item.getProduct().getId()))
                .toList();

        if (safeStoredItems.size() != safeCurrentItems.size()) {
            diffs.add(new FieldDiff("items.size", String.valueOf(safeStoredItems.size()), String.valueOf(safeCurrentItems.size())));
        }

        int max = Math.max(safeStoredItems.size(), safeCurrentItems.size());
        for (int i = 0; i < max; i++) {
            OrderSignedItemDTO stored = i < safeStoredItems.size() ? safeStoredItems.get(i) : null;
            OrderItem current = i < safeCurrentItems.size() ? safeCurrentItems.get(i) : null;

            String prefix = "items[" + i + "]";

            addDiffIfChanged(
                    diffs,
                    prefix + ".product_id",
                    stored == null ? null : stored.getProductId(),
                    current == null || current.getProduct() == null ? null : current.getProduct().getId()
            );

            addDiffIfChanged(
                    diffs,
                    prefix + ".product_name",
                    stored == null ? null : stored.getProductName(),
                    current == null ? null : current.getProductName()
            );

            addDiffIfChanged(
                    diffs,
                    prefix + ".quantity",
                    stored == null ? null : stored.getQuantity(),
                    current == null ? null : current.getQuantity()
            );

            addDiffIfChanged(
                    diffs,
                    prefix + ".price_at_purchase",
                    stored == null ? null : stored.getPriceAtPurchase(),
                    current == null ? null : current.getPriceAtPurchase()
            );

            addDiffIfChanged(
                    diffs,
                    prefix + ".sub_total",
                    stored == null ? null : stored.getSubTotal(),
                    current == null ? null : current.getSubTotal()
            );
        }
    }

    private void addDiffIfChanged(List<FieldDiff> diffs, String fieldName, Object oldValue, Object newValue) {
        if (oldValue instanceof BigDecimal oldDecimal || newValue instanceof BigDecimal newDecimal) {
            BigDecimal oldBd = oldValue instanceof BigDecimal ? (BigDecimal) oldValue : null;
            BigDecimal newBd = newValue instanceof BigDecimal ? (BigDecimal) newValue : null;

            if (!sameMoney(oldBd, newBd)) {
                diffs.add(new FieldDiff(fieldName, stringify(oldBd), stringify(newBd)));
            }
            return;
        }

        String oldText = stringify(oldValue);
        String newText = stringify(newValue);

        if (!Objects.equals(oldText, newText)) {
            diffs.add(new FieldDiff(fieldName, oldText, newText));
        }
    }

    private boolean sameMoney(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return a == b;
        }

        return a.compareTo(b) == 0;
    }

    private String stringify(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal decimal) {
            return decimal.stripTrailingZeros().toPlainString();
        }

        return String.valueOf(value).trim();
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

    private String formatOrderDate(Order order) {
        if (order == null || order.getOrderDate() == null) {
            return null;
        }

        return order.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private void writeTamperLogs(Long orderId, Long actorId, String actorRole, List<FieldDiff> diffs) {
        String normalizedActorRole = actorRole == null || actorRole.isBlank()
                ? "SYSTEM"
                : actorRole.trim();

        for (FieldDiff diff : diffs) {
            orderAuditLogDAO.insertLog(
                    orderId,
                    actorId,
                    normalizedActorRole,
                    ACTION_SIGNED_DATA_CHANGED,
                    diff.fieldName(),
                    diff.oldValue(),
                    diff.newValue(),
                    true
            );
        }
    }

    private void markOrderDataTampered(Session session, Long orderId) {
        String sql = """
                UPDATE orders
                SET signature_status = 'DATA_TAMPERED'
                WHERE id = :orderId
                  AND signature_status <> 'DATA_TAMPERED'
                """;

        session.createNativeMutationQuery(sql)
                .setParameter("orderId", orderId)
                .executeUpdate();
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }

        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

        return null;
    }

    private record FieldDiff(String fieldName, String oldValue, String newValue) {
    }
}