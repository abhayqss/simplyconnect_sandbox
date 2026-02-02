package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.service.DocumentEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReferralAttachmentFileStorage extends BaseFileStorageWithEncryption {

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    public ReferralAttachmentFileStorage(@Value("${referral.attachment.path}") String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected String generateNewFileName(String originalFileName) {
        return addTimestampPostfixToFileName(originalFileName);
    }

    @Override
    protected DocumentEncryptionService getDocumentEncryptionService() {
        return documentEncryptionService;
    }
}
