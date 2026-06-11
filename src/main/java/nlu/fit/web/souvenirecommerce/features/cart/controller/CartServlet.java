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

@WebServlet(urlPatterns = {"/cart", "/user/cart"})
public class CartServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init() throws ServletException {
        cartService = new CartService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Cart cart = cartService.getCartForDisplay(session);

        request.setAttribute("cart", cart);
        request.setAttribute("pageTitle", "Giỏ hàng của bạn - INOLA");
        request.setAttribute("pageCss", "ShoppingCart.css");
        request.setAttribute("contentCss", "cart.css");
        request.setAttribute("pageJs", "ShoppingCart.js");
        request.setAttribute("contentPage", "/WEB-INF/views/cart/cart.jsp");
        request.getRequestDispatcher("/WEB-INF/layout/base.jsp").forward(request, response);
    }

}
