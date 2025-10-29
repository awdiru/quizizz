package ru.programm_shcool.quizizz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class EncryptionService {
    private static final int IV_LENGTH = 16;

    @Value("${encryption.transformation}")
    private String transformation;

    @Value("${encryption.algorithm}")
    private String algorithm;

    @Value("${encryption.key}")
    private String secretKey;

    private SecretKeySpec getSecretKeySpec() {
        return getSecretKeySpec("");
    }

    private SecretKeySpec getSecretKeySpec(String salt) {
        String key = secretKey.substring(0, secretKey.length() - salt.length()) + salt;

        if (key.length() != 32) {
            throw new IllegalArgumentException("Invalid key. Must be 32 chars");
        }
        return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
    }


    public String encrypt(String input) {
        return encrypt(input, "");
    }

    public String encrypt(String input, String salt) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            IvParameterSpec ivSpec = generateIv();

            cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(salt), ivSpec);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(
                    concatenateByteArrays(ivSpec.getIV(), encrypted)
            );
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage());
        }
    }

    public String decrypt(String input) {
        return decrypt(input, "");
    }

    public String decrypt(String input, String salt) {
        try {
            byte[] decoded = Base64.getDecoder().decode(input);

            byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(decoded, IV_LENGTH, decoded.length);

            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(salt), new IvParameterSpec(iv));

            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage());
        }
    }

    private IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
