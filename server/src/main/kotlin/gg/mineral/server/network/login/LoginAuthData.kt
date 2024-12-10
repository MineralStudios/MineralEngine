package gg.mineral.server.network.login

import gg.mineral.server.util.login.LoginUtil
import java.security.KeyPair
import java.util.concurrent.ThreadLocalRandom

class LoginAuthData {
    val keyPair: KeyPair = LoginUtil.createKeyPair(1024)
    val verifyToken = ByteArray(4)

    init {
        ThreadLocalRandom.current().nextBytes(verifyToken)
    }
}
