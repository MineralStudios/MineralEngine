package gg.mineral.server.util.login;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;

import javax.crypto.Cipher;

import lombok.SneakyThrows;
import lombok.val;

public class LoginUtil {

    @SneakyThrows
    public static KeyPair createKeyPair(int size) {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(size);
        return keyPairGenerator.genKeyPair();
    }

    @SneakyThrows
    public static String hashSharedSecret(PublicKey publicKey, byte[] secretKeyBytes) {
        val data = new byte[][] { secretKeyBytes, publicKey.getEncoded() };
        val messageDigest = MessageDigest.getInstance("SHA-1");
        int length = data.length;

        for (int i = 0; i < length; ++i)
            messageDigest.update(data[i]);

        return new BigInteger(messageDigest.digest()).toString(16);
    }

    /**
     * Decrypts an RSA message.
     *
     * @param keyPair the key pair to use
     * @param bytes   the bytes of the encrypted message
     * @return the decrypted message
     */
    @SneakyThrows
    public static byte[] decryptRsa(KeyPair keyPair, byte[] bytes) {
        val cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return cipher.doFinal(bytes);
    }

}
