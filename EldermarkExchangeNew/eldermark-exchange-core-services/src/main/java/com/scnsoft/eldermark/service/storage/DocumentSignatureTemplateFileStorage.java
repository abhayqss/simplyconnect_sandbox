package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class DocumentSignatureTemplateFileStorage extends BaseFileStorageWithEncryption {

    private static final long MAX_SIZE = 20 * 1024 * 1024; // 20 MB

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    public DocumentSignatureTemplateFileStorage(@Value("${signature.file.template.path}") String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected void validateFile(MultipartFile file) {
        super.validateFile(file);
        if (!MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType()) || file.getSize() > MAX_SIZE) {
            throw new ValidationException("Supported file types: PDF | Max 20mb");
        }
    }

    @Override
    protected String generateNewFileName(String originalFileName) {
        return UUID.randomUUID().toString();
    }

    @Override
    protected DocumentEncryptionService getDocumentEncryptionService() {
        return documentEncryptionService;
    }
}
