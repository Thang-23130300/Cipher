package com.cipher.signingtool;

import org.junit.jupiter.api.Test;

import com.cipher.signingtool.localapi.SimpleJson;
import com.cipher.signingtool.localapi.LocalApiServer;
import com.cipher.signingtool.localapi.PublicKeySavedNotification;
import com.cipher.signingtool.localapi.PublicKeySavedResult;
import com.cipher.signingtool.localapi.SignRequest;
import com.cipher.signingtool.localapi.SigningApiBridge;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void privateKeyMatchesOnlyItsWebPublicKey() {
        KeyPair correctPair = new KeyPairService().generateKeyPair();
        KeyPair wrongPair = new KeyPairService().generateKeyPair();
        KeyMatchService keyMatchService = new KeyMatchService();

        assertTrue(keyMatchService.matches(correctPair.getPrivate(), correctPair.getPublic()));
        assertFalse(keyMatchService.matches(wrongPair.getPrivate(), correctPair.getPublic()));
    }

    @Test
    void activePublicKeyCanBeLoadedFromAuthenticatedWebApi() throws Exception {
        KeyPair keyPair = new KeyPairService().generateKeyPair();
        String publicPem = PemUtils.publicKeyToPem(keyPair.getPublic());
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/user/keys/active", exchange -> {
            String cookie = exchange.getRequestHeaders().getFirst("Cookie");
            int status = "JSESSIONID=test-session".equals(cookie) ? 200 : 401;
            String body = status == 200
                    ? SimpleJson.object(
                            "success", true,
                            "keyId", 42,
                            "publicKey", publicPem,
                            "fingerprint", "AA:BB",
                            "createdAt", "2026-06-18T12:00:00"
                    )
                    : SimpleJson.object("success", false, "message", "Bạn cần đăng nhập.");
            exchange.sendResponseHeaders(status, body.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(body.getBytes(StandardCharsets.UTF_8));
            exchange.close();
        });
        server.start();

        try {
            String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
            WebPublicKeyData data = new ActivePublicKeyClient().load(baseUrl, "test-session");

            assertEquals("42", data.keyId());
            PublicKey loadedPublicKey = new KeyLoader().loadPublicKeyPem(data.publicKeyPem());
            assertTrue(new KeyMatchService().matches(keyPair.getPrivate(), loadedPublicKey));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void missingActivePublicKeyReturnsClearError() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/user/keys/active", exchange -> {
            String body = SimpleJson.object(
                    "success", false,
                    "message", "Tài khoản chưa có public key ACTIVE."
            );
            exchange.sendResponseHeaders(404, body.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(body.getBytes(StandardCharsets.UTF_8));
            exchange.close();
        });
        server.start();

        try {
            String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    () -> new ActivePublicKeyClient().load(baseUrl, "test-session")
            );
            assertTrue(error.getMessage().contains("chưa có public key ACTIVE"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void generatedPublicKeyCanBeSavedToAuthenticatedWebApi() throws Exception {
        KeyPair keyPair = new KeyPairService().generateKeyPair();
        String publicPem = PemUtils.publicKeyToPem(keyPair.getPublic());
        AtomicReference<String> receivedBody = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/user/keys/active", exchange -> {
            receivedBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            String body = SimpleJson.object(
                    "success", true,
                    "keyId", 77,
                    "publicKey", publicPem,
                    "fingerprint", "CC:DD",
                    "createdAt", "2026-06-18T17:00:00"
            );
            exchange.sendResponseHeaders(200, body.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(body.getBytes(StandardCharsets.UTF_8));
            exchange.close();
        });
        server.start();

        try {
            String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
            WebPublicKeyData saved = new ActivePublicKeyClient().save(baseUrl, "test-session", publicPem);

            assertEquals("77", saved.keyId());
            assertTrue(receivedBody.get().contains("PUBLIC KEY"));
            assertFalse(receivedBody.get().contains("PRIVATE KEY"));
            PublicKey savedPublicKey = new KeyLoader().loadPublicKeyPem(saved.publicKeyPem());
            assertTrue(new KeyMatchService().matches(keyPair.getPrivate(), savedPublicKey));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void signingStateSeparatesGeneratedAndLoadedPrivateKeyFlows() {
        assertEquals(
                SigningStatePolicy.Readiness.GENERATED_PUBLIC_KEY_NOT_UPLOADED,
                SigningStatePolicy.evaluate(true, false, false, false, false)
        );
        assertEquals(
                SigningStatePolicy.Readiness.READY,
                SigningStatePolicy.evaluate(true, true, true, true, true)
        );
        assertEquals(
                SigningStatePolicy.Readiness.FILE_PRIVATE_KEY_NOT_CHECKED,
                SigningStatePolicy.evaluate(false, false, false, false, false)
        );
        assertEquals(
                SigningStatePolicy.Readiness.READY,
                SigningStatePolicy.evaluate(false, true, true, true, true)
        );
        assertEquals(
                SigningStatePolicy.Readiness.KEY_MISMATCH,
                SigningStatePolicy.evaluate(false, true, true, true, false)
        );
    }

    @Test
    void sharedToolKeyStateResetsGenerateAndLoadTransitionsAtomically() {
        KeyPair generatedPair = new KeyPairService().generateKeyPair();
        KeyPair loadedPair = new KeyPairService().generateKeyPair();
        ToolKeyState state = new ToolKeyState();

        state.useGeneratedKeyPair(generatedPair);
        ToolKeyState.Snapshot generated = state.snapshot();
        assertEquals(generatedPair.getPrivate(), generated.currentPrivateKey());
        assertEquals(generatedPair.getPublic(), generated.currentPublicKey());
        assertTrue(generated.generatedInCurrentSession());
        assertFalse(generated.publicKeyUploadedToWeb());
        assertFalse(generated.webPublicKeyLoaded());
        assertFalse(generated.keyMatchChecked());
        assertFalse(generated.keyPairMatched());

        state.markGeneratedPublicKeyUploaded(generatedPair.getPublic());
        ToolKeyState.Snapshot uploaded = state.snapshot();
        assertTrue(uploaded.publicKeyUploadedToWeb());
        assertTrue(uploaded.webPublicKeyLoaded());
        assertTrue(uploaded.keyMatchChecked());
        assertTrue(uploaded.keyPairMatched());

        state.useLoadedPrivateKey(loadedPair.getPrivate());
        ToolKeyState.Snapshot loaded = state.snapshot();
        assertEquals(loadedPair.getPrivate(), loaded.currentPrivateKey());
        assertFalse(loaded.generatedInCurrentSession());
        assertFalse(loaded.publicKeyUploadedToWeb());
        assertFalse(loaded.webPublicKeyLoaded());
        assertFalse(loaded.keyMatchChecked());
        assertFalse(loaded.keyPairMatched());
    }

    @Test
    void localSignApiReadsReadyStateFromSharedToolKeyState() throws Exception {
        KeyPair pair = new KeyPairService().generateKeyPair();
        ToolKeyState state = new ToolKeyState();
        state.useGeneratedKeyPair(pair);
        SignatureService signatureService = new SignatureService();

        SigningApiBridge bridge = new SigningApiBridge() {
            @Override
            public String getPublicKeyPem() {
                return PemUtils.publicKeyToPem(state.snapshot().currentPublicKey());
            }

            @Override
            public boolean hasPrivateKey() {
                return state.snapshot().currentPrivateKey() != null;
            }

            @Override
            public ToolKeyState.Snapshot getKeyStateSnapshot() {
                return state.snapshot();
            }

            @Override
            public PublicKeySavedResult onPublicKeySaved(PublicKeySavedNotification notification) {
                PublicKey webPublicKey = new KeyLoader().loadPublicKeyPem(notification.publicKey());
                ToolKeyState.SavedPublicKeyResult result = state.applySavedWebPublicKey(webPublicKey);
                return new PublicKeySavedResult(
                        result == ToolKeyState.SavedPublicKeyResult.MATCHED,
                        result.name()
                );
            }

            @Override
            public boolean isSha256Hex(String hashValue) {
                return signatureService.isSha256Hex(hashValue);
            }

            @Override
            public boolean confirmSigning(SignRequest request) {
                return true;
            }

            @Override
            public String signHashValue(String hashValue) {
                return signatureService.signHashValue(hashValue, state.snapshot().currentPrivateKey());
            }
        };

        int port;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        LocalApiServer server = new LocalApiServer(bridge, port);
        server.start();
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            String savedJson = SimpleJson.object(
                    "success", true,
                    "keyId", 123,
                    "publicKey", PemUtils.publicKeyToPem(pair.getPublic()),
                    "fingerprint", "AA:BB",
                    "createdAt", "2026-06-18T18:00:00"
            );
            HttpRequest savedRequest = HttpRequest.newBuilder(
                            URI.create("http://127.0.0.1:" + port + "/public-key/saved"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(savedJson))
                    .build();
            HttpResponse<String> savedResponse = httpClient.send(
                    savedRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, savedResponse.statusCode());
            assertTrue(state.snapshot().keyPairMatched());
            assertTrue(state.snapshot().webPublicKeyLoaded());

            String hash = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
            String requestJson = SimpleJson.object(
                    "orderId", "123",
                    "merchantName", "INOLA",
                    "hashAlgorithm", "SHA-256",
                    "signatureAlgorithm", "SHA256withRSA",
                    "hashValue", hash
            );
            HttpRequest request = HttpRequest.newBuilder(
                            URI.create("http://127.0.0.1:" + port + "/api/sign"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());
            assertTrue(response.body().contains("\"success\":true"));
            assertFalse(response.body().contains("chưa được load"));

            KeyPair otherPair = new KeyPairService().generateKeyPair();
            String mismatchJson = SimpleJson.object(
                    "success", true,
                    "keyId", 124,
                    "publicKey", PemUtils.publicKeyToPem(otherPair.getPublic()),
                    "fingerprint", "CC:DD",
                    "createdAt", "2026-06-18T18:01:00"
            );
            HttpRequest mismatchRequest = HttpRequest.newBuilder(
                            URI.create("http://127.0.0.1:" + port + "/public-key/saved"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mismatchJson))
                    .build();
            HttpResponse<String> mismatchResponse = httpClient.send(
                    mismatchRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, mismatchResponse.statusCode());
            assertFalse(state.snapshot().keyPairMatched());
            assertTrue(state.snapshot().keyMatchChecked());
            assertTrue(state.snapshot().webPublicKeyLoaded());
        } finally {
            server.stop();
        }
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
