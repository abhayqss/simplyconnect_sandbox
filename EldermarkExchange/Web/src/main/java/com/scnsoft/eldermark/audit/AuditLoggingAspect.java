package com.scnsoft.eldermark.audit;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.AuditLogAction;
import com.scnsoft.eldermark.facades.AuditLoggingFacade;
import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.shared.ResidentDto;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Order(2)
@Component
public class AuditLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(AuditLoggingAspect.class);

    @Autowired
    private AuditLoggingFacade loggingFacade;

    @Pointcut("execution(* com.scnsoft.eldermark.facades.ResidentFacade.getResidents*(..))")
    public void patientDiscoveryMethod() {}

    @AfterReturning(pointcut = "patientDiscoveryMethod()",
                    returning = "residentList")
    public Object logPatientDiscovery(List<ResidentDto> residentList) throws Throwable {
        Long employeeId = null;
        List<Long> residentIds = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
            residentIds = AuditLogUtils.getResidentIds(residentList);

            loggingFacade.logPatientDiscovery(employeeId, residentIds);  // search params?
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.PATIENT_DISCOVERY, employeeId, residentIds, null), e);
        }

        return residentList;
    }


    @Pointcut("execution(* com.scnsoft.eldermark.facades.DocumentFacade.queryForDocuments*(..))")
    public void queryForDocumentsMethod() {}

    @Pointcut("queryForDocumentsMethod() && args(residentId)")
    public void queryForDocuments1PassArgs(Long residentId) {}

    @AfterReturning(pointcut = "queryForDocuments1PassArgs(residentId)" ,
                    returning = "documentList")
    public Object logQueryForDocuments(Long residentId, List<DocumentDto> documentList) throws Throwable {
        Long employeeId = null;
        List<Long> documentIds = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
            documentIds = AuditLogUtils.getDocumentIds(documentList);

            loggingFacade.logQueryForDocuments(employeeId, residentId, documentIds);
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.QUERY_FOR_DOCUMENTS, employeeId, AuditLogUtils.toList(residentId), documentIds), e);
        }

        return documentList;
    }


    @Pointcut("queryForDocumentsMethod() && args(residentId, filter, ..)")
    public void queryForDocuments2PassArgs(Long residentId, String filter) {}

    @AfterReturning(pointcut = "queryForDocuments2PassArgs(residentId, filter)" ,
            returning = "pageResult")
    public Object logFilterQueryForDocuments(Long residentId, String filter, Page<DocumentDto> pageResult) throws Throwable {
        Long employeeId = null;
        List<Long> documentIds = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
            documentIds = AuditLogUtils.getDocumentIds(pageResult.getContent());

            if (StringUtils.isNotBlank(filter)) {
                loggingFacade.logFilterDocuments(employeeId, residentId, documentIds);
            } else {
                loggingFacade.logQueryForDocuments(employeeId, residentId, documentIds);
            }
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.QUERY_FOR_DOCUMENTS, employeeId, AuditLogUtils.toList(residentId), documentIds), e);
        }

        return pageResult;
    }


    @Pointcut("execution(* com.scnsoft.eldermark.facades.DocumentFacade.saveDocument(..))")
    public void documentUploadMethod() {}

    @AfterReturning(pointcut = "documentUploadMethod()",
            returning = "documentId")
    public Object logDocumentUpload(Long documentId) throws Throwable {
        Long employeeId = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

            loggingFacade.logDocumentUpload(employeeId, documentId);
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.DOCUMENT_UPLOAD, employeeId, null, AuditLogUtils.toList(documentId)), e);
        }

        return documentId;
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facades.DocumentFacade.restoreDocument(..))")
    public void documentRestoreMethod() {}

    @Pointcut("documentRestoreMethod() && args(documentId, ..)")
    public void documentRestoreMethodPassArgs(Long documentId) {}

    @AfterReturning(pointcut = "documentRestoreMethodPassArgs(documentId)")
    public void logDocumentRestore(Long documentId) throws Throwable {
        logDocumentUpload(documentId);
    }


    @Pointcut("execution(* com.scnsoft.eldermark.facades.DocumentFacade.deleteDocument(..))")
    public void deleteDocumentMethod() {}

    @Pointcut("deleteDocumentMethod() && args(documentId, ..)")
    public void deleteDocumentMethodPassArgs(Long documentId) {}

    @AfterReturning(pointcut = "deleteDocumentMethodPassArgs(documentId)")
    public void logDocumentDelete(Long documentId) throws Throwable {
        Long employeeId = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

            loggingFacade.logDocumentDelete(employeeId, documentId);
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.DOCUMENT_DELETE, employeeId, null, AuditLogUtils.toList(documentId)), e);
        }
    }

    @Pointcut("execution(* com.scnsoft.eldermark.web.controller.DocumentController.downloadDocument(..))")
    public void downloadCustomDocumentMethod() {}

    @Pointcut("downloadCustomDocumentMethod() && args(documentId, residentId, ..)")
    public void downloadCustomDocumentMethodPassArgs(Long documentId, Long residentId) {}

    @AfterReturning(pointcut = "downloadCustomDocumentMethodPassArgs(documentId, residentId)")
    public void logCustomDocumentDownload(Long documentId, Long residentId) throws Throwable {
        Long employeeId = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

            loggingFacade.logDocumentDownload(employeeId, documentId);
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.DOCUMENT_DOWNLOAD, employeeId, AuditLogUtils.toList(residentId), AuditLogUtils.toList(documentId)), e);
        }
    }

    @Pointcut("execution(* com.scnsoft.eldermark.web.controller.DocumentController.viewDocument(..))")
    public void viewCustomDocumentMethod() {}

    @Pointcut("viewCustomDocumentMethod() && args(documentId, residentId, ..)")
    public void viewCustomDocumentMethodPassArgs(Long documentId, Long residentId) {}

    @AfterReturning(pointcut = "viewCustomDocumentMethodPassArgs(documentId, residentId)")
    public void logCustomDocumentView(Long documentId, Long residentId) throws Throwable {
        Long employeeId = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

            loggingFacade.logDocumentView(employeeId, documentId);
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.DOCUMENT_VIEW, employeeId, AuditLogUtils.toList(residentId), AuditLogUtils.toList(documentId)), e);
        }
    }

    @Pointcut("execution(* com.scnsoft.eldermark.web.controller.DocumentController.viewReport(..))")
    public void viewReportMethod() {}

    @Pointcut("viewReportMethod() && args(residentId, reportType, ..)")
    public void viewReportMethodPassArgs(Long residentId, String reportType) {}

    @AfterReturning(pointcut = "viewReportMethodPassArgs(residentId, reportType)")
    public void logCcdAndFacesheetView(Long residentId, String reportType) throws Throwable {
        Long employeeId = null;

        AuditLogAction action = "ccd".equals(reportType) ? AuditLogAction.CCD_GENERATE_AND_VIEW :
                AuditLogAction.FACESHEET_GENERATE_AND_VIEW;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

            if (action == AuditLogAction.CCD_GENERATE_AND_VIEW) {
                loggingFacade.logCCDGenerateAndView(employeeId, residentId);
            } else {
                loggingFacade.logFacesheetGenerateAndView(employeeId, residentId);
            }
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(action, employeeId, AuditLogUtils.toList(residentId), null), e);
        }
    }

    @Pointcut("execution(* com.scnsoft.eldermark.web.controller.DocumentController.downloadReport(..))")
    public void downloadReportMethod() {}

    @Pointcut("downloadReportMethod() && args(residentId, reportType, ..)")
    public void downloadReportMethodPassArgs(Long residentId, String reportType) {}

    @AfterReturning(pointcut = "downloadReportMethodPassArgs(residentId, reportType)")
    public void logCcdAndFacesheetDownload(Long residentId, String reportType) throws Throwable {
        Long employeeId = null;

        AuditLogAction action = "ccd".equals(reportType) ? AuditLogAction.CCD_GENERATE_AND_DOWNLOAD :
                                                           AuditLogAction.FACESHEET_GENERATE_AND_DOWNLOAD;
        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

            if (action == AuditLogAction.CCD_GENERATE_AND_DOWNLOAD) {
                loggingFacade.logCCDGenerateAndDownload(employeeId, residentId);
            } else {
                loggingFacade.logFacesheetGenerateAndDownload(employeeId, residentId);
            }
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(action, employeeId, AuditLogUtils.toList(residentId), null), e);
        }
    }

    /* Pointcuts for WS */

    @Pointcut("execution(* com.scnsoft.eldermark.ws.server.DocumentsDownloadEndpointImpl.generateCcd(..))")
    public void generateCcdWS() {}

    @Pointcut("generateCcdWS() && args(residentId)")
    public void generateCcdWSPassarg(Long residentId) {}

    @AfterReturning(pointcut = "generateCcdWSPassarg(residentId)")
    public void logGenerateCcdWS(Long residentId) throws Throwable {
        Long employeeId = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
            loggingFacade.logCCDGenerateAndDownload(employeeId, residentId);
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.CCD_GENERATE_AND_DOWNLOAD, employeeId, AuditLogUtils.toList(residentId), null), e);
        }
    }

    @Pointcut("execution(* com.scnsoft.eldermark.ws.server.DocumentsDownloadEndpointImpl.generateFacesheet(..))")
    public void generateFacesheetWS() {}

    @Pointcut("generateFacesheetWS() && args(residentId)")
    public void generateFacesheetWSPassarg(Long residentId) {}

    @AfterReturning(pointcut = "generateFacesheetWSPassarg(residentId)")
    public void logGenerateFacesheetWS(Long residentId) throws Throwable {
        Long employeeId = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
            loggingFacade.logFacesheetGenerateAndDownload(employeeId, residentId);
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.FACESHEET_GENERATE_AND_DOWNLOAD, employeeId, AuditLogUtils.toList(residentId), null), e);
        }
    }

    @Pointcut("execution(* com.scnsoft.eldermark.ws.server.DocumentsDownloadEndpointImpl.downloadDocument(..))")
    public void downloadDocumentWS() {}

    @Pointcut("downloadDocumentWS() && args(documentId)")
    public void downloadDocumentWSPassArgs(Long documentId) {}

    @AfterReturning(pointcut = "downloadDocumentWSPassArgs(documentId)")
    public void logDownloadDocumentWS(Long documentId) throws Throwable {
        Long employeeId = null;

        try {
            employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
            loggingFacade.logDocumentDownload(employeeId, documentId);
        } catch (Exception e) {
            logger.error(AuditLogUtils.errorToString(AuditLogAction.CCD_GENERATE_AND_DOWNLOAD, employeeId, null, AuditLogUtils.toList(documentId)), e);
        }
    }
}
