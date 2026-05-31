package nlu.fit.web.souvenirecommerce.features.order.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.cart.model.Cart;
import nlu.fit.web.souvenirecommerce.features.cart.model.CartItem;
import nlu.fit.web.souvenirecommerce.legacy.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/checkout")
public class CheckoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        // Check if cart is empty
        if (cart == null || cart.totalQuantity() == 0) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Forward to checkout page
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        User user = (User) session.getAttribute("user");

        if (cart == null || cart.totalQuantity() == 0) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Get form data
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String province = request.getParameter("province");
        String district = request.getParameter("district");
        String note = request.getParameter("note");
        String paymentMethod = request.getParameter("paymentMethod");

        try {
            OrderDAO orderDAO = new OrderDAO();

            // Get user ID (default to 1 if not logged in - guest user)
            int userId = (user != null && user.getId() != null) ? user.getId().intValue() : 1;

            // Create address record
            String fullAddress = address + ", " + district + ", " + province;
            int addressId = orderDAO.createAddress(userId, address, province, district, "");

            if (addressId == -1) {
                // Address creation failed
                request.setAttribute("error", "Không thể tạo địa chỉ giao hàng");
                request.getRequestDispatcher("/checkout.jsp").forward(request, response);
                return;
            }

            // Get or create order status (Pending)
            int statusId = orderDAO.getOrCreateOrderStatus("Đang xử lý");

            // Create order
            double totalAmount = cart.total();
            int orderId = orderDAO.createOrder(userId, addressId, totalAmount, statusId);

            if (orderId == -1) {
                // Order creation failed
                request.setAttribute("error", "Không thể tạo đơn hàng");
                request.getRequestDispatcher("/checkout.jsp").forward(request, response);
                return;
            }

            // Create order details for each cart item
            for (CartItem item : cart.getItems()) {
                orderDAO.createOrderDetail(
                        orderId,
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getPrice()
                );
            }

            // Generate order code
            String orderCode = generateOrderCode(orderId);
            session.setAttribute("lastOrderCode", orderCode);
            session.setAttribute("lastOrderId", orderId);

            // Clear cart after successful order
            cart.removeAllItems();
            session.setAttribute("cart", cart);

            // Redirect to success page
            response.sendRedirect(request.getContextPath() + "/order-success");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi khi xử lý đơn hàng");
            request.getRequestDispatcher("/checkout.jsp").forward(request, response);
        }
    }

    private String generateOrderCode(int orderId) {
        // Generate order code format: ORD-YYYYMMDD-XXXXX
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String orderIdStr = String.format("%05d", orderId);
        return "ORD-" + dateStr + "-" + orderIdStr;
    }
}
