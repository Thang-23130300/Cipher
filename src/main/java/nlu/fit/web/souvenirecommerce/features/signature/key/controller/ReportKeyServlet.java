package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.KeyCompromiseService;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/signature/keys/report")
public class ReportKeyServlet extends HttpServlet {
    private final KeyCompromiseService compromiseService = new KeyCompromiseService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            HttpSession session = request.getSession(false);
            User currentUser = getCurrentUser(session);

            if (currentUser == null || currentUser.getId() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String keyIdStr = request.getParameter("keyId");
            String reportType = request.getParameter("reportType");
            String compromisedFromStr = request.getParameter("compromisedFrom");
            String description = request.getParameter("description");

            if (keyIdStr == null || keyIdStr.isBlank()) {
                if (session != null) session.setAttribute("error", "Mã khóa trống.");
                redirectToKeyManagement(request, response);
                return;
            }

            Long keyId = Long.parseLong(keyIdStr.trim());
            LocalDateTime compromisedFrom = null;

            if ("COMPROMISED".equalsIgnoreCase(reportType)) {
                if (compromisedFromStr == null || compromisedFromStr.isBlank()) {
                    if (session != null) session.setAttribute("error", "Vui lòng chọn mốc thời gian lộ khóa.");
                    redirectToKeyManagement(request, response);
                    return;
                }
                compromisedFrom = LocalDateTime.parse(compromisedFromStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            compromiseService.reportIncident(currentUser.getId(), keyId, reportType, compromisedFrom, description);
            if (session != null) session.setAttribute("success", "Báo cáo sự cố thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession(false);
            if (session != null) session.setAttribute("error", "Lỗi: " + e.getMessage());
        }
        redirectToKeyManagement(request, response);
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) return null;
        Object user = session.getAttribute("user");
        if (user instanceof User cu) return cu;
        user = session.getAttribute("userInSession");
        if (user instanceof User cu) return cu;
        user = session.getAttribute("currentUser");
        if (user instanceof User cu) return cu;
        return null;
    }

    private void redirectToKeyManagement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/key-management");
    }
}