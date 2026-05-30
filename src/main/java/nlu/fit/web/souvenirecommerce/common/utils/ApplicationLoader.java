package nlu.fit.web.souvenirecommerce.common.utils;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationLoader {
    static Properties prop = new Properties();

    static {
        try {
            InputStream is = ApplicationLoader.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties");

            if (is == null) {
                throw new RuntimeException("application.properties not found");
            }

            prop.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties getProperties() {
        return prop;
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }

}
