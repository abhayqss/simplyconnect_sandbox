package com.scnsoft.eldermark.service.storage;

import java.nio.file.Path;

public abstract class BaseFileStorageWithoutEncryption extends BaseFileStorage {

    public BaseFileStorageWithoutEncryption(Path storageLocation) {
        super(storageLocation);
    }

    public BaseFileStorageWithoutEncryption(String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected byte[] encrypt(byte[] decrypted) {
        return decrypted;
    }

    @Override
    protected byte[] decrypt(byte[] encrypted) {
        return encrypted;
    }

}
