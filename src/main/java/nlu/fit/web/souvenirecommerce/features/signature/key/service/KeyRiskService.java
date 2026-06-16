package nlu.fit.web.souvenirecommerce.features.signature.key.service;

import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import java.time.LocalDateTime;
import java.util.Optional;

public class KeyRiskService {
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();

    public String checkKeyRisk(Long keyId, LocalDateTime signedAt) {
        if (keyId == null) return "INVALID";

        Optional<UserKeyDTO> keyOptional = userKeyDAO.findById(keyId);
        if (keyOptional.isEmpty()) return "INVALID";

        UserKeyDTO key = keyOptional.get();
        String status = key.getKeyStatus();

        if ("ACTIVE".equalsIgnoreCase(status)) {
            return "VALID";
        }

        if ("REVOKED".equalsIgnoreCase(status) || "LOST".equalsIgnoreCase(status)) {
            LocalDateTime revokedAt = key.getRevokedAt();
            if (revokedAt == null) return "INVALID";
            return (signedAt != null && signedAt.isBefore(revokedAt)) ? "VALID" : "INVALID";
        }

        if ("COMPROMISED".equalsIgnoreCase(status)) {
            LocalDateTime compromisedFrom = key.getCompromisedFrom();
            if (compromisedFrom == null) compromisedFrom = key.getRevokedAt();
            if (compromisedFrom == null) return "KEY_COMPROMISED_REVIEW";

            return (signedAt != null && signedAt.isBefore(compromisedFrom)) ? "VALID" : "KEY_COMPROMISED_REVIEW";
        }

        return "INVALID";
    }
}