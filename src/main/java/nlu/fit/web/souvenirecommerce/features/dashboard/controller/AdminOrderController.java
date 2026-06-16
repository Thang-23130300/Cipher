package nlu.fit.web.souvenirecommerce.features.dashboard.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nlu.fit.web.souvenirecommerce.features.signature.service.OrderProcessingGateService;
import nlu.fit.web.souvenirecommerce.features.signature.service.OrderAuditService;
import nlu.fit.web.souvenirecommerce.legacy.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.legacy.model.Order;
import nlu.fit.web.souvenirecommerce.legacy.model.OrderItem;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@WebServlet("/admin/orders")
public class AdminOrderController extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderProcessingGateService processingGateService = new OrderProcessingGateService();
    private final OrderAuditService orderAuditService = new OrderAuditService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        log.debug("Admin order GET request received. action={}, page={}, status={}",
                action, request.getParameter("page"), request.getParameter("status"));

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

        if (orders != null) {
            for (Order o : orders) {
                if (o != null ) {
                    String dynamicStatus = orderAuditService.auditOrderSignature((long) o.getId());
                    o.setSignatureStatus(dynamicStatus);
                }
            }
        }

        log.info("Loaded admin orders page {} with {} records (statusFilter={})",
                page, orders.size(), statusFilter == null || statusFilter.isBlank() ? "all" : statusFilter);

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
        log.debug("Admin order POST request received. action={}", action);

        if ("updateStatus".equals(action)) {
            updateOrderStatus(request, response);
        } else {
            log.warn("Unsupported admin order POST action: {}", action);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported action");
        }
    }

    private void viewOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int orderId;
        try {
            orderId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException ex) {
            log.warn("Invalid order id supplied for admin order detail view: {}", request.getParameter("id"));
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order id");
            return;
        }

        Order order = orderDAO.getOrderById(orderId);
        if (order != null ) {
            String dynamicStatus = orderAuditService.auditOrderSignature((long) order.getId());
            order.setSignatureStatus(dynamicStatus);
        }
        List<OrderItem> orderItems = orderDAO.getOrderItems(orderId);

        log.info("Opened admin order detail for orderId={}", orderId);

        request.setAttribute("order", order);
        request.setAttribute("orderItems", orderItems);
        request.getRequestDispatcher("/admin/order-detail.jsp").forward(request, response);
    }

    private void updateOrderStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int orderId;
        try {
            orderId = Integer.parseInt(request.getParameter("orderId"));
        } catch (NumberFormatException ex) {
            log.warn("Invalid order id supplied for admin order status update: {}", request.getParameter("orderId"));
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order id");
            return;
        }

        String newStatus = request.getParameter("status");
        if (isSignatureStatusValue(newStatus)) {
            log.warn("Blocked attempt to set signature status through order status form. orderId={}, submittedStatus={}",
                    orderId, newStatus);
            request.getSession().setAttribute("error", "Staff/Admin không được tự chuyển trạng thái chữ ký của đơn hàng.");
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=signature_required");
            return;
        }

        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            log.warn("Order not found for admin status update. orderId={}", orderId);
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=true");
            return;
        }

        String auditedSignatureStatus = orderAuditService.auditOrderSignature((long) orderId);
        order.setSignatureStatus(auditedSignatureStatus);
        if (requiresValidSignature(newStatus) && !processingGateService.canProcess(order)) {
            log.warn("Blocked admin status update for unsigned order. orderId={}, signatureStatus={}",
                    orderId, order.getSignatureStatus());
            request.getSession().setAttribute("error", OrderProcessingGateService.DEFAULT_BLOCK_MESSAGE);
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=signature_required");
            return;
        }
        log.info("Updating order status. orderId={}, newStatus={}", orderId, newStatus);
        boolean success = orderDAO.updateOrderStatus(orderId, newStatus);

        if (success) {
            log.info("Order status updated successfully. orderId={}, newStatus={}", orderId, newStatus);
            response.sendRedirect(request.getContextPath() + "/admin/orders?success=true");
        } else {
            log.warn("Order status update failed. orderId={}, newStatus={}", orderId, newStatus);
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=true");
        }
    }

    private boolean requiresValidSignature(String newStatus) {
        if (newStatus == null) {
            return false;
        }

        String normalizedStatus = newStatus.trim().toUpperCase(Locale.ROOT);
        return "PROCESSING".equals(normalizedStatus)
                || "SHIPPING".equals(normalizedStatus)
                || "SHIPPED".equals(normalizedStatus)
                || "COMPLETED".equals(normalizedStatus)
                || "PAID".equals(normalizedStatus)
                || "ĐANG XỬ LÝ".equals(normalizedStatus)
                || "ĐANG GIAO".equals(normalizedStatus)
                || "HOÀN THÀNH".equals(normalizedStatus)
                || "ĐÃ THANH TOÁN".equals(normalizedStatus);
    }

    private boolean isSignatureStatusValue(String newStatus) {
        if (newStatus == null) {
            return false;
        }

        String normalizedStatus = newStatus.trim().toUpperCase(Locale.ROOT);
        return "SIGNED".equals(normalizedStatus)
                || "WAITING_SIGNATURE".equals(normalizedStatus)
                || "SIGNATURE_INVALID".equals(normalizedStatus)
                || "KEY_COMPROMISED_REVIEW".equals(normalizedStatus)
                || "DATA_TAMPERED".equals(normalizedStatus)
                || "UNSIGNED".equals(normalizedStatus);
    }
}
