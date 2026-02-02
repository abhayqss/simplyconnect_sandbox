package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.service.DocumentEncryptionService;

import java.nio.file.Path;

public class DocumentFileStorage extends BaseFileStorageWithEncryption {

    private DocumentEncryptionService documentEncryptionService;

    public DocumentFileStorage(Path storageLocation, DocumentEncryptionService documentEncryptionService) {
        super(storageLocation);
        this.documentEncryptionService = documentEncryptionService;
    }

    @Override
    protected DocumentEncryptionService getDocumentEncryptionService() {
        return documentEncryptionService;
    }
}
