package gg.mineral.server.network.login;

import java.security.KeyPair;
import java.util.Random;

import gg.mineral.server.util.login.LoginUtil;
import lombok.Getter;

public class LoginAuthData {
    @Getter
    KeyPair keyPair;
    @Getter
    byte[] verifyToken = new byte[4];

    public LoginAuthData() {
        keyPair = LoginUtil.createKeyPair();
        new Random().nextBytes(verifyToken);
    }

}
