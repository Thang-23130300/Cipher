package nlu.fit.web.souvenirecommerce.features.signature.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.signature.dao.OrderSignatureDAO;
import nlu.fit.web.souvenirecommerce.features.signature.dao.OrderSignedDataDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dto.UserKeyDTO;
import nlu.fit.web.souvenirecommerce.features.signature.service.SignatureVerifyService;
import nlu.fit.web.souvenirecommerce.legacy.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.model.entity.OrderSignedData;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/orders/submit-signature")
public class SubmitSignatureServlet extends HttpServlet {
    private static final String VERIFY_VALID = "VALID";
    private static final String VERIFY_INVALID = "INVALID";
    private static final String ORDER_SIGNED = "SIGNED";
    private static final String ORDER_SIGNATURE_INVALID = "SIGNATURE_INVALID";

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderSignedDataDAO orderSignedDataDAO = new OrderSignedDataDAO();
    private final OrderSignatureDAO orderSignatureDAO = new OrderSignatureDAO();
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();
    private final SignatureVerifyService signatureVerifyService = new SignatureVerifyService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = getCurrentUser(session);

        if (currentUser == null || currentUser.getId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long orderId = parseOrderId(request.getParameter("orderId"));
        String signatureValue = request.getParameter("signatureValue");

        if (orderId == null) {
            setError(session, "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/user/orders");
            return;
        }

        nlu.fit.web.souvenirecommerce.legacy.model.Order order = orderDAO.getOrderById(orderId.intValue());
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (order.getUserId() != currentUser.getId().intValue()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (signatureValue == null || signatureValue.isBlank()) {
            setError(session, "Chữ ký không được để trống.");
            redirectToOrderDetail(request, response, orderId);
            return;
        }

        Optional<OrderSignedData> signedDataOptional = orderSignedDataDAO.findByOrderId(orderId);
        if (signedDataOptional.isEmpty()
                || signedDataOptional.get().getHashValue() == null
                || signedDataOptional.get().getHashValue().isBlank()) {
            setError(session, "Đơn hàng chưa có dữ liệu hash để verify.");
            redirectToOrderDetail(request, response, orderId);
            return;
        }

        Optional<UserKeyDTO> activeKeyOptional = userKeyDAO.findActiveKeyByUserId(currentUser.getId());
        if (activeKeyOptional.isEmpty()) {
            setError(session, "Bạn chưa có public key ACTIVE. Vui lòng thêm public key trước khi ký đơn.");
            response.sendRedirect(request.getContextPath() + "/signature/keys");
            return;
        }

        OrderSignedData signedData = signedDataOptional.get();
        UserKeyDTO activeKey = activeKeyOptional.get();

        boolean valid;
        try {
            valid = signatureVerifyService.verify(
                    signedData.getHashValue(),
                    signatureValue,
                    activeKey.getPublicKey()
            );
        } catch (IllegalArgumentException e) {
            setError(session, e.getMessage());
            redirectToOrderDetail(request, response, orderId);
            return;
        }

        String verifyStatus = valid ? VERIFY_VALID : VERIFY_INVALID;
        String orderSignatureStatus = valid ? ORDER_SIGNED : ORDER_SIGNATURE_INVALID;

        orderSignatureDAO.saveOrUpdate(
                orderId,
                currentUser.getId(),
                activeKey.getId(),
                signatureValue.trim(),
                verifyStatus
        );
        orderDAO.updateSignatureStatus(orderId.intValue(), orderSignatureStatus, valid);

        setSuccess(session, valid
                ? "Chữ ký hợp lệ. Đơn hàng đã được ký."
                : "Chữ ký không hợp lệ. Vui lòng kiểm tra lại chữ ký và hash đơn hàng.");

        redirectToOrderDetail(request, response, orderId);
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object user = session.getAttribute("user");
        if (user instanceof User currentUser) {
            return currentUser;
        }

        user = session.getAttribute("userInSession");
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

    private void redirectToOrderDetail(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Long orderId) throws IOException {
        response.sendRedirect(request.getContextPath() + "/user/orders?action=detail&id=" + orderId);
    }

    private void setSuccess(HttpSession session, String message) {
        if (session != null) {
            session.setAttribute("success", message);
        }
    }

    private void setError(HttpSession session, String message) {
        if (session != null) {
            session.setAttribute("error", message);
        }
    }
}
