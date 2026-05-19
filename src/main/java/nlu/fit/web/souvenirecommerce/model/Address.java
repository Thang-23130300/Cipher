package nlu.fit.web.souvenirecommerce.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private int id;
    private int userId;
    private String addressDetail;
    private String ward;
    private String district;
    private String city;
    private int isDefault;
}
