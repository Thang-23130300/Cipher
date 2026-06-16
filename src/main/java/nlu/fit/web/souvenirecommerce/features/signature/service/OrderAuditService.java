package nlu.fit.web.souvenirecommerce.features.signature.service;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.KeyRiskService;
import nlu.fit.web.souvenirecommerce.model.entity.Order;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.Optional;

public class OrderAuditService {
    private final OrderSignedDataService orderSignedDataService = new OrderSignedDataService();
    private final HashService hashService = new HashService();
    private final SignatureVerifyService signatureVerifyService = new SignatureVerifyService();
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();
    private final KeyRiskService keyRiskService = new KeyRiskService();

    public String auditOrderSignature(Long orderId) {
        if (orderId == null) {
            return "WAITING_SIGNATURE";
        }

        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();

            //Lấy thông tin chữ ký đã lưu của đơn hàng
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
            java.sql.Timestamp signedAtTimestamp = (java.sql.Timestamp) sigRow[2];
            LocalDateTime signedAt = signedAtTimestamp.toLocalDateTime();

            // lấy khóa công khai đã dùng để ký đơn
            Optional<UserKeyDTO> keyDtoOpt = userKeyDAO.findById(keyId);
            if (keyDtoOpt.isEmpty()) {
                return "SIGNATURE_INVALID";
            }
            UserKeyDTO keyDto = keyDtoOpt.get();

            //  Lấy  Order hiện tại từ Hibernate Session để dựng snapshot động
            Order order = session.find(Order.class, orderId);
            if (order == null) {
                return "SIGNATURE_INVALID";
            }

            // Sinh JSON snapshot hiện tại và tính mã băm mới
            String currentJson = orderSignedDataService.getSignedDataJson(order);
            if (currentJson == null) {
                return "SIGNATURE_INVALID";
            }
            String currentHash = hashService.sha256Hex(currentJson);

            //Kiểm tra tính toàn vẹn của chữ ký số với dữ liệu hiện tại
            boolean valid = false;
            try {
                valid = signatureVerifyService.verify(currentHash, signatureValue, keyDto.getPublicKey());
            } catch (Exception e) {
                valid = false;
            }

            if (!valid) {
                return "SIGNATURE_INVALID";
            }

            //kiểm tra tiếp rủi ro lộ khóa tại thời điểm ký
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
}