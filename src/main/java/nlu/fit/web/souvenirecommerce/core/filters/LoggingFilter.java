package nlu.fit.web.souvenirecommerce.core.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class LoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        MDC.put("user", resolveUserLabel(httpRequest.getSession(false)));

        long startNanos = System.nanoTime();
        try {
            log.info("Incoming request: method={} uri={}", httpRequest.getMethod(), httpRequest.getRequestURI());
            chain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
            log.info("Completed request: method={} uri={} durationMs={}",
                    httpRequest.getMethod(), httpRequest.getRequestURI(), durationMs);
            MDC.clear();
        }
    }

    private String resolveUserLabel(HttpSession session) {
        if (session == null) {
            return "Guest";
        }

        Object sessionUser = session.getAttribute("user");
        if (sessionUser == null) {
            sessionUser = session.getAttribute("currentUser");
        }
        if (sessionUser == null) {
            sessionUser = session.getAttribute("userInSession");
        }
        if (sessionUser == null) {
            sessionUser = session.getAttribute("authUser");
        }

        if (sessionUser instanceof User user) {
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                return user.getEmail();
            }
            String fullName = user.getFullName();
            if (fullName != null && !fullName.isBlank()) {
                return fullName;
            }
        }

        return String.valueOf(sessionUser);
    }
}
