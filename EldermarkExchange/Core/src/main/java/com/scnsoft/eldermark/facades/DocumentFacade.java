package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.services.Report;
import com.scnsoft.eldermark.services.SaveDocumentCallback;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.shared.DocumentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

public interface DocumentFacade {
    List<DocumentDto> queryForDocuments(Long residentId);

    @Deprecated
    Document saveDocument(DocumentMetadata metadata, long residentId, long authorId, boolean isSharedWithAll,
                      List<Long> idsOfDatabasesToShareWith, SaveDocumentCallback callback);

    @Deprecated
    Document saveDocument(DocumentMetadata metadata, long residentId, long authorId, boolean isSharedWithAll,
                      List<Long> idsOfDatabasesToShareWith, boolean includeOptOutResident, SaveDocumentCallback callback, Boolean isCloud);

    Document saveDocument(DocumentMetadata metadata, long residentId, long authorId, boolean isSharedWithAll,
                          List<Long> idsOfDatabasesToShareWith, boolean includeOptOutResident, SaveDocumentCallback callback, Boolean isCloud, byte[] fileContent);

    @Deprecated
    Document saveDocument(DocumentMetadata metadata, long residentId, long authorId, boolean isSharedWithAll,
                          List<Long> idsOfDatabasesToShareWith, File document);

    void deleteDocument(long id);

    void deleteDocument(long id, boolean couldBeRestored);

    void restoreDocument(long id);

    void restoreDocument(long id, boolean includeOptOutResident);

    Page<DocumentDto> queryForDocuments(Long residentId, String filter, int offset, int limit);

    Page<DocumentDto> queryForDocuments(Long residentId, String filter, Pageable pageable, boolean aggregated);

    long getCustomDocumentCount(Long residentId, String filter, boolean aggregated);

    long getDocumentCount(Long residentId, String filter, boolean aggregated);

    boolean isAttachedToResident(Long residentId, Long documentId);

    boolean isAttachedToMergedResidents(Long residentId, Long documentId);

    DocumentBean findDocument(long id);

    DocumentBean findDocument(long documentId, long employeeId, List<Long> orSharedWith, boolean visibleOnly, boolean includeOptOutResident);

    void updateDocumentTitle(long documentId, long employeeId, List<Long> orSharedWith, String title, boolean includeOptOutResident);

    void updateDocumentSharingPolicy(long documentId, long employeeId, List<Long> orSharedWith, List<Long> idsOfDatabasesToShareWith, boolean includeOptOutResident);

    void downloadOrViewReport(Long mainResidentId, List<Long> residentIds, String reportType, HttpServletResponse response, boolean isViewMode);

    void downloadOrViewReport(Long mainResidentId, List<Long> residentIds, String reportType, HttpServletResponse response, boolean isViewMode, Integer timeZoneOffsetInMinutes);

    void downloadOrViewReport(Long residentId, String reportType, HttpServletResponse response, boolean isViewMode, Boolean aggregated);

    void downloadOrViewReport(Long residentId, String reportType, HttpServletResponse response, boolean isViewMode, Boolean aggregated, Integer timeZoneOffsetInMinutes);

    Report generateReport(Long residentId, boolean aggregated, String reportType);

    void downloadOrViewCustomDocument(DocumentBean document, HttpServletResponse response, boolean isViewMode);

    List<Document> setIsCdaDocument(List<Document> documents);

    void setIsCdaDocument(Document document);

}
