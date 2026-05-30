package nlu.fit.web.souvenirecommerce.legacy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import nlu.fit.web.souvenirecommerce.model.entity.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionGroup implements Serializable {
    private Long id;
    private String name;
    private String description;
    private boolean system;

    @Default
    private List<PermissionItem> permissions = new ArrayList<>();

    @Default
    private List<User> users = new ArrayList<>();

    private int userCount;
}
