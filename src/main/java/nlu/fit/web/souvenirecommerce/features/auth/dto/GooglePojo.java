package nlu.fit.web.souvenirecommerce.features.auth.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class GooglePojo implements Serializable {

    @SerializedName(value = "id", alternate = {"sub"})
    private String id;
    private String email;
    private boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
}