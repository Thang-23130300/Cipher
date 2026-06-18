package com.cipher.signingtool.localapi;

import com.cipher.signingtool.ToolKeyState;

public interface SigningApiBridge {
    String getPublicKeyPem();

    boolean hasPrivateKey();

    ToolKeyState.Snapshot getKeyStateSnapshot();

    PublicKeySavedResult onPublicKeySaved(PublicKeySavedNotification notification);

    ConnectCallbackResult onConnectCallback(ConnectCallbackNotification notification);

    boolean isSha256Hex(String hashValue);

    boolean confirmSigning(SignRequest request);

    String signHashValue(String hashValue);
}
