package com.cipher.signingtool;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64;

public class KeyMatchService {
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private final SecureRandom secureRandom = new SecureRandom();

    public boolean matches(PrivateKey privateKey, PublicKey publicKey) {
        if (privateKey == null || publicKey == null) {
            throw new IllegalArgumentException("Private key và public key web đều phải được tải trước.");
        }

        try {
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);
            byte[] challenge = Base64.getEncoder().encode(randomBytes);

            Signature signer = Signature.getInstance(SIGNATURE_ALGORITHM);
            signer.initSign(privateKey, secureRandom);
            signer.update(challenge);
            byte[] signature = signer.sign();

            Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifier.initVerify(publicKey);
            verifier.update(challenge);
            return verifier.verify(signature);
        } catch (Exception e) {
            throw new IllegalStateException("Không thể kiểm tra cặp khóa: " + e.getMessage(), e);
        }
    }
}
