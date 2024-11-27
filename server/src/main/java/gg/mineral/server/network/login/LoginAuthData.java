package gg.mineral.server.network.login;

import java.security.KeyPair;
import java.util.Random;

import gg.mineral.server.util.login.LoginUtil;
import lombok.Value;

@Value
public class LoginAuthData {
    private static final Random RANDOM = new Random();
    private KeyPair keyPair;
    private byte[] verifyToken = new byte[4];

    public LoginAuthData() {
        this.keyPair = LoginUtil.createKeyPair(1024);
        RANDOM.nextBytes(verifyToken);
    }
}
