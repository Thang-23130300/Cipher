package nlu.fit.web.souvenirecommerce.features.dashboard.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.features.notification.dao.NotificationDAO;
import nlu.fit.web.souvenirecommerce.features.notification.dto.NotificationDTO;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/notifications")
public class AdminNotificationsController extends HttpServlet {
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<NotificationDTO> notifications = notificationDAO.findAll();
        request.setAttribute("notifications", notifications);
        request.setAttribute("activePage", "notifications");
        request.getRequestDispatcher("/admin/notifications.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("markAsRead".equalsIgnoreCase(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.isBlank()) {
                try {
                    Long id = Long.parseLong(idStr.trim());
                    notificationDAO.markAsRead(id);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/notifications");
    }
}