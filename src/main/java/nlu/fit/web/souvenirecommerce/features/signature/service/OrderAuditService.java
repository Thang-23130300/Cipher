package nlu.fit.web.souvenirecommerce.features.signature.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.KeyRiskService;
import nlu.fit.web.souvenirecommerce.features.signature.dto.OrderSignedDataDTO;
import nlu.fit.web.souvenirecommerce.features.signature.dto.OrderSignedItemDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Order;
import nlu.fit.web.souvenirecommerce.model.entity.OrderItem;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OrderAuditService {
    private final SignatureVerifyService signatureVerifyService = new SignatureVerifyService();
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();
    private final KeyRiskService keyRiskService = new KeyRiskService();
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public String auditOrderSignature(Long orderId) {
        if (orderId == null) {
            return "WAITING_SIGNATURE";
        }

        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();

            // 1. Lấy thông tin chữ ký đã lưu của đơn hàng
            String sigSql = "SELECT key_id, signature_value, signed_at FROM order_signatures WHERE order_id = :orderId";
            Object[] sigRow = (Object[]) session.createNativeQuery(sigSql)
                    .setParameter("orderId", orderId)
                    .uniqueResult();

            if (sigRow == null) {
                // Nếu chưa có chữ ký, trả về trạng thái signature_status hiện tại từ bảng orders
                String orderSql = "SELECT signature_status FROM orders WHERE id = :orderId";
                String currentStatus = (String) session.createNativeQuery(orderSql)
                        .setParameter("orderId", orderId)
                        .uniqueResult();
                return currentStatus != null ? currentStatus : "WAITING_SIGNATURE";
            }

            Long keyId = ((Number) sigRow[0]).longValue();
            String signatureValue = (String) sigRow[1];

            LocalDateTime signedAt = null;
            if (sigRow[2] instanceof LocalDateTime) {
                signedAt = (LocalDateTime) sigRow[2];
            } else if (sigRow[2] instanceof java.sql.Timestamp) {
                signedAt = ((java.sql.Timestamp) sigRow[2]).toLocalDateTime();
            }

            // 2. Lấy khóa công khai đã dùng để ký đơn
            Optional<UserKeyDTO> keyDtoOpt = userKeyDAO.findById(keyId);
            if (keyDtoOpt.isEmpty()) {
                return "SIGNATURE_INVALID";
            }
            UserKeyDTO keyDto = keyDtoOpt.get();

            // 3. Lấy dữ liệu snapshot và mã băm đã lưu lúc checkout
            String osdSql = "SELECT hash_value, signed_data_json FROM order_signed_data WHERE order_id = :orderId";
            Object[] osdRow = (Object[]) session.createNativeQuery(osdSql)
                    .setParameter("orderId", orderId)
                    .uniqueResult();
            if (osdRow == null) {
                return "SIGNATURE_INVALID";
            }
            String storedHash = (String) osdRow[0];
            String storedJson = (String) osdRow[1];

            // 4. Lấy thực thể Order hiện tại từ Hibernate Session để so sánh các trường dữ liệu
            Order order = session.find(Order.class, orderId);
            if (order == null) {
                return "SIGNATURE_INVALID";
            }

            // Parse chuỗi JSON snapshot đã lưu thành DTO
            OrderSignedDataDTO storedDto = gson.fromJson(storedJson, OrderSignedDataDTO.class);

            // 5. So sánh thông tin đơn hàng hiện tại với snapshot gốc để phát hiện giả mạo dữ liệu (Tampering)
            boolean tampered = false;

            // So sánh tổng tiền (sử dụng phương thức subtract và abs của BigDecimal)
            if (storedDto.getTotalAmount() == null || order.getTotalAmount() == null
                    || storedDto.getTotalAmount().subtract(order.getTotalAmount()).abs().doubleValue() > 0.01) {
                tampered = true;
            }

            // So sánh danh sách sản phẩm trong đơn hàng
            List<OrderItem> currentItems = new ArrayList<>(order.getItems());
            currentItems.sort(Comparator.comparing(item -> item.getProduct().getId()));
            List<OrderSignedItemDTO> storedItems = storedDto.getItems();

            if (currentItems.size() != storedItems.size()) {
                tampered = true;
            } else {
                for (int i = 0; i < currentItems.size(); i++) {
                    OrderItem ci = currentItems.get(i);
                    OrderSignedItemDTO si = storedItems.get(i);
                    if (!ci.getProduct().getId().equals(si.getProductId())
                            || ci.getQuantity() != si.getQuantity()
                            || ci.getPriceAtPurchase() == null || si.getPriceAtPurchase() == null
                            || ci.getPriceAtPurchase().subtract(si.getPriceAtPurchase()).abs().doubleValue() > 0.01) {
                        tampered = true;
                        break;
                    }
                }
            }

            // 6. Xác thực chữ ký số bằng mã băm gốc đã lưu (Cryptographic Verification)
            boolean sigValid = false;
            try {
                sigValid = signatureVerifyService.verify(storedHash, signatureValue, keyDto.getPublicKey());
            } catch (Exception e) {
                sigValid = false;
            }

            String determinedStatus = "SIGNED";
            if (!sigValid) {
                determinedStatus = "SIGNATURE_INVALID";
            } else if (tampered) {
                determinedStatus = "DATA_TAMPERED";
            } else {
                // 7. Kiểm tra tiếp rủi ro lộ khóa tại thời điểm ký
                String riskStatus = keyRiskService.checkKeyRisk(keyId, signedAt);
                if ("KEY_COMPROMISED_REVIEW".equals(riskStatus)) {
                    determinedStatus = "KEY_COMPROMISED_REVIEW";
                }
            }

            // Đồng bộ trạng thái chữ ký số xác định được vào CSDL (orders.signature_status)
            String currentStatusSql = "SELECT signature_status FROM orders WHERE id = :orderId";
            String currentStatus = (String) session.createNativeQuery(currentStatusSql)
                    .setParameter("orderId", orderId)
                    .uniqueResult();

            if (!determinedStatus.equals(currentStatus)) {
                String updateSql = "UPDATE orders SET signature_status = :status WHERE id = :orderId";
                session.createNativeQuery(updateSql)
                        .setParameter("status", determinedStatus)
                        .setParameter("orderId", orderId)
                        .executeUpdate();
            }

            return determinedStatus;
        } catch (Exception e) {
            e.printStackTrace();
            return "SIGNATURE_INVALID";
        }
    }
}
