package nlu.fit.web.souvenirecommerce.features.product.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.http.Part;
import nlu.fit.web.souvenirecommerce.common.utils.ApplicationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CloudinaryService {
    private static final Cloudinary cloudinary;

    static {
        String cloudName = ApplicationLoader.get("cloud_name");
        String apiKey = ApplicationLoader.get("cloud_api_key");
        String apiSecret = ApplicationLoader.get("cloud_api_secret");

        if (isBlank(cloudName) || isBlank(apiKey) || isBlank(apiSecret)) {
            cloudinary = null;
        } else {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret
            ));
        }
    }

    public static boolean isConfigured() {
        return cloudinary != null;
    }

    public static ApiResponse getImages(String cursor) throws Exception {
        requireConfigured();

        Map<String, Object> options = new HashMap<>();
        options.put("max_results", 20);

        if (cursor != null && !cursor.trim().isEmpty()) {
            options.put("next_cursor", cursor);
        }

        return cloudinary.api().resources(options);
    }

    public static boolean deleteImage(String publicId) throws Exception {
        if (isBlank(publicId) || !isConfigured()) return false;

        Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return "ok".equals(result.get("result"));
    }

    public static Map<String, String> uploadImage(byte[] fileBytes, String folderName) throws Exception {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("Dữ liệu file không được để trống.");
        }

        return upload(fileBytes, folderName, null);
    }

    public static Map<String, String> uploadImage(InputStream inputStream, String folderName, String publicId) throws Exception {
        if (inputStream == null) {
            throw new IllegalArgumentException("Dữ liệu file không được để trống.");
        }

        return upload(inputStream.readAllBytes(), folderName, publicId);
    }

    public static Map<String, String> uploadAvatar(Part filePart, Long userId) throws Exception {
        if (filePart == null || filePart.getSize() == 0) {
            throw new IllegalArgumentException("Vui lòng chọn một file ảnh.");
        }

        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File tải lên phải là hình ảnh.");
        }

        String publicId = "avatar_user_" + userId + "_" + System.currentTimeMillis();
        try (InputStream inputStream = filePart.getInputStream()) {
            return uploadImage(inputStream, "avatars", publicId);
        }
    }

    private static Map<String, String> upload(Object file, String folderName, String publicId) throws Exception {
        requireConfigured();

        Map<String, Object> options = new HashMap<>();
        if (folderName != null && !folderName.trim().isEmpty()) {
            options.put("folder", folderName);
        }
        if (!isBlank(publicId)) {
            options.put("public_id", publicId);
        }
        options.put("resource_type", "image");

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file, options);

        Map<String, String> result = new HashMap<>();
        result.put("url", (String) uploadResult.get("secure_url"));
        result.put("public_id", (String) uploadResult.get("public_id"));

        return result;
    }

    private static void requireConfigured() throws IOException {
        if (!isConfigured()) {
            throw new IOException("Cloudinary chưa được cấu hình.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
