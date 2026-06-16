package nlu.fit.web.souvenirecommerce.features.signature.service;

import nlu.fit.web.souvenirecommerce.model.entity.Order;

import java.util.Locale;

public class OrderProcessingGateService {
    public static final String SIGNED = "SIGNED";
    public static final String DEFAULT_BLOCK_MESSAGE = "Đơn hàng chưa có chữ ký hợp lệ, không thể xử lý.";

    public boolean canProcess(Order order) {
        return order != null && isSigned(order.getSignatureStatus());
    }

    public boolean canProcess(nlu.fit.web.souvenirecommerce.legacy.model.Order order) {
        return order != null && isSigned(order.getSignatureStatus());
    }

    public boolean isSigned(String signatureStatus) {
        return SIGNED.equals(normalize(signatureStatus));
    }

    public String getBlockReason(Order order) {
        return getBlockReason(order == null ? null : order.getSignatureStatus());
    }

    public String getBlockReason(nlu.fit.web.souvenirecommerce.legacy.model.Order order) {
        return getBlockReason(order == null ? null : order.getSignatureStatus());
    }

    public String getBlockReason(String signatureStatus) {
        return switch (normalize(signatureStatus)) {
            case "WAITING_SIGNATURE" -> "Đơn hàng đang chờ ký số. Vui lòng ký đơn trước khi xử lý.";
            case "SIGNATURE_INVALID" -> "Chữ ký đơn hàng không hợp lệ. Vui lòng ký lại trước khi xử lý.";
            case "KEY_COMPROMISED_REVIEW" -> "Khóa ký cần được xem xét. Không thể xử lý đơn hàng lúc này.";
            case "DATA_TAMPERED" -> "Dữ liệu đơn hàng có dấu hiệu bị thay đổi. Không thể xử lý đơn hàng.";
            case SIGNED -> "";
            default -> DEFAULT_BLOCK_MESSAGE;
        };
    }

    private String normalize(String signatureStatus) {
        return signatureStatus == null ? "" : signatureStatus.trim().toUpperCase(Locale.ROOT);
    }
}
