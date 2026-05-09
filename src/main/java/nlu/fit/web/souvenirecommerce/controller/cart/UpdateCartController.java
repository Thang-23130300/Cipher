package nlu.fit.web.souvenirecommerce.controller.cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.model.cart.Cart;
import nlu.fit.web.souvenirecommerce.model.cart.CartItem;

import java.io.IOException;

public class UpdateCartController extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity  = Integer.parseInt(request.getParameter("quantity"));

            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute("cart");
            
            if (cart == null) {
                response.getWriter().write("{\"success\":false}");
                return;
            }

            if (quantity <= 0) {
                cart.removeItem(productId);
            } else {
                cart.updateItem(productId, quantity);
            }
            
            session.setAttribute("cart", cart);

            CartItem item = cart.getItem(productId);
            double itemSubtotal = (item != null) ? item.getSubTotal() : 0;

            String json = """
            {
              "success": true,
              "totalQuantity": %d,
              "total": %.0f,
              "itemSubtotal": %.0f
            }
            """.formatted(
                    cart.totalQuantity(),
                    cart.total(),
                    itemSubtotal
            );

            response.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false}");
        }
    }
     
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
