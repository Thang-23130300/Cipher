package nlu.fit.web.souvenirecommerce.common.utils;

import nlu.fit.web.souvenirecommerce.features.product.dto.ProductCardDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.model.entity.Promotion;

public class ProductCardMapper {

    public static ProductCardDTO from(Product p, Promotion promo) {

        ProductCardDTO dto = new ProductCardDTO();

        dto.setId(p.getId());
        dto.setName(p.getName());

        if (p.getImage() != null && !p.getImage().isBlank()) {
            dto.setImage(p.getImage());
        } else {
            dto.setImage("/assets/images/products/default.png");
        }

        dto.setTotalSold(p.getTotalSold());
        dto.setAvgRating(p.getAvgRating());
        dto.setReviewCount(p.getReviewCount());

        double originalPrice = p.getOriginalPrice();

        if (promo != null && promo.getDiscountPercent() > 0) {

            int percent = promo.getDiscountPercent();
            double salePrice = originalPrice * (100 - percent) / 100.0;

            dto.setDiscountPercent(percent);
            dto.setOriginalPrice(originalPrice);
            dto.setDiscountedPrice(salePrice);

            dto.setPrice(salePrice);

        } else {
            dto.setDiscountPercent(null);
            dto.setDiscountedPrice(null);
            dto.setOriginalPrice(originalPrice);

            dto.setPrice(originalPrice);
        }

        return dto;
    }

    public static ProductCardDTO from(Product p, nlu.fit.web.souvenirecommerce.legacy.model.Promotion promo) {
        ProductCardDTO dto = new ProductCardDTO();

        dto.setId(p.getId());
        dto.setName(p.getName());

        if (p.getImage() != null && !p.getImage().isBlank()) {
            dto.setImage(p.getImage());
        } else {
            dto.setImage("/assets/images/products/default.png");
        }

        dto.setTotalSold(p.getTotalSold());
        dto.setAvgRating(p.getAvgRating());
        dto.setReviewCount(p.getReviewCount());

        double originalPrice = p.getOriginalPrice();

        if (promo != null && promo.getDiscountPercent() > 0) {
            int percent = promo.getDiscountPercent();
            double salePrice = originalPrice * (100 - percent) / 100.0;

            dto.setDiscountPercent(percent);
            dto.setOriginalPrice(originalPrice);
            dto.setDiscountedPrice(salePrice);
            dto.setPrice(salePrice);
        } else {
            dto.setDiscountPercent(null);
            dto.setDiscountedPrice(null);
            dto.setOriginalPrice(originalPrice);
            dto.setPrice(originalPrice);
        }

        return dto;
    }
}
