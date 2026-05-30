package nlu.fit.web.souvenirecommerce.legacy.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    private int id;
    private Long productId;
    private int discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public boolean isActive(LocalDateTime now) {
        return (startDate == null || !now.isBefore(startDate))
                && (endDate == null || !now.isAfter(endDate));
    }
}
