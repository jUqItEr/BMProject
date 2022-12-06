package com.bm.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashAlgorithm {
    public static String makeHash(String plainText, String algorithmName) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithmName);
        StringBuilder sb = new StringBuilder();
        String result = null;
        byte[] hash = null;

        md.update(plainText.getBytes());
        hash = md.digest();

        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        result = sb.toString();

        return result;
    }
}