package nlu.fit.web.souvenirecommerce.features.cart.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.cart.model.Cart;
import nlu.fit.web.souvenirecommerce.features.cart.model.CartItem;

import java.io.IOException;

@WebServlet("/cart/remove")
public class RemoveCartController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (session == null) {
            handleResponse(response, isAjax, false, 0, 0);
            return;
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            handleResponse(response, isAjax, false, 0, 0);
            return;
        }

        try {
            Long productId = Long.parseLong(request.getParameter("productId"));
            CartItem removedItem = cart.removeItem(productId);
            session.setAttribute("cart", cart);

            if (removedItem != null) {
                handleResponse(
                        response,
                        isAjax,
                        true,
                        cart.totalQuantity(),
                        cart.total()
                );
            } else {
                handleResponse(response, isAjax, false, cart.totalQuantity(), cart.total());
            }

        } catch (Exception e) {
            handleResponse(response, isAjax, false, cart.totalQuantity(), cart.total());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        doPost(request, response);
    }


    private void handleResponse(HttpServletResponse response,
                                boolean isAjax,
                                boolean success,
                                int totalQty,
                                double total) throws IOException {

        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String json = """
            {
              "success": %b,
              "totalQuantity": %d,
              "total": %.0f
            }
            """.formatted(success, totalQty, total);

            response.getWriter().write(json);
        } else {
            response.sendRedirect("/cart");
        }
    }
}
