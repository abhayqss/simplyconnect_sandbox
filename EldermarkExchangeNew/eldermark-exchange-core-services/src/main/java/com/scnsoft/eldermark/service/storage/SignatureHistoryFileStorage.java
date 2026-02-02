package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.service.DocumentEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SignatureHistoryFileStorage extends BaseFileStorageWithEncryption {

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    public SignatureHistoryFileStorage(@Value("${signature.file.historyStorage.base}") String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected DocumentEncryptionService getDocumentEncryptionService() {
        return documentEncryptionService;
    }
}
