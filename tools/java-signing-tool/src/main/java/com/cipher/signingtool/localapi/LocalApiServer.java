package com.cipher.signingtool.localapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class LocalApiServer {
    public static final int DEFAULT_PORT = 9090;

    private final SigningApiBridge bridge;
    private final int port;
    private HttpServer server;

    public LocalApiServer(SigningApiBridge bridge) {
        this(bridge, DEFAULT_PORT);
    }

    public LocalApiServer(SigningApiBridge bridge, int port) {
        if (bridge == null) {
            throw new IllegalArgumentException("SigningApiBridge is required.");
        }
        this.bridge = bridge;
        this.port = port;
    }

    public synchronized void start() {
        if (server != null) {
            return;
        }

        try {
            server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
            server.createContext("/api/health", new HealthHandler());
            server.createContext("/api/public-key", new PublicKeyHandler());
            server.createContext("/api/sign", new SignHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        } catch (IOException e) {
            server = null;
            throw new IllegalStateException("Could not start Local API Server on port " + port + ": " + e.getMessage(), e);
        }
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    public synchronized boolean isRunning() {
        return server != null;
    }

    public int getPort() {
        return port;
    }

    private abstract static class BaseHandler implements HttpHandler {
        @Override
        public final void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 204, "");
                return;
            }
            doHandle(exchange);
        }

        protected abstract void doHandle(HttpExchange exchange) throws IOException;

        protected void requireMethod(HttpExchange exchange, String method) throws IOException {
            if (!method.equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, SimpleJson.object(
                        "success", false,
                        "message", "Method not allowed. Use " + method + "."
                ));
            }
        }

        protected boolean isMethod(HttpExchange exchange, String method) {
            return method.equalsIgnoreCase(exchange.getRequestMethod());
        }

        protected String readBody(HttpExchange exchange) throws IOException {
            return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        }

        protected void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            send(exchange, statusCode, json);
        }

        protected void send(HttpExchange exchange, int statusCode, String body) throws IOException {
            byte[] bytes = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(bytes);
            }
        }

        private void addCorsHeaders(HttpExchange exchange) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().set("Access-Control-Max-Age", "3600");
        }
    }

    private class HealthHandler extends BaseHandler {
        @Override
        protected void doHandle(HttpExchange exchange) throws IOException {
            if (!isMethod(exchange, "GET")) {
                requireMethod(exchange, "GET");
                return;
            }

            sendJson(exchange, 200, SimpleJson.object(
                    "status", "OK",
                    "toolName", "Java Signing Tool",
                    "version", "1.0.0"
            ));
        }
    }

    private class PublicKeyHandler extends BaseHandler {
        @Override
        protected void doHandle(HttpExchange exchange) throws IOException {
            if (!isMethod(exchange, "GET")) {
                requireMethod(exchange, "GET");
                return;
            }

            try {
                String publicKeyPem = bridge.getPublicKeyPem();
                sendJson(exchange, 200, SimpleJson.object(
                        "success", true,
                        "algorithm", "RSA",
                        "keySize", 2048,
                        "publicKey", publicKeyPem
                ));
            } catch (Exception e) {
                sendJson(exchange, 400, SimpleJson.object(
                        "success", false,
                        "message", e.getMessage()
                ));
            }
        }
    }

    private class SignHandler extends BaseHandler {
        @Override
        protected void doHandle(HttpExchange exchange) throws IOException {
            if (!isMethod(exchange, "POST")) {
                requireMethod(exchange, "POST");
                return;
            }

            try {
                SignRequest request = SignRequest.fromJson(readBody(exchange));

                if (!bridge.hasPrivateKey()) {
                    sendJson(exchange, 400, SimpleJson.object(
                            "success", false,
                            "orderId", request.getOrderId(),
                            "message", "Private key is not loaded. Generate or load private key first."
                    ));
                    return;
                }

                if (request.getHashValue() == null || request.getHashValue().isBlank()) {
                    sendJson(exchange, 400, SimpleJson.object(
                            "success", false,
                            "orderId", request.getOrderId(),
                            "message", "hashValue is required."
                    ));
                    return;
                }

                if (!bridge.isSha256Hex(request.getHashValue())) {
                    sendJson(exchange, 400, SimpleJson.object(
                            "success", false,
                            "orderId", request.getOrderId(),
                            "message", "hashValue must be a 64-character SHA-256 hex string."
                    ));
                    return;
                }

                String signatureAlgorithm = request.getSignatureAlgorithm();
                if (signatureAlgorithm != null && !signatureAlgorithm.isBlank()
                        && !"SHA256withRSA".equalsIgnoreCase(signatureAlgorithm.trim())) {
                    sendJson(exchange, 400, SimpleJson.object(
                            "success", false,
                            "orderId", request.getOrderId(),
                            "message", "Only SHA256withRSA is supported."
                    ));
                    return;
                }

                if (!bridge.confirmSigning(request)) {
                    sendJson(exchange, 200, SimpleJson.object(
                            "success", false,
                            "orderId", request.getOrderId(),
                            "message", "User rejected signing request."
                    ));
                    return;
                }

                String signatureValue = bridge.signHashValue(request.getHashValue().trim().toLowerCase());

                sendJson(exchange, 200, SimpleJson.object(
                        "success", true,
                        "orderId", request.getOrderId(),
                        "signatureValue", signatureValue
                ));
            } catch (Exception e) {
                sendJson(exchange, 500, SimpleJson.object(
                        "success", false,
                        "message", e.getMessage()
                ));
            }
        }
    }
}
