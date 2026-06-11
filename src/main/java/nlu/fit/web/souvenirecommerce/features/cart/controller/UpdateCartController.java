package nlu.fit.web.souvenirecommerce.features.cart.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.cart.model.Cart;
import nlu.fit.web.souvenirecommerce.features.cart.model.CartItem;
import nlu.fit.web.souvenirecommerce.features.cart.service.CartService;

import java.io.IOException;

@WebServlet(name = "UpdateCartController", value = "/cart/update")
public class UpdateCartController extends HttpServlet {
    private final CartService cartService = new CartService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Long productId = Long.parseLong(request.getParameter("productId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            HttpSession session = request.getSession();
            boolean success = cartService.updateItem(session, productId, quantity);
            Cart cart = cartService.getCartForDisplay(session);
            CartItem item = cart.getItem(productId);
            double itemSubtotal = item != null ? item.getSubTotal() : 0;

            String json = """
                    {
                      "success": %b,
                      "totalQuantity": %d,
                      "total": %.0f,
                      "itemSubtotal": %.0f
                    }
                    """.formatted(
                    success,
                    cart.totalQuantity(),
                    cart.total(),
                    itemSubtotal
            );

            response.getWriter().write(json);
        } catch (Exception e) {
            response.getWriter().write("{\"success\":false}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
