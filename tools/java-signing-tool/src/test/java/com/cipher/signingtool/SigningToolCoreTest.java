package com.cipher.signingtool;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void localConfigStoresOnlyLastPrivateKeyPath() throws Exception {
        Path configFile = Files.createTempFile("signing-tool-config", ".properties");
        Path privateKeyFile = Files.createTempFile("signing-tool-private-key", ".pem");
        LocalConfigService configService = new LocalConfigService(configFile);

        configService.saveLastPrivateKeyPath(privateKeyFile);

        String configText = Files.readString(configFile, StandardCharsets.UTF_8);
        assertTrue(configText.contains("lastPrivateKeyPath="));
        assertTrue(configService.getLastPrivateKeyPath().isPresent());
        assertEquals(privateKeyFile.toAbsolutePath().normalize(), configService.getLastPrivateKeyPath().get());
    }

    @Test
    void publicKeyCanBeDerivedFromLoadedPrivateKey() throws Exception {
        KeyPair keyPair = new KeyPairService().generateKeyPair();
        Path privateKeyFile = Files.createTempFile("signing-tool-private-key", ".pem");
        Files.writeString(privateKeyFile, PemUtils.privateKeyToPem(keyPair.getPrivate()), StandardCharsets.UTF_8);

        KeyLoader keyLoader = new KeyLoader();
        PublicKey derivedPublicKey = keyLoader.derivePublicKey(keyLoader.loadPrivateKey(privateKeyFile));

        String hashValue = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        String signatureBase64 = new SignatureService().signHashValue(hashValue, keyPair.getPrivate());
        assertTrue(verify(hashValue, signatureBase64, derivedPublicKey));
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
        return verify(hashValue, signatureBase64, keyPair.getPublic());
    }

    private boolean verify(String hashValue, String signatureBase64, PublicKey publicKey) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(hashValue.getBytes(StandardCharsets.UTF_8));
        return verifier.verify(Base64.getDecoder().decode(signatureBase64));
    }
}
