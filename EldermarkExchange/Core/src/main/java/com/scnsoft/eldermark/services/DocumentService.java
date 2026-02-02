package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import org.springframework.data.domain.Pageable;

import java.io.*;
import java.util.Date;
import java.util.List;

public interface DocumentService {
    List<Document> queryForDocuments(Resident resident, Employee requestingEmployee);

    List<Document> queryForDocuments(Resident resident, String filter, Employee requestingEmployee,
                                     int offset, int limit);

    List<Document> queryForDocuments(Resident resident, String filter, Employee requestingEmployee,
                                     Pageable pageable);

    Long getCustomDocumentCount(Resident resident, String filter, Employee requestingEmployee);

    List<Document> queryForDocuments(Resident resident, Employee requestingEmployee, List<Long> orSharedWith, boolean visibleOnly);

    Document saveDocument(DocumentMetadata metadata, Resident resident, Employee author,
                      boolean isSharedWithAll, List<Database> databasesToShareWith,
                      SaveDocumentCallback callback, Boolean isCloud, byte[] fileContent) throws IOException;


    Document saveDocumentFromXds(DocumentMetadata metadata, Resident resident, Employee author,
                          String uuid, String uniqueId,
                          SaveDocumentCallback callback) throws IOException;

    void deleteDocument(long id, boolean couldBeRestored) throws IOException;

    void deleteDocumentFromXds(long id) throws IOException;

    void restoreDocument(long id) throws IOException;

    void restoreDocument(long id, boolean includeOptOutResident) throws IOException;

    Document findDocument(long id);

    Document findDocument(long id, boolean visibleOnly);
    
    Document findDocument(long id, boolean visibleOnly, boolean includeOptOutResident);

    Document findDocumentByUniqueId(String uniqueId);
    Document findDocumentByUniqueIdOrThrow(String uniqueId) ;

    Resident getResident(Document document);
    
    Resident getResident(Document document, boolean includeOptOutResident);

    Long getResident(long documentId);

    Employee getAuthor(Document document);

    @Deprecated
    //use readDocument
    File getDocumentFile(Document document);

    @Deprecated
    //use readDocument
    InputStream getDocumentInputStream(Document document) throws FileNotFoundException;

    void updateDocument(Document document) throws IOException;

    void synchronizeAllDocumentsWithXdsRegistry(Writer writer, Date fromTime) throws IOException;

    List<Document> setIsCdaDocument(List<Document> document);

    void setIsCdaDocument(Document document);

    boolean defineIsCdaDocument(Document document);

    byte[] readDocument(Document document);
}
