package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserManualFileStorage extends BaseFileStorageWithoutEncryption {

    private static final int ONE_MB = 1024 * 1024;
    private static final int MAX_FILE_SIZE_MB = 20;

    public UserManualFileStorage(@Value("${documents.upload.usermanuals}") String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected String generateNewFileName(String originalFileName) {
        return addUuidPostfixToFileName(originalFileName);
    }

    @Override
    protected void validateFile(MultipartFile file) {
        super.validateFile(file);
        if (file.getSize() > ONE_MB * MAX_FILE_SIZE_MB) {
            throw new BusinessException("Document size should be less than " + MAX_FILE_SIZE_MB + " MB.");
        }
    }
}
