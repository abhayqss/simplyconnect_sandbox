package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.dao.AuditLoggingDao;
import com.scnsoft.eldermark.entity.AuditLog;
import com.scnsoft.eldermark.entity.AuditLogAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AuditLoggingServiceImpl implements AuditLoggingService {
    @Autowired
    private AuditLoggingDao auditLoggingDao;

    @Override
    public void logLogin(Long employeeId) {
        auditLoggingDao.logOperation(AuditLogAction.LOG_IN, employeeId, null, null);
    }

    @Override
    public void logPatientDiscovery(Long employeeId, List<Long> residentIds) {
        auditLoggingDao.logOperation(AuditLogAction.PATIENT_DISCOVERY, employeeId, residentIds, null);
    }

    @Override
    public void logQueryForDocuments(Long employeeId, Long residentId, List<Long> documentIds) {
        auditLoggingDao.logOperation(AuditLogAction.QUERY_FOR_DOCUMENTS, employeeId, toList(residentId), documentIds);
    }

    @Override
    public void logFilterDocuments(Long employeeId, Long residentId, List<Long> documentIds) {
        auditLoggingDao.logOperation(AuditLogAction.FILTER_DOCUMENTS, employeeId, toList(residentId), documentIds);
    }

    @Override
    public void logDocumentView(Long employeeId, Long documentId) {
        auditLoggingDao.logOperation(AuditLogAction.DOCUMENT_VIEW, employeeId, null, toList(documentId));
    }

    @Override
    public void logDocumentDownload(Long employeeId, Long documentId) {
        auditLoggingDao.logOperation(AuditLogAction.DOCUMENT_DOWNLOAD, employeeId, null, toList(documentId));
    }

    @Override
    public void logDocumentUpload(Long employeeId, Long documentId) {
        auditLoggingDao.logOperation(AuditLogAction.DOCUMENT_UPLOAD, employeeId, null, toList(documentId));
    }

    @Override
    public void logDocumentDelete(Long employeeId, Long documentId) {
        auditLoggingDao.logOperation(AuditLogAction.DOCUMENT_DELETE, employeeId, null, toList(documentId));
    }

    @Override
    public void logCCDGenerateAndView(Long employeeId, Long residentId) {
        auditLoggingDao.logOperation(AuditLogAction.CCD_GENERATE_AND_VIEW, employeeId, toList(residentId), null);
    }

    @Override
    public void logCCDGenerateAndDownload(Long employeeId, Long residentId) {
        auditLoggingDao.logOperation(AuditLogAction.CCD_GENERATE_AND_DOWNLOAD, employeeId, toList(residentId), null);
    }

    @Override
    public void logFacesheetGenerateAndView(Long employeeId, Long residentId) {
        auditLoggingDao.logOperation(AuditLogAction.FACESHEET_GENERATE_AND_VIEW, employeeId, toList(residentId), null);
    }

    @Override
    public void logFacesheetGenerateAndDownload(Long employeeId, Long residentId) {
        auditLoggingDao.logOperation(AuditLogAction.FACESHEET_GENERATE_AND_DOWNLOAD, employeeId, toList(residentId), null);

    }

    @Override
    public void logLogout(Long employeeId) {
        auditLoggingDao.logOperation(AuditLogAction.LOG_OUT, employeeId, null, null);
    }

    @Override
    public void logOperation(AuditLog entry) {
        auditLoggingDao.logOperation(entry);
    }

    public static List<Long> toList(Long element) {
        return (element == null) ? null : Arrays.asList(element);
    }
}
