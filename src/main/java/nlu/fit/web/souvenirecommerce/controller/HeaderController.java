package nlu.fit.web.souvenirecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.service.HeaderService;

import java.io.IOException;
import java.util.List;

@WebServlet("/header")
public class HeaderController extends HttpServlet {

    private HeaderService headerService;

    @Override
    public void init() {
        headerService = new HeaderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Category> categories = headerService.getAllCategories();
        List<Category> topCategories = headerService.getTopCategories(5);

        request.setAttribute("categories", categories);
        request.setAttribute("topCategories", topCategories);

        HttpSession session = request.getSession(false);
        if (session != null) {
            request.setAttribute("authUser",
                    (User) session.getAttribute("authUser"));
        }
    }
}
