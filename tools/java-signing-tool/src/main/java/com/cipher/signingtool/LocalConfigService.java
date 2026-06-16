package com.cipher.signingtool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

public class LocalConfigService {
    private static final String CONFIG_DIR_NAME = ".cipher-signing-tool";
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String LAST_PRIVATE_KEY_PATH = "lastPrivateKeyPath";

    private final Path configFile;

    public LocalConfigService() {
        this(Path.of(System.getProperty("user.home"), CONFIG_DIR_NAME, CONFIG_FILE_NAME));
    }

    LocalConfigService(Path configFile) {
        this.configFile = configFile;
    }

    public Optional<Path> getLastPrivateKeyPath() {
        String value = loadProperties().getProperty(LAST_PRIVATE_KEY_PATH);
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(Path.of(value.trim()));
    }

    public void saveLastPrivateKeyPath(Path privateKeyPath) {
        if (privateKeyPath == null) {
            return;
        }

        Properties properties = loadProperties();
        properties.setProperty(LAST_PRIVATE_KEY_PATH, privateKeyPath.toAbsolutePath().normalize().toString());
        try {
            Files.createDirectories(configFile.getParent());
            try (OutputStream outputStream = Files.newOutputStream(configFile)) {
                properties.store(outputStream, "Java Signing Tool local config");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not save local config: " + e.getMessage(), e);
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        if (!Files.exists(configFile)) {
            return properties;
        }

        try (InputStream inputStream = Files.newInputStream(configFile)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read local config: " + e.getMessage(), e);
        }
    }
}
