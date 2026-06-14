package nlu.fit.web.souvenirecommerce.features.order.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/order-success")
public class OrderSuccessController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        // Lấy thông tin đơn hàng từ session (nếu có)
        String orderCode = (String) session.getAttribute("lastOrderCode");
        Long orderId = (Long) session.getAttribute("lastOrderId");
        Object signaturePaymentSuccess = session.getAttribute("signaturePaymentSuccess");

        if (orderCode != null) {
            request.setAttribute("orderCode", orderCode);
            // Xóa khỏi session sau khi hiển thị
            session.removeAttribute("lastOrderCode");
        }
        if (orderId != null) {
            request.setAttribute("orderId", orderId);
            session.removeAttribute("lastOrderId");
        }
        if (Boolean.TRUE.equals(signaturePaymentSuccess)) {
            request.setAttribute("signaturePaymentSuccess", true);
            session.removeAttribute("signaturePaymentSuccess");
        }

        request.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(request, response);
    }
}
