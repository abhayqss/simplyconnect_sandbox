package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.service.DocumentEncryptionService;

import java.nio.file.Path;

public abstract class BaseFileStorageWithEncryption extends BaseFileStorage {

    public BaseFileStorageWithEncryption(Path storageLocation) {
        super(storageLocation);
    }

    public BaseFileStorageWithEncryption(String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected byte[] encrypt(byte[] decrypted) {
        return getDocumentEncryptionService().encrypt(decrypted);
    }

    @Override
    protected byte[] decrypt(byte[] encrypted) {
        return getDocumentEncryptionService().decrypt(encrypted);
    }

    protected abstract DocumentEncryptionService getDocumentEncryptionService();
}
