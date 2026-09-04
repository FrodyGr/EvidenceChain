package io.github.frodygr.evidencechain.core.merkle;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Standardized cryptographic hashing utilities for SHA-256 Merkle calculations.
 */
public final class HashUtils {

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    private HashUtils() {}

    public static String sha256(String input) {
        if (input == null) {
            return sha256("");
        }
        return sha256(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm must be present in JDK", e);
        }
    }

    public static String combine(String leftHash, String rightHash) {
        return sha256(leftHash + rightHash);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
