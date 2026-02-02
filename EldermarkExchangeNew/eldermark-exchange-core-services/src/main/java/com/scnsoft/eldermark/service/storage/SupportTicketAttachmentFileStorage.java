package com.scnsoft.eldermark.service.storage;


import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.util.DocumentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
public class SupportTicketAttachmentFileStorage extends BaseFileStorageWithEncryption {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("DOCX", "DOC", "TIFF", "TIF", "PDF", "JPEG", "JPG", "GIF", "PNG");
    private static final int ONE_MB = 1024 * 1024;
    private static final int MAX_FILE_SIZE_MB = 20;

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    public SupportTicketAttachmentFileStorage(@Value("${support.ticket.attachment.path}") String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected String generateNewFileName(String originalFileName) {
        return addTimestampPostfixToFileName(originalFileName);
    }

    @Override
    protected void validateFile(MultipartFile file) {
        super.validateFile(file);
        DocumentUtils.validateUploadedFile(file, ALLOWED_EXTENSIONS);
        if (file.getSize() > ONE_MB * MAX_FILE_SIZE_MB) {
            throw new BusinessException("Attached file size should be less than " + MAX_FILE_SIZE_MB + " MB.");
        }
    }

    @Override
    protected DocumentEncryptionService getDocumentEncryptionService() {
        return documentEncryptionService;
    }
}
