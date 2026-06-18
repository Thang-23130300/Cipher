package com.cipher.signingtool;

public final class SigningStatePolicy {
    private SigningStatePolicy() {
    }

    public enum Readiness {
        READY,
        GENERATED_PUBLIC_KEY_NOT_UPLOADED,
        FILE_PRIVATE_KEY_NOT_CHECKED,
        KEY_MISMATCH
    }

    public static Readiness evaluate(
            boolean generatedInCurrentSession,
            boolean publicKeyUploadedToWeb,
            boolean webPublicKeyLoaded,
            boolean keyMatchChecked,
            boolean keyPairMatched
    ) {
        if (keyPairMatched) {
            return Readiness.READY;
        }
        if (generatedInCurrentSession && !publicKeyUploadedToWeb) {
            return Readiness.GENERATED_PUBLIC_KEY_NOT_UPLOADED;
        }
        if (!generatedInCurrentSession && (!webPublicKeyLoaded || !keyMatchChecked)) {
            return Readiness.FILE_PRIVATE_KEY_NOT_CHECKED;
        }
        return Readiness.KEY_MISMATCH;
    }
}
