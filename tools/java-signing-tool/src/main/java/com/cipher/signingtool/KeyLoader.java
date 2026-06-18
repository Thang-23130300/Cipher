package com.cipher.signingtool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyLoader {
    private static final String KEY_ALGORITHM = "RSA";

    public PrivateKey loadPrivateKey(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Private key file is required.");
        }

        try {
            String pem = Files.readString(path, StandardCharsets.UTF_8);
            byte[] keyBytes = PemUtils.decodePem(pem, "PRIVATE KEY");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(keySpec);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read private key file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load PKCS#8 private key: " + e.getMessage(), e);
        }
    }

    public PublicKey loadPublicKeyPem(String pem) {
        if (pem == null || pem.isBlank()) {
            throw new IllegalArgumentException("Public key PEM is required.");
        }

        try {
            byte[] keyBytes = PemUtils.decodePem(pem, "PUBLIC KEY");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load X.509 public key: " + e.getMessage(), e);
        }
    }

}
