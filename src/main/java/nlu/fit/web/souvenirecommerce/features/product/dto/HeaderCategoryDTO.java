package nlu.fit.web.souvenirecommerce.features.product.dto;

public class HeaderCategoryDTO {

    private Long id;

    private String name;

    private String image;

    private Integer productCount;

    public HeaderCategoryDTO() {
    }

    public HeaderCategoryDTO(Long id, String name, String image, Integer productCount) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.productCount = productCount;
    }

    public Long getId() {
        return id;
    }

    public HeaderCategoryDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public HeaderCategoryDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public HeaderCategoryDTO setImage(String image) {
        this.image = image;
        return this;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public HeaderCategoryDTO setProductCount(Integer productCount) {
        this.productCount = productCount;
        return this;
    }
}