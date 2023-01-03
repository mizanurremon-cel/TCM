package com.cel.tcm.Utils;


import android.content.Context;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils {
    public static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final String DELIMITER = "]";

    private static final int KEY_LENGTH = 128;

    private static final int ITERATION_COUNT = 1000;

    private static final int SALT_LENGTH = 8;

    private static SecureRandom random = new SecureRandom();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    public static String encrypt(Context context, String plaintext)
            throws Exception {
        byte[] salt = generateSalt();
        return encrypt(plaintext, getKey(salt, getPassword(context)), salt);
    }

    private static String encrypt(String plaintext, SecretKey key, byte[] salt)
            throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");

            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            if (salt != null) {
                return String.format("%s%s%s%s%s",
                        new String(Base64.encode(salt)), DELIMITER, new String(
                                Base64.encode(iv)), DELIMITER, new String(
                                Base64.encode(cipherText)));
            }

            return String.format("%s%s%s", new String(Base64.encode(iv)),
                    DELIMITER, new String(Base64.encode(cipherText)));
        } catch (Throwable e) {
            throw new Exception("Error while encryption", e);
        }
    }

    public static String decrypt(Context context, String ciphertext)
            throws Exception {
        return decrypt(ciphertext, getPassword(context));
    }

    private static String decrypt(String ciphertext, String password)
            throws Exception {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }
        try {
            byte[] salt = Base64.decode(fields[0]);
            byte[] iv = Base64.decode(fields[1]);
            byte[] cipherBytes = Base64.decode(fields[2]);

            SecretKey key = getKey(salt, password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] plaintext = cipher.doFinal(cipherBytes);
            String plainrStr = new String(plaintext, "UTF-8");

            return plainrStr;
        } catch (Throwable e) {
            throw new Exception("Error while decryption", e);
        }
    }

    private static String getPassword(Context context) {

        return "My secret password";
    }

    private static SecretKey getKey(byte[] salt, String password)
            throws Exception {
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                    ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(
                    KEY_DERIVATION_ALGORITHM, "BC");
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Throwable e) {
            throw new Exception("Error while generating key", e);
        }
    }

    private static byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);

        return b;
    }

    private static byte[] generateSalt() {
        byte[] b = new byte[SALT_LENGTH];
        random.nextBytes(b);

        return b;
    }
}
