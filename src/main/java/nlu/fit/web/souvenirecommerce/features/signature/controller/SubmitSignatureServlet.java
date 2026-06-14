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
import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.legacy.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.model.entity.OrderSignedData;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import org.hibernate.Transaction;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        try {
            request.setCharacterEncoding("UTF-8");

            HttpSession session = request.getSession(false);
            User currentUser = getCurrentUser(session);

            System.out.println("[SubmitSignatureServlet] START");

            if (currentUser == null || currentUser.getId() == null) {
                System.out.println("[SubmitSignatureServlet] Missing login session.");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            Long orderId = parseOrderId(request.getParameter("orderId"));
            String signatureValue = request.getParameter("signatureValue");

            if (orderId == null) {
                System.out.println("[SubmitSignatureServlet] Invalid orderId.");
                setError(session, "Mã đơn hàng không hợp lệ.");
                redirectToOrders(request, response);
                return;
            }

            nlu.fit.web.souvenirecommerce.legacy.model.Order order = orderDAO.getOrderById(orderId.intValue());
            if (order == null) {
                System.out.println("[SubmitSignatureServlet] Order not found: orderId=" + orderId);
                setError(session, "Không tìm thấy đơn hàng #" + orderId + ".");
                redirectToOrders(request, response);
                return;
            }

            if (order.getUserId() != currentUser.getId().intValue()) {
                System.out.println("[SubmitSignatureServlet] Forbidden: userId=" + currentUser.getId()
                        + " orderId=" + orderId + " ownerId=" + order.getUserId());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            if (signatureValue == null || signatureValue.isBlank()) {
                System.out.println("[SubmitSignatureServlet] Empty signatureValue for orderId=" + orderId);
                setError(session, "Chữ ký không được để trống.");
                redirectToOrders(request, response);
                return;
            }

            Optional<OrderSignedData> signedDataOptional = orderSignedDataDAO.findByOrderId(orderId);
            if (signedDataOptional.isEmpty()
                    || signedDataOptional.get().getHashValue() == null
                    || signedDataOptional.get().getHashValue().isBlank()) {
                System.out.println("[SubmitSignatureServlet] Missing hash_value for orderId=" + orderId);
                setError(session, "Đơn hàng chưa có dữ liệu hash để verify.");
                redirectToOrders(request, response);
                return;
            }

            Optional<UserKeyDTO> activeKeyOptional = userKeyDAO.findActiveKeyByUserId(currentUser.getId());
            if (activeKeyOptional.isEmpty()) {
                System.out.println("[SubmitSignatureServlet] Missing ACTIVE public key for userId=" + currentUser.getId());
                setError(session, "Bạn chưa có public key ACTIVE. Vui lòng thêm public key trước khi ký đơn.");
                redirectToKeyManagement(request, response, orderId);
                return;
            }

            OrderSignedData signedData = signedDataOptional.get();
            UserKeyDTO activeKey = activeKeyOptional.get();

            boolean valid;
            try {
                System.out.println("[SubmitSignatureServlet] before verify orderId=" + orderId);
                valid = signatureVerifyService.verify(
                        signedData.getHashValue(),
                        signatureValue,
                        activeKey.getPublicKey()
                );
            } catch (IllegalArgumentException e) {
                System.out.println("[SubmitSignatureServlet] Verify failed: " + e.getMessage());
                setError(session, e.getMessage());
                redirectToOrders(request, response);
                return;
            }

            String verifyStatus = valid ? VERIFY_VALID : VERIFY_INVALID;
            String orderSignatureStatus = valid ? ORDER_SIGNED : ORDER_SIGNATURE_INVALID;

            System.out.println("[SubmitSignatureServlet] before save orderId=" + orderId + ", verifyStatus=" + verifyStatus);
            orderSignatureDAO.saveOrUpdate(
                    orderId,
                    currentUser.getId(),
                    activeKey.getId(),
                    signatureValue.trim(),
                    verifyStatus
            );

            System.out.println("[SubmitSignatureServlet] before update orderId=" + orderId + ", signatureStatus=" + orderSignatureStatus);
            orderDAO.updateSignatureStatus(orderId.intValue(), orderSignatureStatus, valid);

            System.out.println("[SubmitSignatureServlet] END orderId=" + orderId);
            if (valid) {
                session.setAttribute("lastOrderId", orderId);
                session.setAttribute("lastOrderCode", order.getOrderCode());
                session.setAttribute("signaturePaymentSuccess", Boolean.TRUE);
                setSuccess(session, "Ký hợp lệ. Thanh toán thành công.");
                response.sendRedirect(request.getContextPath() + "/order-success");
                return;
            }

            setSuccess(session, "Chữ ký không hợp lệ. Vui lòng kiểm tra lại chữ ký và hash đơn hàng.");
            redirectToOrders(request, response);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            rollbackCurrentTransaction();
            HttpSession session = request.getSession(false);
            setError(session, "Có lỗi khi gửi chữ ký: " + e.getMessage());
            if (!response.isCommitted()) {
                redirectToOrders(request, response);
            }
        }
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

    private void redirectToOrders(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/orders");
    }

    private void redirectToKeyManagement(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Long orderId) throws IOException {
        String returnUrl = "/orders/sign?id=" + orderId;
        response.sendRedirect(request.getContextPath()
                + "/key-management?returnUrl="
                + URLEncoder.encode(returnUrl, StandardCharsets.UTF_8));
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

    private void rollbackCurrentTransaction() {
        try {
            Transaction transaction = HibernateUtil.getSessionFactory().getCurrentSession().getTransaction();
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                System.out.println("[SubmitSignatureServlet] Rolled back Hibernate transaction.");
            }
        } catch (RuntimeException rollbackError) {
            rollbackError.printStackTrace();
        }
    }
}
