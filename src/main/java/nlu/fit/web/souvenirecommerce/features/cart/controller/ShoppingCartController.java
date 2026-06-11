package nlu.fit.web.souvenirecommerce.features.cart.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.cart.model.Cart;
import nlu.fit.web.souvenirecommerce.features.cart.service.CartPersistenceService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;

@WebServlet(name = "ShoppingCartController", value = "/cart")
public class ShoppingCartController extends HttpServlet {
    private final CartPersistenceService cartPersistenceService = new CartPersistenceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        User user = getCurrentUser(session);

        if (session == null || user == null) {
            HttpSession loginSession = request.getSession(true);
            loginSession.setAttribute("redirectAfterLogin",
                    request.getContextPath() + "/cart");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Cart cart = (Cart) session.getAttribute("cart");
  
        if (cart == null) {
            cart = cartPersistenceService.loadCart(user);
            session.setAttribute("cart", cart);
            session.setAttribute("cartItemCount", cart.totalQuantity());
        }

        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/cart.jsp").forward(request, response);
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object user = session.getAttribute("userInSession");

        if (user instanceof User currentUser) {
            return currentUser;
        }

        user = session.getAttribute("authUser");

        return user instanceof User currentUser ? currentUser : null;
    }

}
