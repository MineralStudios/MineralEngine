package gg.mineral.server.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.SecretKey;

public class EncryptionUtil {
    public static byte[] encrypt(String string, PublicKey publicKey, SecretKey secretKey) {
        try {
            return encrypt("SHA-1", string.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    private static byte[] encrypt(String algorithm, byte[]... data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[][] bytes = data;
            int length = data.length;

            for (int i = 0; i < length; ++i) {
                messageDigest.update(bytes[i]);
            }

            return messageDigest.digest();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
            return null;
        }
    }
}
