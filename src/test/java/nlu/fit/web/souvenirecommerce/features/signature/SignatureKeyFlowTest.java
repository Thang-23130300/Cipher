package nlu.fit.web.souvenirecommerce.features.signature;

import nlu.fit.web.souvenirecommerce.features.signature.key.service.PublicKeyFingerprintService;
import nlu.fit.web.souvenirecommerce.features.signature.service.SignatureVerifyService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureKeyFlowTest {

    @Test
    void correctPrivateKeyVerifiesAndWrongPrivateKeyIsInvalid() throws Exception {
        KeyPair activePair = generateKeyPair();
        KeyPair wrongPair = generateKeyPair();
        String hashValue = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        String activePublicPem = publicKeyPem(activePair);
        SignatureVerifyService verifyService = new SignatureVerifyService();

        assertTrue(verifyService.verify(hashValue, sign(hashValue, activePair), activePublicPem));
        assertFalse(verifyService.verify(hashValue, sign(hashValue, wrongPair), activePublicPem));
    }

    @Test
    void activePublicKeyFingerprintIsStable() throws Exception {
        String pem = publicKeyPem(generateKeyPair());
        PublicKeyFingerprintService service = new PublicKeyFingerprintService();

        String first = service.sha256Fingerprint(pem);
        String second = service.sha256Fingerprint(pem);

        assertTrue(first.matches("([0-9A-F]{2}:){31}[0-9A-F]{2}"));
        assertTrue(first.equals(second));
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private String sign(String value, KeyPair keyPair) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private String publicKeyPem(KeyPair keyPair) {
        String body = Base64.getMimeEncoder(64, new byte[] {'\n'})
                .encodeToString(keyPair.getPublic().getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" + body + "\n-----END PUBLIC KEY-----";
    }
}
