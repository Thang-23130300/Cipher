package nlu.fit.web.souvenirecommerce.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import nlu.fit.web.souvenirecommerce.util.ApplicationLoader;

import java.util.*;

public class CloudinaryService {
    private static Cloudinary cloudinary;

    static {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", ApplicationLoader.get("cloud_name"));
        config.put("api_key", ApplicationLoader.get("cloud_api_key"));
        config.put("api_secret", ApplicationLoader.get("cloud_api_secret"));
        cloudinary = new Cloudinary(config);
    }

    public static ApiResponse getImages(String cursor) throws Exception {
        Map options = ObjectUtils.asMap(
                "max_results", 20
        );

        if (cursor != null && !cursor.isEmpty()) {
            options.put("next_cursor", cursor);
        }

        return cloudinary.api().resources(options);
    }

    public static void deleteImage(String publicId) throws Exception {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public static String uploadImage(byte[] fileBytes) throws Exception {
        Map uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }
}