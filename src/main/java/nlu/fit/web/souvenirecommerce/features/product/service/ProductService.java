package nlu.fit.web.souvenirecommerce.features.product.service;

import nlu.fit.web.souvenirecommerce.common.utils.ProductCardMapper;
import nlu.fit.web.souvenirecommerce.features.product.dto.ProductCardDTO;
import nlu.fit.web.souvenirecommerce.features.product.dto.ProductDetailDTO;
import nlu.fit.web.souvenirecommerce.features.product.repository.ProductRepository;
import nlu.fit.web.souvenirecommerce.features.product.repository.ProductSpecificationRepository;
import nlu.fit.web.souvenirecommerce.features.product.repository.PromotionRepository;
import nlu.fit.web.souvenirecommerce.features.product.repository.ReviewRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.model.entity.Promotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductService {
    private static final int RELATED_PRODUCT_LIMIT = 5;

    private final ProductRepository productRepository = new ProductRepository();
    private final PromotionRepository promotionRepository = new PromotionRepository();
    private final ProductSpecificationRepository specificationRepository = new ProductSpecificationRepository();
    private final ReviewRepository reviewRepository = new ReviewRepository();

    public ProductDetailDTO getProductDetail(Long productId) {
        Product product = productRepository.findDetailById(productId).orElse(null);
        if (product == null) {
            return null;
        }

        Category category = product.getCategory();
        Promotion promotion = promotionRepository.findBestActiveByProductId(productId).orElse(null);
        long totalReviews = reviewRepository.countByProductId(productId);
        double avgRating = reviewRepository.avgRatingByProductId(productId);
        Map<Integer, Integer> ratingCount = reviewRepository.countByRating(productId);

        List<Product> relatedProducts = productRepository.findRelatedProducts(
                category == null ? null : category.getId(),
                productId,
                RELATED_PRODUCT_LIMIT
        );
        List<ProductCardDTO> relatedCards = mapRelatedProducts(relatedProducts);

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProduct(product);
        dto.setCategory(category);
        dto.setPromotion(promotion);
        dto.setSpecifications(specificationRepository.findByProductId(productId));
        dto.setAvgRating(avgRating);
        dto.setTotalReviews((int) totalReviews);
        dto.setRatingCount(ratingCount);
        dto.setRelatedProductCards(relatedCards);

        if (promotion != null && promotion.getDiscountPercent() > 0) {
            double discounted = product.getOriginalPrice() * (100 - promotion.getDiscountPercent()) / 100.0;
            dto.setDiscountedPrice(discounted);
        }

        return dto;
    }

    private List<ProductCardDTO> mapRelatedProducts(List<Product> relatedProducts) {
        if (relatedProducts == null || relatedProducts.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = relatedProducts.stream()
                .map(Product::getId)
                .toList();
        Map<Long, Promotion> promotions = promotionRepository.findBestActiveByProductIds(productIds);

        List<ProductCardDTO> relatedCards = new ArrayList<>();
        for (Product relatedProduct : relatedProducts) {
            relatedCards.add(ProductCardMapper.from(relatedProduct, promotions.get(relatedProduct.getId())));
        }
        return relatedCards;
    }
}
