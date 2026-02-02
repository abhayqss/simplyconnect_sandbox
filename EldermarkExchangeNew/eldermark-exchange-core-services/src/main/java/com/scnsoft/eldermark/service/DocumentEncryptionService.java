package com.scnsoft.eldermark.service;

public interface DocumentEncryptionService {
    byte[] decrypt(byte[] encrypted);
    byte[] encrypt(byte[] decrypted);
}
