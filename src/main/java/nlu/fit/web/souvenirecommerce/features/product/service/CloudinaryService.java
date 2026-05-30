package nlu.fit.web.souvenirecommerce.features.product.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import nlu.fit.web.souvenirecommerce.common.utils.ApplicationLoader;

import java.util.*;

public class CloudinaryService {
    private static final Cloudinary cloudinary;

    static {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", ApplicationLoader.get("cloud_name"));
        config.put("api_key", ApplicationLoader.get("cloud_api_key"));
        config.put("api_secret", ApplicationLoader.get("cloud_api_secret"));
        cloudinary = new Cloudinary(config);
    }

    // Tối ưu hóa Generics để tránh cảnh báo (warning) compile-time
    public static ApiResponse getImages(String cursor) throws Exception {
        Map<String, Object> options = new HashMap<>();
        options.put("max_results", 20);

        if (cursor != null && !cursor.trim().isEmpty()) {
            options.put("next_cursor", cursor);
        }

        return cloudinary.api().resources(options);
    }

    public static void deleteImage(String publicId) throws Exception {
        if (publicId == null || publicId.trim().isEmpty()) return;
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    /**
     * Upload hình ảnh và trả về Map chứa cả URL lẫn Public ID
     * (Cực kỳ quan trọng để lưu vào Database phục vụ cho việc XÓA hoặc SỬA ảnh sau này)
     */
    public static Map<String, String> uploadImage(byte[] fileBytes, String folderName) throws Exception {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("Dữ liệu file không được để trống.");
        }

        Map<String, Object> options = new HashMap<>();
        if (folderName != null && !folderName.trim().isEmpty()) {
            options.put("folder", folderName);
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(fileBytes, options);

        Map<String, String> result = new HashMap<>();
        result.put("url", (String) uploadResult.get("secure_url"));
        result.put("public_id", (String) uploadResult.get("public_id"));

        return result;
    }
}