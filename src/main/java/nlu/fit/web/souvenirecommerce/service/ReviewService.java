package nlu.fit.web.souvenirecommerce.service;

import nlu.fit.web.souvenirecommerce.dao.ReviewDAO;
import nlu.fit.web.souvenirecommerce.model.Review;

import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO = new ReviewDAO();

    public List<Review> getReviews(
            Long productId,
            Integer rating,
            String sort,
            int offset,
            int limit
    ) {
        return reviewDAO.getReviewsByProductWithFilter(
                productId, rating, sort, offset, limit
        );
    }
    public boolean canReview(int userId, Long productId) {
        return reviewDAO.hasPurchased(userId, productId);
    }

    public boolean addReview(Review review) {
        return reviewDAO.addReview(review);
    }
}
