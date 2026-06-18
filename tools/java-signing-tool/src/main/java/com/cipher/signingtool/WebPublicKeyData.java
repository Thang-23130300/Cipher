package com.cipher.signingtool;

public record WebPublicKeyData(
        String keyId,
        String publicKeyPem,
        String fingerprint,
        String createdAt
) {
}
