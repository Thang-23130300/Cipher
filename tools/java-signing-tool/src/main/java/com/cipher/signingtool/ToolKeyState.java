package com.cipher.signingtool;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public final class ToolKeyState {
    private PrivateKey currentPrivateKey;
    private PublicKey currentPublicKey;
    private PublicKey currentWebPublicKey;
    private boolean generatedInCurrentSession;
    private boolean publicKeyUploadedToWeb;
    private boolean webPublicKeyLoaded;
    private boolean keyMatchChecked;
    private boolean keyPairMatched;

    public synchronized void useGeneratedKeyPair(KeyPair keyPair) {
        if (keyPair == null) {
            throw new IllegalArgumentException("Key pair is required.");
        }
        currentPrivateKey = keyPair.getPrivate();
        currentPublicKey = keyPair.getPublic();
        currentWebPublicKey = null;
        generatedInCurrentSession = true;
        publicKeyUploadedToWeb = false;
        webPublicKeyLoaded = false;
        keyMatchChecked = false;
        keyPairMatched = false;
    }

    public synchronized void useLoadedPrivateKey(PrivateKey privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key is required.");
        }
        currentPrivateKey = privateKey;
        currentPublicKey = null;
        currentWebPublicKey = null;
        generatedInCurrentSession = false;
        publicKeyUploadedToWeb = false;
        webPublicKeyLoaded = false;
        keyMatchChecked = false;
        keyPairMatched = false;
    }

    public synchronized void useWebPublicKey(PublicKey webPublicKey) {
        if (webPublicKey == null) {
            throw new IllegalArgumentException("Web public key is required.");
        }
        currentWebPublicKey = webPublicKey;
        currentPublicKey = webPublicKey;
        publicKeyUploadedToWeb = true;
        webPublicKeyLoaded = true;
        keyMatchChecked = false;
        keyPairMatched = false;
    }

    public synchronized void markGeneratedPublicKeyUploaded(PublicKey savedWebPublicKey) {
        if (currentPrivateKey == null || currentPublicKey == null || savedWebPublicKey == null) {
            throw new IllegalStateException("Generated key pair and saved web public key are required.");
        }
        currentWebPublicKey = currentPublicKey;
        generatedInCurrentSession = true;
        publicKeyUploadedToWeb = true;
        webPublicKeyLoaded = true;
        keyMatchChecked = true;
        keyPairMatched = true;
    }

    public synchronized void setKeyMatchResult(boolean matches) {
        keyMatchChecked = true;
        keyPairMatched = matches;
    }

    public synchronized SavedPublicKeyResult applySavedWebPublicKey(PublicKey savedWebPublicKey) {
        if (savedWebPublicKey == null) {
            throw new IllegalArgumentException("Saved web public key is required.");
        }

        currentWebPublicKey = savedWebPublicKey;
        publicKeyUploadedToWeb = true;
        webPublicKeyLoaded = true;

        if (currentPublicKey == null) {
            keyMatchChecked = false;
            keyPairMatched = false;
            return SavedPublicKeyResult.NO_CURRENT_PUBLIC_KEY;
        }

        boolean matches = Arrays.equals(currentPublicKey.getEncoded(), savedWebPublicKey.getEncoded());
        keyMatchChecked = true;
        keyPairMatched = matches;
        if (matches) {
            generatedInCurrentSession = true;
            return SavedPublicKeyResult.MATCHED;
        }
        return SavedPublicKeyResult.MISMATCHED;
    }

    public synchronized Snapshot snapshot() {
        return new Snapshot(
                currentPrivateKey,
                currentPublicKey,
                currentWebPublicKey,
                generatedInCurrentSession,
                publicKeyUploadedToWeb,
                webPublicKeyLoaded,
                keyMatchChecked,
                keyPairMatched
        );
    }

    public record Snapshot(
            PrivateKey currentPrivateKey,
            PublicKey currentPublicKey,
            PublicKey currentWebPublicKey,
            boolean generatedInCurrentSession,
            boolean publicKeyUploadedToWeb,
            boolean webPublicKeyLoaded,
            boolean keyMatchChecked,
            boolean keyPairMatched
    ) {
    }

    public enum SavedPublicKeyResult {
        MATCHED,
        MISMATCHED,
        NO_CURRENT_PUBLIC_KEY
    }
}
