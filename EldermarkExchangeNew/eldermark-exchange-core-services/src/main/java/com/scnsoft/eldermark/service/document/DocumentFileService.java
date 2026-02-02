package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.entity.document.DocumentFileFieldsAware;

import java.io.InputStream;

public interface DocumentFileService {

    void save(DocumentFileFieldsAware document, InputStream dataInputStream);

    void delete(DocumentFileFieldsAware document);

    InputStream loadDocument(DocumentFileFieldsAware document);

    byte[] loadDocumentAsBytes(DocumentFileFieldsAware document);

    String calculateDocumentHash(DocumentFileFieldsAware document);

    long calculateDocumentSize(DocumentFileFieldsAware document);
}
