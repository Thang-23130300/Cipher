package nlu.fit.web.souvenirecommerce.features.order.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/order-success")
public class OrderSuccessController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thông tin đơn hàng từ session (nếu có)
        String orderCode = (String) request.getSession().getAttribute("lastOrderCode");

        if (orderCode != null) {
            request.setAttribute("orderCode", orderCode);
            // Xóa khỏi session sau khi hiển thị
            request.getSession().removeAttribute("lastOrderCode");
        }

        request.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(request, response);
    }
}
