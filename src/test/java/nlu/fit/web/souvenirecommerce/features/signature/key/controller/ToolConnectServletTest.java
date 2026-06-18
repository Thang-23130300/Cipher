package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToolConnectServletTest {

    @Test
    void allowsOnlyHttpLoopbackCallbacks() {
        assertTrue(ToolConnectServlet.isAllowedCallback(
                "http://127.0.0.1:9090/tool/connect/callback"));
        assertTrue(ToolConnectServlet.isAllowedCallback(
                "http://localhost:9090/tool/connect/callback"));

        assertFalse(ToolConnectServlet.isAllowedCallback(
                "https://127.0.0.1:9090/tool/connect/callback"));
        assertFalse(ToolConnectServlet.isAllowedCallback(
                "http://localhost.example.com/tool/connect/callback"));
        assertFalse(ToolConnectServlet.isAllowedCallback(
                "http://127.0.0.1.example.com/tool/connect/callback"));
        assertFalse(ToolConnectServlet.isAllowedCallback(
                "http://example.com/tool/connect/callback"));
    }
}
