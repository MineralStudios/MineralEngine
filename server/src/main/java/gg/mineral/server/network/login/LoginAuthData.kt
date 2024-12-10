package gg.mineral.server.network.login

import gg.mineral.server.util.login.LoginUtil
import lombok.Value
import java.security.KeyPair
import java.util.concurrent.ThreadLocalRandom

@Value
class LoginAuthData {
    private val keyPair: KeyPair = LoginUtil.createKeyPair(1024)
    private val verifyToken = ByteArray(4)

    init {
        ThreadLocalRandom.current().nextBytes(verifyToken)
    }
}
