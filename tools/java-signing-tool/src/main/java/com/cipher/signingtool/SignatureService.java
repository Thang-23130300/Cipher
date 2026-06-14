package com.cipher.signingtool;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.regex.Pattern;

public class SignatureService {
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final Pattern SHA256_HEX_PATTERN = Pattern.compile("^[a-fA-F0-9]{64}$");

    public String signHashValue(String hashValue, PrivateKey privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key is required before signing.");
        }
        if (hashValue == null || hashValue.isBlank()) {
            throw new IllegalArgumentException("hash_value must not be empty.");
        }

        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(hashValue.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new IllegalStateException("Could not sign hash_value: " + e.getMessage(), e);
        }
    }

    public boolean isSha256Hex(String value) {
        return value != null && SHA256_HEX_PATTERN.matcher(value.trim()).matches();
    }
}
