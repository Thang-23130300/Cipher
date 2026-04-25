package nlu.fit.web.souvenirecommerce.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "original_price")
    private double originalPrice;

    @Column(name = "discount_percent")
    private int discountPercent;

    @Column(name = "sale_price")
    private Double salePrice;

    private String image;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @Column(name = "total_sold")
    private int totalSold;

    @Column(name = "avg_rating")
    private double avgRating;

    @Column(name = "review_count")
    private int reviewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}