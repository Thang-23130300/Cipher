package nlu.fit.web.souvenirecommerce.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.model.Order;
import nlu.fit.web.souvenirecommerce.model.OrderItem;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders")
public class AdminOrderController extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("view".equals(action)) {
            viewOrderDetail(request, response);
            return;
        }

        // Get filter parameter
        String statusFilter = request.getParameter("status");

        // Get pagination parameters
        int page = 1;
        int pageSize = 20;

        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Get orders with pagination and filter
        List<Order> orders;
        int totalOrders;

        if (statusFilter != null && !statusFilter.isEmpty() && !"all".equals(statusFilter)) {
            orders = orderDAO.getOrdersByStatus(statusFilter, page, pageSize);
            totalOrders = orderDAO.getOrderCountByStatus(statusFilter);
        } else {
            orders = orderDAO.getOrdersPaginated(page, pageSize);
            totalOrders = orderDAO.getTotalOrders();
        }

        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        // Get status counts for stats cards
        int pendingCount = orderDAO.getOrderCountByStatus("Chờ xác nhận");
        int processingCount = orderDAO.getOrderCountByStatus("Đang xử lý");
        int shippingCount = orderDAO.getOrderCountByStatus("Đang giao");
        int completedCount = orderDAO.getOrderCountByStatus("Hoàn thành");

        // Set attributes
        request.setAttribute("orders", orders);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("processingCount", processingCount);
        request.setAttribute("shippingCount", shippingCount);
        request.setAttribute("completedCount", completedCount);

        // Forward to JSP
        request.getRequestDispatcher("/admin/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            updateOrderStatus(request, response);
        }
    }

    private void viewOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int orderId = Integer.parseInt(request.getParameter("id"));
        Order order = orderDAO.getOrderById(orderId);
        List<OrderItem> orderItems = orderDAO.getOrderItems(orderId);

        request.setAttribute("order", order);
        request.setAttribute("orderItems", orderItems);
        request.getRequestDispatcher("/admin/order-detail.jsp").forward(request, response);
    }

    private void updateOrderStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int orderId = Integer.parseInt(request.getParameter("orderId"));
        String newStatus = request.getParameter("status");

        boolean success = orderDAO.updateOrderStatus(orderId, newStatus);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?success=true");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=true");
        }
    }
}