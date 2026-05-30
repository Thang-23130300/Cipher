package nlu.fit.web.souvenirecommerce.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.AuthorizationDAO;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.util.AuthorizationPolicy;
import java.io.IOException;

//@WebFilter(urlPatterns = {"/admin/*", "/user/*"})
public class AuthFilter implements Filter {
    private final AuthorizationDAO authorizationDAO = new AuthorizationDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        Object sessionUser = session != null ? session.getAttribute("userInSession") : null;
        if (sessionUser == null && session != null) {
            sessionUser = session.getAttribute("user");
        }
        if (sessionUser == null && session != null) {
            sessionUser = session.getAttribute("currentUser");
        }
        boolean loggedIn = sessionUser != null;

        System.out.println(
                "Đang truy cập: " + req.getServletPath() + " - LoggedIn: " + loggedIn
        );

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        if (!loggedIn) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (req.getServletPath() != null && req.getServletPath().startsWith("/admin")) {
            long userId;
            String role;

            if (sessionUser instanceof User user) {
                userId = user.getId() == null ? 0 : user.getId();
                role = user.getRoles() == null ? null : user.getRoles().stream()
                        .map(nlu.fit.web.souvenirecommerce.model.entity.Role::getName)
                        .filter("Admin"::equalsIgnoreCase)
                        .findFirst()
                        .orElse(null);
            } else {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            AuthorizationPolicy.RequiredPermission requiredPermission = AuthorizationPolicy.resolve(req);
            boolean legacyAdmin = "Admin".equalsIgnoreCase(role);
            boolean allowed = !requiredPermission.protectedRoute()
                    || legacyAdmin
                    || authorizationDAO.hasPermission(userId, requiredPermission.resource(), requiredPermission.action());

            if (!allowed) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập chức năng này.");
                return;
            }

            chain.doFilter(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
