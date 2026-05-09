package nlu.fit.web.souvenirecommerce.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.dao.ProductDAO;
import nlu.fit.web.souvenirecommerce.dao.UserDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Product;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

    private ProductDAO productDAO;
    private UserDAO userDAO;
    private OrderDAO orderDAO;

    @Override
    public void init() {
        productDAO = new ProductDAO();
        userDAO = new UserDAO();
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get statistics from database
            int totalProducts = productDAO.getTotalProducts();
            int totalCustomers = userDAO.getTotalCustomers();
            double totalRevenue = orderDAO.getMonthlyRevenue();
            int totalOrders = orderDAO.getMonthlyOrders();

            // Get top selling products
            List<Product> topProducts = productDAO.getTopSellingProducts(10);

            // Get recent orders
            List<nlu.fit.web.souvenirecommerce.model.Order> recentOrders = orderDAO.getRecentOrders(5);

            // Get monthly revenue data for chart (last 6 months)
            List<Double> monthlyRevenues = orderDAO.getMonthlyRevenueData(6);

            // Set attributes
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("topProducts", topProducts);
            request.setAttribute("recentOrders", recentOrders);
            request.setAttribute("monthlyRevenues", monthlyRevenues);

            // Forward to dashboard page
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading dashboard");
        }
    }
}
