package nlu.fit.web.souvenirecommerce.service;

import nlu.fit.web.souvenirecommerce.Enums.ProductSort;
import nlu.fit.web.souvenirecommerce.dao.*;
import nlu.fit.web.souvenirecommerce.dao.ProductDAO;
import nlu.fit.web.souvenirecommerce.dto.ProductCardDTO;
import nlu.fit.web.souvenirecommerce.dto.ProductTypeDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Category;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.model.Promotion;
import nlu.fit.web.souvenirecommerce.util.ProductCardMapper;

import java.util.ArrayList;
import java.util.List;

public class ProductTypeService {

    private static final int PAGE_SIZE = 12;

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PromotionDAO promotionDAO = new PromotionDAO();


    public ProductTypeDTO getProductType(
            Long categoryId,
            Integer minPrice,
            Integer maxPrice,
            Integer rating,
            ProductSort sort,
            int page
    ) {
        Category category = categoryDAO.getCategoryById(categoryId);
        if (category == null) return null;

        int offset = (page - 1) * PAGE_SIZE;

        List<Product> products =
                productDAO.getProductsByCategoryWithFilter(
                        categoryId,
                        minPrice,
                        maxPrice,
                        rating,
                        sort,
                        offset,
                        PAGE_SIZE
                );

        int totalProducts =
                productDAO.countProductsByCategoryWithFilter(
                        categoryId,
                        minPrice,
                        maxPrice,
                        rating
                );

        int totalPages =
                (int) Math.ceil((double) totalProducts / PAGE_SIZE);
        List<ProductCardDTO> cards = new ArrayList<>();
        ProductTypeDTO dto = new ProductTypeDTO();
        dto.setCategory(category);
        for (Product p : products) {
            Promotion promo = promotionDAO.getActivePromotionByProductId(p.getId());
            cards.add(ProductCardMapper.from(p, promo));
        }

        dto.setProducts(cards);
        dto.setCurrentPage(page);
        dto.setTotalPages(totalPages);
        dto.setTotalProducts(totalProducts);
        dto.setMinPrice(minPrice);
        dto.setMaxPrice(maxPrice);
        dto.setRating(rating);
        dto.setSort(sort);
        dto.setSortParam(sort != null ? sort.name().toLowerCase() : "popular");

        return dto;
    }
}
