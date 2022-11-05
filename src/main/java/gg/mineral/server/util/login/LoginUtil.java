package gg.mineral.server.util.login;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gg.mineral.server.util.collection.ArrayUtil;

public class LoginUtil {

    public static KeyPair createKeyPair() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.genKeyPair();
    }

    public static String hashSharedSecret(PublicKey publicKey, byte[] secretKeyBytes) {
        byte[][] data = ArrayUtil.of(secretKeyBytes, publicKey.getEncoded());
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            int length = data.length;

            for (int i = 0; i < length; ++i) {
                messageDigest.update(data[i]);
            }

            return new BigInteger(messageDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypts an RSA message.
     *
     * @param keyPair the key pair to use
     * @param bytes   the bytes of the encrypted message
     * @return the decrypted message
     */
    public static byte[] decryptRsa(KeyPair keyPair, byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
