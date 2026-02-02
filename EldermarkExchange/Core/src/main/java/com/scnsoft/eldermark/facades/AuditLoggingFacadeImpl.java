package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.AuditLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class AuditLoggingFacadeImpl implements AuditLoggingFacade {
    @Autowired
    private AuditLoggingService auditLoggingService;

    @Override
    public void logLogin(Long employeeId) {
        auditLoggingService.logLogin(employeeId);
    }

    @Override
    public void logPatientDiscovery(Long employeeId, List<Long> residentIds) {
        auditLoggingService.logPatientDiscovery(employeeId, residentIds);
    }

    @Override
    public void logQueryForDocuments(Long employeeId, Long residentId, List<Long> documentIds) {
        auditLoggingService.logQueryForDocuments(employeeId, residentId, documentIds);
    }

    @Override
    public void logFilterDocuments(Long employeeId, Long residentId, List<Long> documentIds) {
        auditLoggingService.logFilterDocuments(employeeId, residentId, documentIds);
    }

    @Override
    public void logDocumentView(Long employeeId, Long documentId) {
        auditLoggingService.logDocumentView(employeeId, documentId);
    }

    @Override
    public void logDocumentDownload(Long employeeId, Long documentId) {
        auditLoggingService.logDocumentDownload(employeeId, documentId);
    }

    @Override
    public void logDocumentUpload(Long employeeId, Long documentId) {
        auditLoggingService.logDocumentUpload(employeeId, documentId);
    }

    @Override
    public void logDocumentDelete(Long employeeId, Long documentId) {
        auditLoggingService.logDocumentDelete(employeeId, documentId);
    }

    @Override
    public void logCCDGenerateAndView(Long employeeId, Long residentId) {
        auditLoggingService.logCCDGenerateAndView(employeeId, residentId);
    }

    @Override
    public void logCCDGenerateAndDownload(Long employeeId, Long residentId) {
        auditLoggingService.logCCDGenerateAndDownload(employeeId, residentId);
    }

    @Override
    public void logFacesheetGenerateAndView(Long employeeId, Long residentId) {
        auditLoggingService.logFacesheetGenerateAndView(employeeId, residentId);
    }

    @Override
    public void logFacesheetGenerateAndDownload(Long employeeId, Long residentId) {
        auditLoggingService.logFacesheetGenerateAndDownload(employeeId, residentId);
    }

    @Override
    public void logLogout(Long employeeId) {
        auditLoggingService.logLogout(employeeId);
    }

    @Override
    public void logOperation(AuditLog entry) {
        auditLoggingService.logOperation(entry);
    }
}
