package nlu.fit.web.souvenirecommerce.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.dto.DashboardMetricsDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.service.AdminDashboardService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

    private AdminDashboardService dashboardService;

    @Override
    public void init() {
        dashboardService = new AdminDashboardService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            DashboardMetricsDTO metrics = dashboardService.buildDashboardMetrics();

            request.setAttribute("totalProducts", metrics.getTotalProducts());
            request.setAttribute("totalCustomers", metrics.getTotalCustomers());
            request.setAttribute("totalRevenue", metrics.getTotalRevenue());
            request.setAttribute("totalOrders", metrics.getTotalOrders());
            request.setAttribute("topProducts", metrics.getTopProducts());
            request.setAttribute("recentOrders", metrics.getRecentOrders());
            request.setAttribute("monthlyRevenues", metrics.getMonthlyRevenue());
            request.setAttribute("dashboardMetrics", metrics);

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading dashboard");
        }
    }
}
