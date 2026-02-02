package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.entity.document.DocumentFileFieldsAware;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.storage.DocumentFileStorage;
import com.scnsoft.eldermark.service.storage.FileStorage;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Paths;

@Service
public class DocumentFileServiceImpl implements DocumentFileService {

    @Value("${documents.upload.basedir}")
    private String documentsUploadBaseDir;

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    @Override
    public void save(DocumentFileFieldsAware document, InputStream dataInputStream) {
        var fileStorage = getFileStorage(document);
        var fileName = getFileName(document);
        fileStorage.save(dataInputStream, fileName);
    }

    @Override
    public void delete(DocumentFileFieldsAware document) {
        var fileStorage = getFileStorage(document);
        var fileName = getFileName(document);
        fileStorage.delete(fileName);
    }

    @Override
    public InputStream loadDocument(DocumentFileFieldsAware document) {
        var fileStorage = getFileStorage(document);
        var fileName = getFileName(document);
        return fileStorage.loadAsInputStream(fileName);
    }

    @Override
    public byte[] loadDocumentAsBytes(DocumentFileFieldsAware document) {
        var fileStorage = getFileStorage(document);
        var fileName = getFileName(document);
        return fileStorage.loadAsBytes(fileName);
    }

    @Override
    public String calculateDocumentHash(DocumentFileFieldsAware document) {
        var fileStorage = getFileStorage(document);
        var fileName = getFileName(document);
        return fileStorage.hash(fileName);
    }

    @Override
    public long calculateDocumentSize(DocumentFileFieldsAware document) {
        var fileStorage = getFileStorage(document);
        var fileName = getFileName(document);
        return fileStorage.size(fileName);
    }

    private FileStorage getFileStorage(DocumentFileFieldsAware document) {
        return getFileStorage(
            document.getAuthorOrganizationAlternativeId(),
            document.getAuthorLegacyId()
        );
    }

    private String getFileName(DocumentFileFieldsAware document) {
        return getFileName(document.getUuid());
    }

    private String getFileName(String uuid) {
        return "file_" + uuid;
    }

    @SuppressFBWarnings(
        value = "PATH_TRAVERSAL_IN",
        justification = "authorLegacyId is internal id, not user-supplied."
            + "organizationAlternativeId depends on organization name, but organization name is choosen by super admin"
    )
    private FileStorage getFileStorage(String authorOrganizationAlternativeId, String authorLegacyId) {
        return new DocumentFileStorage(Paths.get(
            documentsUploadBaseDir,
            "database_" + authorOrganizationAlternativeId + "_user_" + authorLegacyId
        ), documentEncryptionService);
    }
}
