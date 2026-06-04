package nlu.fit.web.souvenirecommerce.features.product.service;

import nlu.fit.web.souvenirecommerce.legacy.dao.ReviewDAO;
import nlu.fit.web.souvenirecommerce.legacy.model.Review;

import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO = new ReviewDAO();

    public List<Review> getReviews(Long productId, Integer rating, String sort, int offset, int limit) {
        return reviewDAO.getReviewsByProductWithFilter(productId, rating, sort, offset, limit);
    }

    public boolean canReview(int userId, Long productId) {
        return reviewDAO.hasPurchased(userId, productId);
    }

    public boolean addReview(Review review) {
        boolean success = reviewDAO.addReview(review);

        if (success) {reviewDAO.refreshProductReviewStats(review.getProductId());
        }

        return success;
    }
}