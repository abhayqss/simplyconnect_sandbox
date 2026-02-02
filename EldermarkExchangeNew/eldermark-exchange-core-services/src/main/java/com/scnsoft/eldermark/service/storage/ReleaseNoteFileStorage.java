package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.util.DocumentUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
public class ReleaseNoteFileStorage extends BaseFileStorageWithoutEncryption {

    private final Set<String> ALLOWED_EXTENSIONS = Set.of("DOCX", "PDF", "DOC");

    public ReleaseNoteFileStorage(@Value("${documents.upload.releasenote}") String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected String generateNewFileName(String originalFileName) {
        return addUuidPostfixToFileName(originalFileName);
    }

    @Override
    protected void validateFile(MultipartFile file) {
        super.validateFile(file);
        DocumentUtils.validateUploadedFile(file, ALLOWED_EXTENSIONS);
    }
}
