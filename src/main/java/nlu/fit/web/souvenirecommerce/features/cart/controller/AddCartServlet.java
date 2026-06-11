package nlu.fit.web.souvenirecommerce.features.cart.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.cart.model.Cart;
import nlu.fit.web.souvenirecommerce.features.cart.service.CartService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "AddCart", value = "/cart/add")
public class AddCartServlet extends HttpServlet {
    private final CartService cartService = new CartService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Long productId;
        int quantity;

        try {
            productId = Long.parseLong(request.getParameter("productId"));
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (Exception e) {
            handleFailure(request, response);
            return;
        }

        if (!cartService.addItem(session, productId, quantity)) {
            handleFailure(request, response);
            return;
        }

        Cart cart = cartService.getCartForDisplay(session);

        if ("true".equals(request.getParameter("buyNow"))) {
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        if (isAjax(request)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("""
                    {
                      "success": true,
                      "message": "Đã thêm sản phẩm vào giỏ hàng",
                      "cartCount": %d
                    }
                    """.formatted(cart.totalQuantity()));
            return;
        }

        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : request.getContextPath() + "/cart");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/home");
    }

    private void handleFailure(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (isAjax(request)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("""
                    {
                      "success": false,
                      "message": "Không thể thêm sản phẩm vào giỏ hàng"
                    }
                    """);
            return;
        }

        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : request.getContextPath() + "/home");
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
