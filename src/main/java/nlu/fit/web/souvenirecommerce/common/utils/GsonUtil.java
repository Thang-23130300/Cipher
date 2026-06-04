package nlu.fit.web.souvenirecommerce.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson Utility Helper
 * Provides centralized Gson instance with custom configurations
 * 
 * @author Development Team
 * @version 1.0
 */
public final class GsonUtil {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private GsonUtil() {
        // Prevent instantiation
    }

    /**
     * Get Gson instance
     * @return Configured Gson instance
     */
    public static Gson getGson() {
        return gson;
    }
}
