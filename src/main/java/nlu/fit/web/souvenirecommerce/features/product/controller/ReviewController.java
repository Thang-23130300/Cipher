package nlu.fit.web.souvenirecommerce.features.product.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nlu.fit.web.souvenirecommerce.features.product.service.ReviewService;
import nlu.fit.web.souvenirecommerce.legacy.model.Review;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;

@WebServlet("/reviews")
public class ReviewController extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() {
        reviewService = new ReviewService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

        if (page < 1) {
            page = 1;
        }

        if (size < 1 || size > 20) {
            size = 5;
        }

        int offset = (page - 1) * size;

        List<Review> reviews = reviewService.getReviews(productId, rating, sort, offset, size);

        request.setAttribute("reviews", reviews);

        request.getRequestDispatcher("ReviewItem.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        Integer userId = getCurrentUserId(request);

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\":\"Vui lòng đăng nhập để đánh giá\"}");
            return;
        }

        Long productId = parseLong(request.getParameter("productId"), -1L);
        Integer rating = parseInt(request.getParameter("rating"), null);
        String comment = request.getParameter("comment");

        if (productId == -1L) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Sản phẩm không hợp lệ\"}");
            return;
        }

        if (rating == null || rating < 1 || rating > 5) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Số sao đánh giá không hợp lệ\"}");
            return;
        }

        if (comment == null || comment.trim().length() < 5) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Nội dung đánh giá quá ngắn\"}");
            return;
        }

        if (!reviewService.canReview(userId, productId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"message\":\"Bạn chỉ có thể đánh giá sản phẩm đã mua\"}");
            return;
        }

        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment.trim());

        boolean success = reviewService.addReview(review);

        if (!success) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"Không thể gửi đánh giá\"}");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\":\"Đã gửi đánh giá\"}");
    }

    private Integer parseInt(String val, Integer def) {
        try {
            return val != null && !val.isBlank() ? Integer.parseInt(val) : def;
        } catch (Exception e) {
            return def;
        }
    }

    private Long parseLong(String val, Long def) {
        try {
            return val != null && !val.isBlank() ? Long.parseLong(val) : def;
        } catch (Exception e) {
            return def;
        }
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        String[] commonKeys = {"user", "currentUser", "authUser", "userInSession", "loggedInUser", "account"};

        for (String key : commonKeys) {
            Integer userId = extractUserId(session.getAttribute(key));

            if (userId != null) {
                return userId;
            }
        }

        Enumeration<String> names = session.getAttributeNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Integer userId = extractUserId(session.getAttribute(name));

            if (userId != null) {
                return userId;
            }
        }

        return null;
    }

    private Integer extractUserId(Object userObj) {
        if (userObj == null) {
            return null;
        }

        try {
            Method getIdMethod = userObj.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(userObj);

            if (id instanceof Number) {
                return ((Number) id).intValue();
            }

            if (id instanceof String) {
                return Integer.parseInt((String) id);
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}