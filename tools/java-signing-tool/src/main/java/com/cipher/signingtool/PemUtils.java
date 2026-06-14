package com.cipher.signingtool;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public final class PemUtils {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private PemUtils() {
    }

    public static String publicKeyToPem(PublicKey publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("Public key is required.");
        }
        return encodePem("PUBLIC KEY", publicKey.getEncoded());
    }

    public static String privateKeyToPem(PrivateKey privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key is required.");
        }
        return encodePem("PRIVATE KEY", privateKey.getEncoded());
    }

    public static byte[] decodePem(String pem, String type) {
        if (pem == null || pem.isBlank()) {
            throw new IllegalArgumentException(type + " PEM is empty.");
        }

        String begin = "-----BEGIN " + type + "-----";
        String end = "-----END " + type + "-----";

        String body = pem
                .replace(begin, "")
                .replace(end, "")
                .replaceAll("\\s", "");

        if (body.isBlank()) {
            throw new IllegalArgumentException(type + " PEM body is empty.");
        }

        try {
            return Base64.getDecoder().decode(body);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(type + " PEM is not valid Base64.", e);
        }
    }

    private static String encodePem(String type, byte[] bytes) {
        String encoded = Base64.getMimeEncoder(64, LINE_SEPARATOR.getBytes()).encodeToString(bytes);
        return "-----BEGIN " + type + "-----" + LINE_SEPARATOR
                + encoded + LINE_SEPARATOR
                + "-----END " + type + "-----" + LINE_SEPARATOR;
    }
}
