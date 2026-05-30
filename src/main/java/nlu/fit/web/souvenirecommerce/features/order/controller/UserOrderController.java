package nlu.fit.web.souvenirecommerce.features.order.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.legacy.dao.OrderDAO;
import nlu.fit.web.souvenirecommerce.legacy.model.Order;
import nlu.fit.web.souvenirecommerce.legacy.model.OrderItem;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/user/orders")
public class UserOrderController extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("userInSession");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        action = (action != null) ? action.trim() : "";

        if ("detail".equals(action)) {
            viewOrderDetail(request, response, user);
        } else {
            viewOrderList(request, response, user);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(request.getContextPath() + "/user/orders");
    }

    private void viewOrderList(HttpServletRequest request,
                               HttpServletResponse response,
                               User user)
             throws ServletException, IOException {

         List<Order> orderList = orderDAO.getOrdersByUserId(user.getId().intValue());

         request.setAttribute("orderList", orderList);
         request.setAttribute("pageTitle", "Đơn hàng");
         request.setAttribute("pageCss", "account/account-layout.css");
         request.setAttribute("contentCss", "account/orders.css");
         request.setAttribute("pageJs", "account/profile.js");
         request.setAttribute("pageContent", "orders.jsp");
         request.setAttribute("contentPage", "/WEB-INF/views/account/account_layout.jsp");

         request.getRequestDispatcher("/WEB-INF/layout/base.jsp").forward(request, response);
     }

     private void viewOrderDetail(HttpServletRequest request,
                                  HttpServletResponse response,
                                  User user)
             throws ServletException, IOException {

         int orderId;
         try {
             orderId = Integer.parseInt(request.getParameter("id"));
         } catch (NumberFormatException e) {
             response.sendRedirect(request.getContextPath() + "/user/orders");
             return;
         }

         Order order = orderDAO.getOrderById(orderId);

         if (order == null || user.getId() == null || order.getUserId() != user.getId().intValue()) {
             response.sendRedirect(request.getContextPath() + "/user/orders");
             return;
         }

         List<OrderItem> orderItems = orderDAO.getOrderItems(orderId);

         request.setAttribute("order", order);
         request.setAttribute("orderItems", orderItems);
         request.setAttribute("pageTitle", "Chi tiết đơn hàng");
         request.setAttribute("pageCss", "account/account-layout.css");
         request.setAttribute("contentCss", "account/orders.css");
         request.setAttribute("pageJs", "account/profile.js");
         request.setAttribute("pageContent", "orders.jsp");
         request.setAttribute("contentPage", "/WEB-INF/views/account/account_layout.jsp");

         request.getRequestDispatcher("/WEB-INF/layout/base.jsp").forward(request, response);
     }
}
