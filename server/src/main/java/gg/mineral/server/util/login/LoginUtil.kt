package gg.mineral.server.util.login

import lombok.SneakyThrows
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PublicKey
import javax.crypto.Cipher

object LoginUtil {
    @SneakyThrows
    fun createKeyPair(size: Int): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(size)
        return keyPairGenerator.genKeyPair()
    }

    @SneakyThrows
    fun hashSharedSecret(publicKey: PublicKey, secretKeyBytes: ByteArray): String {
        val data = arrayOf(secretKeyBytes, publicKey.encoded)
        val messageDigest = MessageDigest.getInstance("SHA-1")
        val length = data.size

        for (i in 0..<length) messageDigest.update(data[i])

        return BigInteger(messageDigest.digest()).toString(16)
    }

    /**
     * Decrypts an RSA message.
     *
     * @param keyPair the key pair to use
     * @param bytes   the bytes of the encrypted message
     * @return the decrypted message
     */
    @SneakyThrows
    fun decryptRsa(keyPair: KeyPair, bytes: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, keyPair.private)
        return cipher.doFinal(bytes)
    }
}
