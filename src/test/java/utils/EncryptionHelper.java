package utils;

import java.util.Base64;

/**
 * Utility class for encrypting and decrypting sensitive data.
 * Uses Base64 encoding to protect passwords stored in Excel.
 */
public class EncryptionHelper {

    /**
     * Encrypts a plain text string using Base64 encoding.
     */
    public static String encrypt(String plainText) {
        return Base64.getEncoder().encodeToString(plainText.getBytes());
    }

    /**
     * Decrypts a Base64 encoded string back to plain text.
     */
    public static String decrypt(String encryptedText) {
        return new String(Base64.getDecoder().decode(encryptedText));
    }
}