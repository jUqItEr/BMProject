package com.bmstore.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * @author  jUqItEr (pyt773924@gmail.com)
 * @version 1.0.1
 * */
public class HashAlgorithm {
    /*
     * @description The universal hash algorithm.
     *              The algorithm will create a hash value by changing the second parameter.
     * @author      jUqItEr (pyt773924@gmail.com)
     * */
    public static String makeHash(String plainText, String algorithmName) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithmName);
        StringBuilder sb = new StringBuilder();
        String result;
        byte[] hash;

        md.update(plainText.getBytes());
        hash = md.digest();

        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        result = sb.toString();

        return result;
    }
}