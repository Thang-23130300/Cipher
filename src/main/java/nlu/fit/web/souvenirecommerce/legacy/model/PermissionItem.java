package nlu.fit.web.souvenirecommerce.legacy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionItem implements Serializable {
    private Long id;
    private String resource;
    private String action;
    private String description;
}
