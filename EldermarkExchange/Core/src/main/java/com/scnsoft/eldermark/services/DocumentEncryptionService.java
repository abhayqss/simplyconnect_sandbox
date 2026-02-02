package com.scnsoft.eldermark.services;

public interface DocumentEncryptionService {
    byte[] decrypt(byte[] encrypted);
    byte[] encrypt(byte[] decrypted);
}
