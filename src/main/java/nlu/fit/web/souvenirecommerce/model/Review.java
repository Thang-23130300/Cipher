package nlu.fit.web.souvenirecommerce.model;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private int id;
    private Long productId;
    private int userId;
    private String userName;
    private int rating;
    private String comment;
    private Timestamp createdAt;
}
