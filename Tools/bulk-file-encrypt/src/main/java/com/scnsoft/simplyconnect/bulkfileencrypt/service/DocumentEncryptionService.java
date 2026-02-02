package com.scnsoft.simplyconnect.bulkfileencrypt.service;

public interface DocumentEncryptionService {
    void encryptAllFiles(String path, Boolean checkIfAlreadyEncrypted);
}
