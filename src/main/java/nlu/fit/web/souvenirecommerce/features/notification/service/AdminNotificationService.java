package nlu.fit.web.souvenirecommerce.features.notification.service;

import nlu.fit.web.souvenirecommerce.features.notification.dao.NotificationDAO;

public class AdminNotificationService {
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public void notifySignatureInvalid(Long orderId) {
        notifySignatureInvalid(orderId, null);
    }

    public void notifySignatureInvalid(Long orderId, Long userId) {
        safeSave(
                userId,
                orderId,
                "SIGNATURE_INVALID",
                "Chữ ký đơn hàng không hợp lệ",
                "Đơn hàng #" + safeId(orderId) + " vừa nhận chữ ký không hợp lệ. Vui lòng kiểm tra trước khi xử lý."
        );
    }

    public void notifyBlockedOrderProcessing(Long orderId, Long actorId, String signatureStatus) {
        String actorText = actorId == null ? "nhân sự/admin" : "user #" + actorId;
        safeSave(
                actorId,
                orderId,
                "ORDER_PROCESSING_BLOCKED",
                "Đơn hàng bị chặn xử lý",
                actorText + " cố xử lý đơn hàng #" + safeId(orderId)
                        + " khi trạng thái chữ ký là " + safeText(signatureStatus) + "."
        );
    }

    public void notifyLostKey(Long userId, Long keyId) {
        safeSave(
                userId,
                null,
                "KEY_LOST",
                "Người dùng báo mất private key",
                "User #" + safeId(userId) + " đã báo mất private key cho public key #" + safeId(keyId) + "."
        );
    }

    public void notifyCompromisedKey(Long userId, Long keyId) {
        safeSave(
                userId,
                null,
                "KEY_COMPROMISED",
                "Người dùng báo lộ private key",
                "User #" + safeId(userId) + " đã báo lộ private key cho public key #" + safeId(keyId) + "."
        );
    }

    public void notifyKeyRisk(Long orderId, Long keyId) {
        notifyKeyRisk(orderId, keyId, null);
    }

    public void notifyKeyRisk(Long orderId, Long keyId, Long userId) {
        safeSave(
                userId,
                orderId,
                "KEY_RISK",
                "Đơn hàng cần xem xét rủi ro khóa",
                "Đơn hàng #" + safeId(orderId) + " liên quan đến key #" + safeId(keyId)
                        + " đang ở trạng thái cần xem xét."
        );
    }

    private void safeSave(Long recipientUserId, Long orderId, String type, String title, String message) {
        try {
            notificationDAO.save(recipientUserId, orderId, type, title, message);
        } catch (Exception e) {
            System.err.println("[AdminNotificationService] Could not save notification: " + e.getMessage());
        }
    }

    private String safeId(Long id) {
        return id == null ? "N/A" : String.valueOf(id);
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "N/A" : value.trim();
    }
}
