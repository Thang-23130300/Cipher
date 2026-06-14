package com.cipher.signingtool;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.Signature;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SigningToolCoreTest {

    @Test
    void generateExportLoadSignAndVerify() throws Exception {
        KeyPairService keyPairService = new KeyPairService();
        SignatureService signatureService = new SignatureService();
        KeyLoader keyLoader = new KeyLoader();

        KeyPair keyPair = keyPairService.generateKeyPair();
        String publicPem = PemUtils.publicKeyToPem(keyPair.getPublic());
        String privatePem = PemUtils.privateKeyToPem(keyPair.getPrivate());

        assertTrue(publicPem.startsWith("-----BEGIN PUBLIC KEY-----"));
        assertTrue(privatePem.startsWith("-----BEGIN PRIVATE KEY-----"));

        Path privateKeyFile = Files.createTempFile("signing-tool-private-key", ".pem");
        Files.writeString(privateKeyFile, privatePem, StandardCharsets.UTF_8);

        String hashValue = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        String signatureBase64 = signatureService.signHashValue(hashValue, keyLoader.loadPrivateKey(privateKeyFile));

        assertDoesNotThrow(() -> Base64.getDecoder().decode(signatureBase64));
        assertTrue(verify(hashValue, signatureBase64, keyPair));
    }

    @Test
    void emptyHashIsRejected() {
        KeyPair keyPair = new KeyPairService().generateKeyPair();

        assertThrows(
                IllegalArgumentException.class,
                () -> new SignatureService().signHashValue(" ", keyPair.getPrivate())
        );
    }

    @Test
    void sha256HexFormatIsDetected() {
        SignatureService signatureService = new SignatureService();

        assertTrue(signatureService.isSha256Hex(
                "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        ));
    }

    private boolean verify(String hashValue, String signatureBase64, KeyPair keyPair) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(keyPair.getPublic());
        verifier.update(hashValue.getBytes(StandardCharsets.UTF_8));
        return verifier.verify(Base64.getDecoder().decode(signatureBase64));
    }
}
