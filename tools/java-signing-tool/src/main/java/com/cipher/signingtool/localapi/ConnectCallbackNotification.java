package com.cipher.signingtool.localapi;

import java.util.Map;

public record ConnectCallbackNotification(
        boolean success,
        String nonce,
        String keyId,
        String publicKey,
        String fingerprint,
        String createdAt,
        String message
) {
    public static ConnectCallbackNotification fromJson(String json) {
        Map<String, String> values = SimpleJson.parseObject(json);
        return new ConnectCallbackNotification(
                Boolean.parseBoolean(values.getOrDefault("success", "false")),
                values.getOrDefault("nonce", ""),
                values.getOrDefault("keyId", ""),
                values.getOrDefault("publicKey", ""),
                values.getOrDefault("fingerprint", ""),
                values.getOrDefault("createdAt", ""),
                values.getOrDefault("message", "")
        );
    }
}
