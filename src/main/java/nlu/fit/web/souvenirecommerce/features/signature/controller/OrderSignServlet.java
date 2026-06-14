package nlu.fit.web.souvenirecommerce.features.signature.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.signature.dao.OrderSignedDataDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import nlu.fit.web.souvenirecommerce.legacy.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.legacy.model.Order;
import nlu.fit.web.souvenirecommerce.model.entity.OrderSignedData;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/orders/sign")
public class OrderSignServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderSignedDataDAO orderSignedDataDAO = new OrderSignedDataDAO();
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = getCurrentUser(session);

        if (currentUser == null || currentUser.getId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long orderId = parseOrderId(request.getParameter("id"));
        if (orderId == null) {
            System.out.println("[OrderSignServlet] Missing or invalid order id parameter.");
            setError(session, "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/user/orders");
            return;
        }

        Order order = orderDAO.getOrderById(orderId.intValue());
        if (order == null) {
            System.out.println("[OrderSignServlet] Order not found: orderId=" + orderId);
            setError(session, "Không tìm thấy đơn hàng #" + orderId + ".");
            response.sendRedirect(request.getContextPath() + "/user/orders");
            return;
        }

        if (order.getUserId() != currentUser.getId().intValue()) {
            System.out.println("[OrderSignServlet] Forbidden: userId=" + currentUser.getId()
                    + " tried to sign orderId=" + orderId
                    + " ownedBy=" + order.getUserId());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền ký đơn hàng này.");
            return;
        }

        Optional<OrderSignedData> signedDataOptional = orderSignedDataDAO.findByOrderId(orderId);
        if (signedDataOptional.isEmpty()
                || signedDataOptional.get().getHashValue() == null
                || signedDataOptional.get().getHashValue().isBlank()) {
            System.out.println("[OrderSignServlet] Missing hash_value for orderId=" + orderId);
            setError(session, "Đơn hàng #" + orderId + " chưa có hash_value để ký.");
            response.sendRedirect(request.getContextPath() + "/user/orders?action=detail&id=" + orderId);
            return;
        }

        request.setAttribute("order", order);
        request.setAttribute("orderId", orderId);
        request.setAttribute("orderCode", order.getOrderCode());
        request.setAttribute("hashValue", signedDataOptional.get().getHashValue());
        request.setAttribute("hasActivePublicKey", userKeyDAO.findActiveKeyByUserId(currentUser.getId()).isPresent());

        request.getRequestDispatcher("/WEB-INF/views/signature/order-sign.jsp")
                .forward(request, response);
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object user = session.getAttribute("userInSession");
        if (user instanceof User currentUser) {
            return currentUser;
        }

        user = session.getAttribute("user");
        if (user instanceof User currentUser) {
            return currentUser;
        }

        user = session.getAttribute("currentUser");
        if (user instanceof User currentUser) {
            return currentUser;
        }

        return null;
    }

    private Long parseOrderId(String orderIdValue) {
        if (orderIdValue == null || orderIdValue.isBlank()) {
            return null;
        }

        try {
            long orderId = Long.parseLong(orderIdValue.trim());
            return orderId > 0 ? orderId : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setError(HttpSession session, String message) {
        if (session != null) {
            session.setAttribute("error", message);
        }
    }
}
