package nlu.fit.web.souvenirecommerce.service;

import nlu.fit.web.souvenirecommerce.dao.HomeDAO;
import nlu.fit.web.souvenirecommerce.dao.ProductDAO;
import nlu.fit.web.souvenirecommerce.dao.PromotionDAO;
import nlu.fit.web.souvenirecommerce.dto.HomeCategoryDTO;
import nlu.fit.web.souvenirecommerce.dto.HomePageDTO;
import nlu.fit.web.souvenirecommerce.dto.ProductCardDTO;
import nlu.fit.web.souvenirecommerce.model.entity.Product;
import nlu.fit.web.souvenirecommerce.model.Promotion;
import nlu.fit.web.souvenirecommerce.util.ProductCardMapper;

import java.util.ArrayList;
import java.util.List;

public class HomeService {

    private final HomeDAO homeDAO = new HomeDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final PromotionDAO promotionDAO = new PromotionDAO();

    private static final int BANNER_LIMIT = 5;
    private static final int TOP_CATEGORY_LIMIT = 5;
    private static final int EXTENSION_LIMIT = 6;
    private static final int CATEGORY_PRODUCT_LIMIT = 4;
    private static final int TOP_RATED_LIMIT = 4;
    private static final int NEWEST_LIMIT = 4;

    public HomePageDTO getHomePageData() {

        HomePageDTO dto = new HomePageDTO();

        /* Banner */
        dto.setBannerCategories(HomeCategoryDTO.fromCategories(homeDAO.getBannerCategories(BANNER_LIMIT)));

        /*Top category */
        dto.setTopCategorySections(HomeCategoryDTO.fromCategories(homeDAO.getTopCategoriesWithProducts(TOP_CATEGORY_LIMIT, CATEGORY_PRODUCT_LIMIT)));

        /* Extension*/
        dto.setExtensionSections(HomeCategoryDTO.fromCategories(homeDAO.getExtensionCategories(EXTENSION_LIMIT)));

        /* Top rated */
        dto.setTopRatedProductCards(mapToProductCardDTOs(productDAO.getTopRatedProducts(TOP_RATED_LIMIT)));

        /* Newest */
        dto.setNewestProductCards(mapToProductCardDTOs(productDAO.getNewestProducts(NEWEST_LIMIT)));

        /* Map */
        dto.getTopCategorySections().forEach(section -> section.setProductCards(mapToProductCardDTOs(section.getCategory().getProducts())));
        return dto;
    }

    /* Map helper*/
    private List<ProductCardDTO> mapToProductCardDTOs(List<Product> products) {

        List<ProductCardDTO> cards = new ArrayList<>();
        if (products == null || products.isEmpty()) return cards;

        for (Product p : products) {
            Promotion promo =
                    promotionDAO.getActivePromotionByProductId(p.getId());

            cards.add(ProductCardMapper.from(p, promo));
        }
        return cards;
    }
}
