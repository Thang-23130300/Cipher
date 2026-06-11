package nlu.fit.web.souvenirecommerce.features.order.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.common.enums.PaymentMethod;
import nlu.fit.web.souvenirecommerce.features.cart.model.Cart;
import nlu.fit.web.souvenirecommerce.features.cart.service.CartPersistenceService;
import nlu.fit.web.souvenirecommerce.features.order.dto.CheckoutException;
import nlu.fit.web.souvenirecommerce.features.order.dto.CheckoutRequest;
import nlu.fit.web.souvenirecommerce.features.order.dto.CheckoutResult;
import nlu.fit.web.souvenirecommerce.features.order.service.CheckoutService;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutController extends HttpServlet {
    private final CheckoutService checkoutService = new CheckoutService();
    private final CartPersistenceService cartPersistenceService = new CartPersistenceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = getCurrentUser(session);

        if (user == null) {
            HttpSession newSession = request.getSession();
            newSession.setAttribute("redirectAfterLogin", request.getContextPath() + "/checkout");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Cart cart = getCart(session);
        if (cart == null || cart.totalQuantity() == 0) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        prepareCheckoutPage(request, user);
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = getCurrentUser(session);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Cart cart = getCart(session);
        if (cart == null || cart.totalQuantity() == 0) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        try {
            CheckoutResult result = checkoutService.checkout(user, cart, buildCheckoutRequest(request));
            cart.removeAllItems();
            session.setAttribute("cart", cart);
            session.setAttribute("cartItemCount", 0);
            cartPersistenceService.saveCart(user, cart);
            session.setAttribute("lastOrderCode", result.getOrderCode());
            session.setAttribute("lastOrderId", result.getOrder().getId());

            if (result.requiresExternalPayment()) {
                response.sendRedirect(result.getPaymentUrl());
                return;
            }
            response.sendRedirect(request.getContextPath() + "/order-success");
        } catch (CheckoutException e) {
            request.setAttribute("error", e.getMessage());
            prepareCheckoutPage(request, user);
            request.getRequestDispatcher("/checkout.jsp").forward(request, response);
        }
    }

    private void prepareCheckoutPage(HttpServletRequest request, User user) {
        request.setAttribute("currentUser", user);
        request.setAttribute("savedAddresses", checkoutService.getUserAddresses(user.getId()));
        request.setAttribute("provinceOptions", checkoutService.getProvinces());
    }

    private CheckoutRequest buildCheckoutRequest(HttpServletRequest request) {
        return CheckoutRequest.builder()
                .savedAddressId(parseLong(request.getParameter("savedAddressId")))
                .receiverName(request.getParameter("receiverName"))
                .receiverPhone(request.getParameter("receiverPhone"))
                .addressDetail(request.getParameter("addressDetail"))
                .provinceCode(parseInteger(request.getParameter("provinceCode")))
                .wardCode(parseInteger(request.getParameter("wardCode")))
                .note(request.getParameter("note"))
                .paymentMethod(parsePaymentMethod(request.getParameter("paymentMethod")))
                .build();
    }

    private PaymentMethod parsePaymentMethod(String value) {
        if (value == null || value.isBlank()) {
            return PaymentMethod.COD;
        }
        try {
            return PaymentMethod.valueOf(value);
        } catch (IllegalArgumentException e) {
            return PaymentMethod.COD;
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Cart getCart(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object cart = session.getAttribute("cart");
        return cart instanceof Cart ? (Cart) cart : null;
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute("userInSession");
        if (user instanceof User) {
            return (User) user;
        }
        user = session.getAttribute("user");
        if (user instanceof User) {
            return (User) user;
        }
        user = session.getAttribute("currentUser");
        if (user instanceof User) {
            return (User) user;
        }
        user = session.getAttribute("authUser");
        return user instanceof User ? (User) user : null;
    }
}
