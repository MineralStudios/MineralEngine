package gg.mineral.server.network.login;

import java.security.KeyPair;
import java.util.Random;

import gg.mineral.server.util.login.LoginUtil;

public class LoginAuthData {
    KeyPair keyPair;
    byte[] verifyToken = new byte[4];

    public LoginAuthData() {
        keyPair = LoginUtil.createKeyPair();
        new Random().nextBytes(verifyToken);
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

}
