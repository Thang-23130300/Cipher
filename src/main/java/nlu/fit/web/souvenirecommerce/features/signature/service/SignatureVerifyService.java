package nlu.fit.web.souvenirecommerce.features.signature.service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignatureVerifyService {
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String KEY_ALGORITHM = "RSA";

    public boolean verify(String hashValue, String signatureValue, String publicKeyPem) {
        if (hashValue == null || hashValue.isBlank()) {
            throw new IllegalArgumentException("Hash đơn hàng không hợp lệ");
        }
        if (signatureValue == null || signatureValue.isBlank()) {
            throw new IllegalArgumentException("Chữ ký không được để trống");
        }
        if (publicKeyPem == null || publicKeyPem.isBlank()) {
            throw new IllegalArgumentException("Public key không hợp lệ");
        }

        try {
            byte[] signatureBytes = decodeSignature(signatureValue);
            PublicKey publicKey = parsePublicKey(publicKeyPem);

            Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifier.initVerify(publicKey);
            verifier.update(hashValue.getBytes(StandardCharsets.UTF_8));

            return verifier.verify(signatureBytes);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Không thể verify chữ ký: " + e.getMessage(), e);
        }
    }

    private byte[] decodeSignature(String signatureValue) {
        try {
            return Base64.getDecoder().decode(signatureValue.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Chữ ký phải là Base64 hợp lệ", e);
        }
    }

    private PublicKey parsePublicKey(String publicKeyPem) throws Exception {
        String pemBody = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        if (pemBody.isBlank()) {
            throw new IllegalArgumentException("Public key PEM không hợp lệ");
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(pemBody);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Public key PEM không hợp lệ", e);
        }

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(keySpec);
    }
}
