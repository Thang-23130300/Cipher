package com.cipher.signingtool.localapi;

public interface SigningApiBridge {
    String getPublicKeyPem();

    boolean hasPrivateKey();

    boolean isSha256Hex(String hashValue);

    boolean confirmSigning(SignRequest request);

    String signHashValue(String hashValue);
}
