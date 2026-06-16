package nlu.fit.web.souvenirecommerce.features.signature.key.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.KeyReportService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/signature/keys/report")
public class ReportKeyServlet extends HttpServlet {
    private final KeyReportService keyReportService = new KeyReportService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        if (currentUser == null || currentUser.getId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Long keyId = parseLong(request.getParameter("keyId"));
            String reportType = request.getParameter("reportType");
            LocalDateTime compromisedFrom = parseCompromisedFrom(request.getParameter("compromisedFrom"));
            String description = request.getParameter("description");

            keyReportService.reportKey(
                    currentUser.getId(),
                    keyId,
                    reportType,
                    compromisedFrom,
                    description
            );

            request.getSession().setAttribute(
                    "success",
                    "Đã ghi nhận báo cáo khóa. Vui lòng tạo/cập nhật public key mới để tiếp tục ký đơn hàng."
            );
        } catch (Exception e) {
            request.getSession().setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/signature/keys");
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Key ID không hợp lệ.");
        }

        return Long.parseLong(value);
    }

    private LocalDateTime parseCompromisedFrom(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(value.trim());
    }
}