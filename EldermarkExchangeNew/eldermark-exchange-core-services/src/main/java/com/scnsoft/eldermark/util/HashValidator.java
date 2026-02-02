package com.scnsoft.eldermark.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public abstract class HashValidator {

    public static final String MD5_HASH = "MD5";

    private HashValidator() {
    }

    @SuppressFBWarnings(value = "UNSAFE_HASH_EQUALS", justification = "")
    public static boolean matches(String hashAlgorithm, byte[] src, byte[] hash) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            md.update(src);
            byte[] digest = md.digest();

            return Arrays.equals(hash, digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
