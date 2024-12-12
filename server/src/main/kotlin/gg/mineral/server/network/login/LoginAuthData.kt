package gg.mineral.server.network.login

import gg.mineral.server.util.login.LoginUtil
import java.util.concurrent.ThreadLocalRandom

class LoginAuthData {
    val keyPair = LoginUtil.createKeyPair(1024)
    val verifyToken = ByteArray(4)

    init {
        ThreadLocalRandom.current().nextBytes(verifyToken)
    }
}
