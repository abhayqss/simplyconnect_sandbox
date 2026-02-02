package com.scnsoft.simplyconnect.bulkfileencrypt.service;

public interface EncryptedDocumentService {
    boolean isEncrypted(String path);
    void markEncrypted(String path);
}
