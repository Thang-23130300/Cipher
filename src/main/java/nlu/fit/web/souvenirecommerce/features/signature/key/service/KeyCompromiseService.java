package nlu.fit.web.souvenirecommerce.features.signature.key.service;

import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.KeyCompromiseReportDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.features.signature.dao.OrderSignatureDAO;
import nlu.fit.web.souvenirecommerce.features.notification.service.AdminNotificationService;
import nlu.fit.web.souvenirecommerce.legacy.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.model.entity.KeyCompromiseReport;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.model.entity.UserKey;
import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import org.hibernate.Session;
import java.time.LocalDateTime;
import java.util.Optional;

public class KeyCompromiseService {
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();
    private final KeyCompromiseReportDAO reportDAO = new KeyCompromiseReportDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderSignatureDAO signatureDAO = new OrderSignatureDAO();
    private final AdminNotificationService adminNotificationService = new AdminNotificationService();

    public void reportIncident(Long userId, Long keyId, String reportType, LocalDateTime compromisedFrom, String description) {
        if (userId == null || keyId == null) throw new IllegalArgumentException("Dữ liệu không hợp lệ.");

        Optional<UserKeyDTO> keyOptional = userKeyDAO.findById(keyId);
        if (keyOptional.isEmpty() || !keyOptional.get().getUserId().equals(userId)) {
            throw new SecurityException("Khóa này không thuộc về tài khoản của bạn.");
        }

        UserKeyDTO keyDto = keyOptional.get();
        if (!"ACTIVE".equalsIgnoreCase(keyDto.getKeyStatus())) {
            throw new IllegalArgumentException("Khóa đã bị thu hồi hoặc báo cáo sự cố trước đó.");
        }

        userKeyDAO.updateKeyIncidentStatus(keyId, userId, reportType.toUpperCase(), compromisedFrom, description);

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        KeyCompromiseReport report = KeyCompromiseReport.builder()
                .user(session.find(User.class, userId))
                .userKey(session.find(UserKey.class, keyId)) 
                .reportType(reportType.toUpperCase())
                .compromisedFrom(compromisedFrom)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
        reportDAO.save(report);

        if ("LOST".equalsIgnoreCase(reportType)) {
            adminNotificationService.notifyLostKey(userId, keyId);
        }

        if ("COMPROMISED".equalsIgnoreCase(reportType)) {
            adminNotificationService.notifyCompromisedKey(userId, keyId);
            signatureDAO.markSignaturesAsCompromisedReview(keyId, compromisedFrom);
            orderDAO.markOrdersAsCompromisedReview(keyId, compromisedFrom);

            String affectedOrdersSql = """
                    SELECT o.id
                    FROM orders o
                    JOIN order_signatures os ON o.id = os.order_id
                    WHERE os.key_id = :keyId
                      AND os.signed_at >= :compromisedFrom
                    """;
            java.util.List<?> affected = session.createNativeQuery(affectedOrdersSql)
                    .setParameter("keyId", keyId)
                    .setParameter("compromisedFrom", compromisedFrom)
                    .list();

            for (Object row : affected) {
                Long orderId = ((Number) row).longValue();
                adminNotificationService.notifyKeyRisk(orderId, keyId, userId);
            }
        }
    }
}
