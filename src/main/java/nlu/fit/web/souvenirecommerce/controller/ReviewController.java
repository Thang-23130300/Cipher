package nlu.fit.web.souvenirecommerce.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.model.Review;
import nlu.fit.web.souvenirecommerce.model.User;
import nlu.fit.web.souvenirecommerce.service.ReviewService;

import java.io.IOException;
import java.util.List;

@WebServlet("/reviews")
public class ReviewController extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() {
        reviewService = new ReviewService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        Long productId = parseLong(request.getParameter("productId"), -1L);
        if (productId == -1L) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Integer rating = null;
        String ratingParam = request.getParameter("rating");
        if (ratingParam != null && !ratingParam.isBlank()) {
            rating = parseInt(ratingParam, null);
        }

        String sort = request.getParameter("sort");
        if (sort == null || sort.isBlank()) {
            sort = "newest";
        }

        int page = parseInt(request.getParameter("page"), 1);
        int size = parseInt(request.getParameter("size"), 5);
        int offset = (page - 1) * size;

        List<Review> reviews =
                reviewService.getReviews(productId, rating, sort, offset, size);

        request.setAttribute("reviews", reviews);

        request.getRequestDispatcher("ReviewItem.jsp")
                .forward(request, response);
    }

    private Integer parseInt(String val, Integer def) {
        try {
            return val != null ? Integer.parseInt(val) : def;
        } catch (Exception e) {
            return def;
        }
    }

    private Long parseLong(String val, Long def) {
        try {
            return val != null ? Long.parseLong(val) : def;
        } catch (Exception e) {
            return def;
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = ((User) session.getAttribute("user")).getId();

        Long productId = parseLong(request.getParameter("productId"), -1L);
        int rating = parseInt(request.getParameter("rating"), 0);
        String comment = request.getParameter("comment");

        if (productId <= 0 || rating < 1 || rating > 5 || comment == null || comment.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!reviewService.canReview(userId, productId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }


        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment);

        reviewService.addReview(review);

        response.setStatus(HttpServletResponse.SC_OK);
    }

}
