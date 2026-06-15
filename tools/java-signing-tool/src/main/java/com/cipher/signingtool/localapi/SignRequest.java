package com.cipher.signingtool.localapi;

import java.util.Map;

public class SignRequest {
    private final String orderId;
    private final String merchantName;
    private final String hashAlgorithm;
    private final String signatureAlgorithm;
    private final String hashValue;

    public SignRequest(String orderId, String merchantName, String hashAlgorithm, String signatureAlgorithm, String hashValue) {
        this.orderId = orderId;
        this.merchantName = merchantName;
        this.hashAlgorithm = hashAlgorithm;
        this.signatureAlgorithm = signatureAlgorithm;
        this.hashValue = hashValue;
    }

    public static SignRequest fromJson(String json) {
        Map<String, String> values = SimpleJson.parseObject(json);
        return new SignRequest(
                values.getOrDefault("orderId", ""),
                values.getOrDefault("merchantName", ""),
                values.getOrDefault("hashAlgorithm", ""),
                values.getOrDefault("signatureAlgorithm", ""),
                values.getOrDefault("hashValue", "")
        );
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public String getHashValue() {
        return hashValue;
    }
}
