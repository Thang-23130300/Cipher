package nlu.fit.web.souvenirecommerce.features.signature.key.service;

import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;

import java.util.List;
import java.util.Optional;

public class UserKeyService {
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();

    public Optional<UserKeyDTO> getActiveKey(Long userId) {
        validateUserId(userId);
        return userKeyDAO.findActiveByUserId(userId);
    }

    public List<UserKeyDTO> getUserKeys(Long userId) {
        validateUserId(userId);
        return userKeyDAO.findAllByUserId(userId);
    }

    public void saveNewPublicKey(Long userId, String publicKey) {
        validateUserId(userId);
        validatePublicKey(publicKey);

        userKeyDAO.revokeActiveKeys(userId);
        userKeyDAO.savePublicKey(userId, normalizePublicKey(publicKey));
    }

    public void revokeKey(Long keyId, Long userId) {
        if (keyId == null) {
            throw new IllegalArgumentException("Key ID không hợp lệ");
        }

        validateUserId(userId);
        userKeyDAO.revokeById(keyId, userId);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User chưa đăng nhập");
        }
    }

    private void validatePublicKey(String publicKey) {
        if (publicKey == null || publicKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Public key không được để trống");
        }

        String normalized = normalizePublicKey(publicKey);

        if (!normalized.contains("-----BEGIN PUBLIC KEY-----")
                || !normalized.contains("-----END PUBLIC KEY-----")) {
            throw new IllegalArgumentException("Public key phải đúng định dạng PEM");
        }

        String body = normalized
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        if (body.isEmpty()) {
            throw new IllegalArgumentException("Nội dung public key không hợp lệ");
        }
    }

    private String normalizePublicKey(String publicKey) {
        return publicKey == null ? null : publicKey.trim();
    }
}