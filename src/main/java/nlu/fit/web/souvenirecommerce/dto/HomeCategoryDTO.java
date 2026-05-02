package nlu.fit.web.souvenirecommerce.dto;

import nlu.fit.web.souvenirecommerce.model.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class HomeCategoryDTO {

    private Category category;
    private List<ProductCardDTO> productCards;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ProductCardDTO> getProductCards() {
        return productCards;
    }

    public void setProductCards(List<ProductCardDTO> productCards) {
        this.productCards = productCards;
    }

    public static List<HomeCategoryDTO> fromCategories(List<Category> categories) {

        List<HomeCategoryDTO> list = new ArrayList<>();
        if (categories == null) return list;

        for (Category c : categories) {
            HomeCategoryDTO dto = new HomeCategoryDTO();
            dto.setCategory(c);
            list.add(dto);
        }
        return list;
    }
}
