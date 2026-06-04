package nlu.fit.web.souvenirecommerce.features.product.service;

import nlu.fit.web.souvenirecommerce.legacy.dao.*;
import nlu.fit.web.souvenirecommerce.features.product.dto.ProductCardDTO;
import nlu.fit.web.souvenirecommerce.features.product.dto.ProductDetailDTO;
import nlu.fit.web.souvenirecommerce.legacy.model.*;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.common.utils.ProductCardMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PromotionDAO promotionDAO = new PromotionDAO();
    private final ProductSpecificationDAO specificationDAO = new ProductSpecificationDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public ProductDetailDTO getProductDetail(Long productId) {
        Product product = productDAO.getProductById(productId);
        if (product == null) return null;

        Category category = categoryDAO.getCategoryById(product.getCategory().getId());
        Promotion promotion = promotionDAO.getActivePromotionByProductId(productId);

        ReviewSummary summary = reviewDAO.getReviewSummaryByProductId(productId);
        Map<String, Integer> ratingCount = reviewDAO.countReviewsByRating(productId);

        for (int i = 1; i <= 5; i++) {
            ratingCount.putIfAbsent(String.valueOf(i), 0);
        }
        List<Product> relatedProducts = productDAO.getRelatedProducts(product.getCategory().getId(), productId, 5);

        List<ProductCardDTO> relatedCards = new ArrayList<>();

        if (relatedProducts != null && !relatedProducts.isEmpty()) {
            for (Product rp : relatedProducts) {
                Promotion promo = promotionDAO.getActivePromotionByProductId(rp.getId());

                relatedCards.add(ProductCardMapper.from(rp, promo));
            }
        }

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProduct(product);
        dto.setCategory(category);
        dto.setPromotion(promotion);
        dto.setSpecifications(specificationDAO.getByProductId(productId));

        if (summary != null) {
            dto.setAvgRating(summary.getAvgRating());
            dto.setTotalReviews(summary.getTotalReviews());
        } else {
            dto.setAvgRating(0);
            dto.setTotalReviews(0);
        }

        dto.setRatingCount(ratingCount);
        dto.setRelatedProductCards(relatedCards);

        if (promotion != null) {
            double discounted = product.getOriginalPrice() * (100 - promotion.getDiscountPercent()) / 100.0;
            dto.setDiscountedPrice(discounted);
        }

        return dto;
    }
}
