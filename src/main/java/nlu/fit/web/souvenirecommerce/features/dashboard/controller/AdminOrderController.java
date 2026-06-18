package nlu.fit.web.souvenirecommerce.features.dashboard.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nlu.fit.web.souvenirecommerce.features.notification.service.AdminNotificationService;
import nlu.fit.web.souvenirecommerce.features.order.service.OrderManagementService;
import nlu.fit.web.souvenirecommerce.features.signature.service.OrderAuditService;
import nlu.fit.web.souvenirecommerce.features.signature.service.OrderProcessingGateService;
import nlu.fit.web.souvenirecommerce.legacy.dao.AuthorizationDAO;
import nlu.fit.web.souvenirecommerce.legacy.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.legacy.model.Order;
import nlu.fit.web.souvenirecommerce.legacy.model.OrderItem;
import nlu.fit.web.souvenirecommerce.legacy.model.PermissionGroup;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@WebServlet("/admin/orders")
public class AdminOrderController extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderProcessingGateService processingGateService = new OrderProcessingGateService();
    private final OrderAuditService orderAuditService = new OrderAuditService();
    private final AdminNotificationService adminNotificationService = new AdminNotificationService();
    private final OrderManagementService orderManagementService = new OrderManagementService();
    private final AuthorizationDAO authorizationDAO = new AuthorizationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ActorContext actorContext = requireAuthorizedActor(request, response);
        if (actorContext == null) {
            return;
        }
        request.setAttribute("activePage", "orders");
        request.setAttribute("canManageOrderStatuses", actorContext.admin());

        String action = request.getParameter("action");
        log.debug("Admin order GET request received. action={}, page={}, status={}",
                action, request.getParameter("page"), request.getParameter("status"));

        if ("view".equals(action)) {
            viewOrderDetail(request, response);
            return;
        }

        String statusFilter = request.getParameter("status");
        String signatureStatusFilter = request.getParameter("signatureStatus");

        int page = 1;
        int pageSize = 20;

        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<Order> orders;
        int totalOrders;

        orders = orderDAO.getOrdersFiltered(statusFilter, signatureStatusFilter, page, pageSize);
        totalOrders = orderDAO.getOrderCountFiltered(statusFilter, signatureStatusFilter);

        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        User actor = actorContext.user();
        Long actorId = actor == null ? null : actor.getId();

        if (orders != null) {
            Map<Integer, Boolean> acceptOrderById = new HashMap<>();
            Map<Integer, Boolean> cancelOrderById = new HashMap<>();
            for (Order o : orders) {
                if (o != null) {
                    String dynamicStatus = orderAuditService.auditOrderSignature(
                            (long) o.getId(),
                            actorId,
                            actorContext.roleLabel()
                    );
                    o.setSignatureStatus(dynamicStatus);
                    acceptOrderById.put(o.getId(), OrderManagementService.canAcceptStatus(
                            o.getStatus(), o.getSignatureStatus()
                    ));
                    cancelOrderById.put(o.getId(), OrderManagementService.canCancelStatus(o.getStatus()));
                }
            }
            request.setAttribute("acceptOrderById", acceptOrderById);
            request.setAttribute("cancelOrderById", cancelOrderById);
        }

        log.info("Loaded admin orders page {} with {} records (statusFilter={})",
                page,
                orders == null ? 0 : orders.size(),
                statusFilter == null || statusFilter.isBlank() ? "all" : statusFilter);

        int pendingCount = orderDAO.getOrderCountByStatus(OrderManagementService.STATUS_WAITING_PROCESSING)
                + orderDAO.getOrderCountByStatus(OrderManagementService.STATUS_WAITING_SIGNATURE);
        int processingCount = orderDAO.getOrderCountByStatus(OrderManagementService.STATUS_CONFIRMED)
                + orderDAO.getOrderCountByStatus(OrderManagementService.STATUS_LEGACY_PROCESSING);
        int shippingCount = orderDAO.getOrderCountByStatus("Đang giao hàng");
        int completedCount = orderDAO.getOrderCountByStatus("Đã giao hàng")
                + orderDAO.getOrderCountByStatus("Hoàn thành");

        request.setAttribute("orders", orders);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("signatureStatusFilter", signatureStatusFilter);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("processingCount", processingCount);
        request.setAttribute("shippingCount", shippingCount);
        request.setAttribute("completedCount", completedCount);

        request.getRequestDispatcher("/admin/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        ActorContext actorContext = requireAuthorizedActor(request, response);
        if (actorContext == null) {
            return;
        }
        String action = request.getParameter("action");
        String rawOrderId = request.getParameter("orderId");
        String rawId = request.getParameter("id");
        User actor = actorContext.user();
        log.info("Admin order POST received. uri={}, action={}, orderId={}, id={}, actorId={}, actorName={}",
                request.getRequestURI(),
                action,
                rawOrderId,
                rawId,
                actor == null ? null : actor.getId(),
                actor == null ? null : actor.getFullName());

        if ("accept".equals(action) || "acceptOrder".equals(action)) {
            handleAcceptOrder(request, response, actorContext);
        } else if ("cancel".equals(action) || "cancelOrder".equals(action)) {
            handleCancelOrder(request, response, actorContext);
        } else if ("updateStatus".equals(action) && actorContext.admin()) {
            updateOrderStatus(request, response);
        } else if ("updateStatus".equals(action)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Sale/Staff chỉ được chấp nhận hoặc hủy đơn hàng.");
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
        if (order != null) {
            User actor = getCurrentUser(request);
            String dynamicStatus = orderAuditService.auditOrderSignature(
                    (long) order.getId(),
                    actor == null ? null : actor.getId(),
                    resolveActorRole(actor)
            );
            order.setSignatureStatus(dynamicStatus);
            request.setAttribute("canAcceptOrder", OrderManagementService.canAcceptStatus(
                    order.getStatus(), order.getSignatureStatus()
            ));
            request.setAttribute("canCancelOrder", OrderManagementService.canCancelStatus(order.getStatus()));
        }

        List<OrderItem> orderItems = orderDAO.getOrderItems(orderId);

        log.info("Opened admin order detail for orderId={}", orderId);

        request.setAttribute("order", order);
        request.setAttribute("orderItems", orderItems);
        request.setAttribute("signatureInfo", orderDAO.getOrderSignatureInfo(orderId));
        request.setAttribute("auditLogs", orderDAO.getOrderAuditLogs(orderId));

        request.getRequestDispatcher("/admin/order-detail.jsp").forward(request, response);
    }

    private void handleAcceptOrder(HttpServletRequest request,
                                   HttpServletResponse response,
                                   ActorContext actorContext) throws IOException {
        Long orderId = parsePositiveLong(request.getParameter("orderId"));
        try {
            OrderManagementService.ActionResult result = orderManagementService.acceptOrder(
                    orderId, actorContext.user(), actorContext.roleLabel()
            );
            request.getSession().setAttribute("success", result.message());
            redirectAfterAction(request, response, result.orderId());
        } catch (OrderManagementService.OrderActionException exception) {
            Long actorId = actorContext == null || actorContext.user() == null ? null : actorContext.user().getId();
            log.warn("Accept order rejected. action=accept, orderId={}, actorId={}, reason={}",
                    orderId, actorId, exception.getMessage(), exception);
            request.getSession().setAttribute("error", exception.getMessage());
            redirectAfterAction(request, response, orderId);
        } catch (Exception exception) {
            Long actorId = actorContext == null || actorContext.user() == null ? null : actorContext.user().getId();
            log.error("Unexpected error when accepting order. action=accept, orderId={}, actorId={}",
                    orderId, actorId, exception);
            request.getSession().setAttribute("error", "Không thể cập nhật đơn hàng. Vui lòng thử lại.");
            redirectAfterAction(request, response, orderId);
        }
    }

    private void handleCancelOrder(HttpServletRequest request,
                                   HttpServletResponse response,
                                   ActorContext actorContext) throws IOException {
        Long orderId = parsePositiveLong(request.getParameter("orderId"));
        try {
            OrderManagementService.ActionResult result = orderManagementService.cancelOrder(
                    orderId,
                    actorContext.user(),
                    actorContext.roleLabel(),
                    request.getParameter("reason")
            );
            request.getSession().setAttribute("success", result.message());
            redirectAfterAction(request, response, result.orderId());
        } catch (OrderManagementService.OrderActionException exception) {
            Long actorId = actorContext == null || actorContext.user() == null ? null : actorContext.user().getId();
            log.warn("Cancel order rejected. action=cancel, orderId={}, actorId={}, reason={}",
                    orderId, actorId, exception.getMessage(), exception);
            request.getSession().setAttribute("error", exception.getMessage());
            redirectAfterAction(request, response, orderId);
        } catch (Exception exception) {
            Long actorId = actorContext == null || actorContext.user() == null ? null : actorContext.user().getId();
            log.error("Unexpected error when cancelling order. action=cancel, orderId={}, actorId={}",
                    orderId, actorId, exception);
            request.getSession().setAttribute("error", "Không thể cập nhật đơn hàng. Vui lòng thử lại.");
            redirectAfterAction(request, response, orderId);
        }
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

        User actor = (User) request.getSession().getAttribute("user");
        String auditedSignatureStatus = orderAuditService.auditOrderSignature(
                (long) orderId,
                actor == null ? null : actor.getId(),
                resolveActorRole(actor)
        );
        order.setSignatureStatus(auditedSignatureStatus);

        if (requiresValidSignature(newStatus) && !processingGateService.canProcess(order)) {
            log.warn("Blocked admin status update for unsigned order. orderId={}, signatureStatus={}",
                    orderId, order.getSignatureStatus());
            adminNotificationService.notifyBlockedOrderProcessing(
                    (long) orderId,
                    getActorId(request),
                    order.getSignatureStatus()
            );
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
                || "ĐÃ XÁC NHẬN".equals(normalizedStatus)
                || "ĐANG GIAO".equals(normalizedStatus)
                || "ĐANG GIAO HÀNG".equals(normalizedStatus)
                || "ĐÃ GIAO HÀNG".equals(normalizedStatus)
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

    private Long getActorId(HttpServletRequest request) {
        Object user = request.getSession(false) == null ? null : request.getSession(false).getAttribute("user");
        if (user instanceof User currentUser) {
            return currentUser.getId();
        }

        user = request.getSession(false) == null ? null : request.getSession(false).getAttribute("userInSession");
        if (user instanceof User currentUser) {
            return currentUser.getId();
        }

        user = request.getSession(false) == null ? null : request.getSession(false).getAttribute("currentUser");
        return user instanceof User currentUser ? currentUser.getId() : null;
    }

    private String resolveActorRole(User actor) {
        return actor == null ? "N/A" : resolveActorContext(actor).roleLabel();
    }

    private ActorContext requireAuthorizedActor(HttpServletRequest request,
                                                HttpServletResponse response) throws IOException {
        User user = getCurrentUser(request);
        if (user == null || user.getId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        if (!user.isActive()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Tài khoản đã bị vô hiệu hóa.");
            return null;
        }

        ActorContext actorContext = resolveActorContext(user);
        if (!actorContext.authorized()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Bạn không có quyền quản lý đơn hàng.");
            return null;
        }
        return actorContext;
    }

    private ActorContext resolveActorContext(User user) {
        if (user == null || user.getId() == null) {
            return new ActorContext(user, false, false, "N/A");
        }

        List<PermissionGroup> roles = authorizationDAO.getUserRoles(user.getId());
        Set<String> normalizedRoles = roles.stream()
                .map(PermissionGroup::getName)
                .map(this::normalizeRole)
                .collect(java.util.stream.Collectors.toSet());
        boolean admin = normalizedRoles.stream().anyMatch(ADMIN_ROLES::contains);
        boolean staff = normalizedRoles.stream().anyMatch(STAFF_ROLES::contains);
        String roleLabel = admin ? "admin" : (normalizedRoles.contains("STAFF") ? "staff" : "sale");
        return new ActorContext(user, admin || staff, admin, roleLabel);
    }

    private User getCurrentUser(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return null;
        }
        for (String attribute : List.of("user", "userInSession", "currentUser", "authUser")) {
            Object value = request.getSession(false).getAttribute(attribute);
            if (value instanceof User user) {
                return user;
            }
        }
        return null;
    }

    private Long parsePositiveLong(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception exception) {
            return null;
        }
    }

    private void redirectAfterAction(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Long orderId) throws IOException {
        if ("detail".equals(request.getParameter("returnTo")) && orderId != null) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?action=view&id=" + orderId);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/orders");
    }

    private String normalizeRole(String value) {
        return value == null ? "" : value.replaceAll("[^A-Za-z]", "").toUpperCase(Locale.ROOT);
    }

    private static final Set<String> ADMIN_ROLES = Set.of("ADMIN", "SUPERADMIN");
    private static final Set<String> STAFF_ROLES = Set.of("SALE", "SALES", "STAFF");

    private record ActorContext(User user,
                                boolean authorized,
                                boolean admin,
                                String roleLabel) {
    }
}
