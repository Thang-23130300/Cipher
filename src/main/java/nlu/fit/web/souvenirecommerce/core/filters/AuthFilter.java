package nlu.fit.web.souvenirecommerce.core.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.common.utils.PermissionHelper;
import nlu.fit.web.souvenirecommerce.legacy.dao.AuthorizationDAO;
import nlu.fit.web.souvenirecommerce.common.utils.AuthorizationPolicy;
import java.io.IOException;

public class AuthFilter implements Filter {
    private final AuthorizationDAO authorizationDAO = new AuthorizationDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        boolean loggedIn = PermissionHelper.getCurrentUser(req) != null;

        System.out.println(
                "Đang truy cập: " + req.getServletPath() + " - LoggedIn: " + loggedIn
        );

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        if (!loggedIn) {
            if (session != null) {
                String requestedUrl = req.getRequestURI() + (req.getQueryString() == null ? "" : "?" + req.getQueryString());
                session.setAttribute("redirectAfterLogin", requestedUrl);
            }
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (!PermissionHelper.hasAdminPortalAccess(req)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập kênh quản trị.");
            return;
        }

        AuthorizationPolicy.RequiredPermission requiredPermission = AuthorizationPolicy.resolve(req);
        long userId = PermissionHelper.getUserId(req);
        boolean fullAdmin = PermissionHelper.hasFullAdminAccess(req);
        boolean orderStaff = PermissionHelper.hasOrderManagementRole(req);
        boolean allowed = !requiredPermission.protectedRoute()
                || fullAdmin
                || ("order".equals(requiredPermission.resource())
                && orderStaff
                && (authorizationDAO.hasPermission(userId, requiredPermission.resource(), requiredPermission.action())
                || "read".equals(requiredPermission.action())
                || "update".equals(requiredPermission.action())));

        if (!allowed) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập chức năng này.");
            return;
        }

        chain.doFilter(request, response);
    }
}
