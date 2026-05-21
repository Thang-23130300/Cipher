package nlu.fit.web.souvenirecommerce.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.dao.CategoryDAO;
import nlu.fit.web.souvenirecommerce.model.entity.Category;

import java.io.IOException;
import java.util.List;

//@WebFilter("/*")
public class HeaderFilter implements Filter {

    private CategoryDAO categoryDAO;

    @Override
    public void init(FilterConfig filterConfig) {
        categoryDAO = new CategoryDAO();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("authUser") != null) {
            request.setAttribute("authUser", session.getAttribute("authUser"));
        }

        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        List<Category> topCategories = categoryDAO.getTopSellingCategories(6);
        request.setAttribute("topCategories", topCategories);

        if (session != null && session.getAttribute("cartItemCount") != null) {
            request.setAttribute("cartItemCount", session.getAttribute("cartItemCount"));
        } else {
            request.setAttribute("cartItemCount", 0);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
