package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.*;

import java.util.List;

public interface AuditLoggingService {
    public void logLogin(Long employeeId);

    public void logLogout(Long employeeId);

    public void logPatientDiscovery(Long employeeId, List<Long> residentIds);

    public void logQueryForDocuments(Long employeeId, Long residentId, List<Long> documentIds);

    public void logFilterDocuments(Long employeeId, Long residentId, List<Long> documentIds);

    public void logDocumentView(Long employeeId, Long documentId);

    public void logDocumentDownload(Long employeeId, Long documentId);

    public void logDocumentUpload(Long employeeId, Long documentId);

    public void logDocumentDelete(Long employeeId, Long documentId);

    public void logCCDGenerateAndView(Long employeeId, Long residentId);

    public void logCCDGenerateAndDownload(Long employeeId, Long residentId);

    public void logFacesheetGenerateAndView(Long employeeId, Long residentId);

    public void logFacesheetGenerateAndDownload(Long employeeId, Long residentId);

    public void logOperation(AuditLog entry);
}
