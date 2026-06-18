package com.cipher.signingtool;

import com.cipher.signingtool.localapi.SimpleJson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class ActivePublicKeyClient {
    private static final String ACTIVE_KEY_PATH = "/api/user/keys/active";
    private final HttpClient httpClient;

    public ActivePublicKeyClient() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build());
    }

    ActivePublicKeyClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public WebPublicKeyData load(String webBaseUrl, String sessionCookie) {
        URI uri = activeKeyUri(webBaseUrl);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET();

        String cookieHeader = normalizeSessionCookie(sessionCookie);
        if (!cookieHeader.isBlank()) {
            requestBuilder.header("Cookie", cookieHeader);
        }

        return send(requestBuilder.build());
    }

    public WebPublicKeyData save(String webBaseUrl, String sessionCookie, String publicKeyPem) {
        if (publicKeyPem == null || publicKeyPem.isBlank()) {
            throw new IllegalArgumentException("Public key chưa được tạo.");
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(activeKeyUri(webBaseUrl))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(
                        SimpleJson.object("publicKey", publicKeyPem)
                ));
        String cookieHeader = normalizeSessionCookie(sessionCookie);
        if (!cookieHeader.isBlank()) {
            requestBuilder.header("Cookie", cookieHeader);
        }
        return send(requestBuilder.build());
    }

    private WebPublicKeyData send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, String> json = SimpleJson.parseObject(response.body());
            String message = json.getOrDefault("message", "").trim();

            if (response.statusCode() == 401 || response.statusCode() == 403) {
                throw new IllegalStateException(message.isBlank()
                        ? "Phiên đăng nhập không hợp lệ hoặc đã hết hạn."
                        : message);
            }
            if (response.statusCode() == 404 || "false".equalsIgnoreCase(json.get("success"))) {
                throw new IllegalStateException(message.isBlank()
                        ? "Tài khoản chưa có public key ACTIVE."
                        : message);
            }
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(message.isBlank()
                        ? "Web trả lỗi HTTP " + response.statusCode() + "."
                        : message);
            }

            String publicKey = json.getOrDefault("publicKey", "").trim();
            if (publicKey.isBlank()) {
                throw new IllegalStateException("API không trả public key ACTIVE.");
            }

            return new WebPublicKeyData(
                    json.getOrDefault("keyId", ""),
                    publicKey,
                    json.getOrDefault("fingerprint", ""),
                    json.getOrDefault("createdAt", "")
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Yêu cầu tải public key đã bị gián đoạn.", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Không thể kết nối tới web: " + e.getMessage(), e);
        }
    }

    private URI activeKeyUri(String webBaseUrl) {
        if (webBaseUrl == null || webBaseUrl.isBlank()) {
            throw new IllegalArgumentException("Web URL không được để trống.");
        }
        String normalized = webBaseUrl.trim().replaceAll("/+$", "");
        return URI.create(normalized + ACTIVE_KEY_PATH);
    }

    private String normalizeSessionCookie(String sessionCookie) {
        if (sessionCookie == null || sessionCookie.isBlank()) {
            return "";
        }
        String value = sessionCookie.trim();
        return value.contains("=") ? value : "JSESSIONID=" + value;
    }
}
