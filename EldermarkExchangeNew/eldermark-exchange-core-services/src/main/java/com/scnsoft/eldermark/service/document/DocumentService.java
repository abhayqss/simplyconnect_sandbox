package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.basic.HistoryIdsAware;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.DocumentEditableData;
import com.scnsoft.eldermark.entity.document.DocumentFileFieldsAware;
import com.scnsoft.eldermark.service.ProjectingService;

import java.io.InputStream;
import java.time.Instant;

public interface DocumentService extends ProjectingService<Long> {

    Document findDocumentById(Long id);

    void temporaryDelete(long id, Employee curEmployee);

    void temporaryDelete(Document document, Employee curEmployee);

    void restore(long id, Employee curEmployee);

    void markInvisible(long id, Employee curEmployee);

    InputStream readDocument(DocumentFileFieldsAware document);

    byte[] readDocumentAsBytes(DocumentFileFieldsAware document);

    void deleteDocumentFile(DocumentFileFieldsAware document);

    String calculateDocumentHash(DocumentFileFieldsAware document);

    boolean existsByCategory(HistoryIdsAware category);

    Long edit(DocumentEditableData documentEditableData);

    void temporaryDeleteDocumentsInFolderIfNotAlreadyTemporaryOrPermanentlyDeleted(Long folderId, Employee curEmployee, Instant temporaryDeletionTime);

    void permanentlyDeleteDocumentsInFolderIfNotAlreadyPermanentlyDeleted(Long folderId, Employee curEmployee, Instant deletionTime);

    void restoreDocumentsInFolderIfTemporaryDeletedAtTime(Long folderId, Employee restoredByEmployee, Instant restorationTime, Instant initialFolderTemporaryDeletedTime);

    <P> P findDocumentById(Long id, Class<P> projection);
}
