package com.cipher.signingtool.localapi;

import java.util.Map;

public record PublicKeySavedNotification(
        boolean success,
        String keyId,
        String publicKey,
        String fingerprint,
        String createdAt
) {
    public static PublicKeySavedNotification fromJson(String json) {
        Map<String, String> values = SimpleJson.parseObject(json);
        return new PublicKeySavedNotification(
                Boolean.parseBoolean(values.getOrDefault("success", "false")),
                values.getOrDefault("keyId", ""),
                values.getOrDefault("publicKey", ""),
                values.getOrDefault("fingerprint", ""),
                values.getOrDefault("createdAt", "")
        );
    }
}
