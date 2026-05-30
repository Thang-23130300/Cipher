package nlu.fit.web.souvenirecommerce.legacy.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSummary {
    private int totalReviews;
    private double avgRating;
}
