package com.scnsoft.eldermark.ws.server;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.services.Report;
import com.scnsoft.eldermark.services.ReportGenerator;
import com.scnsoft.eldermark.services.ReportGeneratorFactory;
import com.scnsoft.eldermark.shared.DocumentRetrieveDto;
import com.scnsoft.eldermark.shared.DocumentType;
import com.scnsoft.eldermark.ws.server.exceptions.*;
import org.apache.cxf.attachment.ByteDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;
import java.io.FileNotFoundException;
import java.io.IOException;

//@WebService(
//        endpointInterface = "com.scnsoft.eldermark.ws.server.DocumentsDownloadEndpoint",
//        targetNamespace = Constants.WEB_SERVICES_NAMESPACE_DOCUMENTS_DOWNLOAD,
//        wsdlLocation = "wsdl/documentsDownload.wsdl")
//@PreAuthorize(
//        SecurityExpressions.IS_ELDERMARK_USER)
public class DocumentsDownloadEndpointImpl extends SpringBeanAutowiringSupport implements DocumentsDownloadEndpoint {
    private final static Logger logger = LoggerFactory.getLogger(DocumentsDownloadEndpointImpl.class);

    @Autowired
    private DocumentFacade documentFacade;

    @Autowired
    private ReportGeneratorFactory generatorFactory;

    @Override
    public DocumentRetrieveDto downloadDocument(Long documentId) throws DocumentNotFoundException, ResidentOptedOutException {
        if (documentId == null) {
            throw new ContractViolationException("documentId is required");
        }

        try {
            DocumentBean document = documentFacade.findDocument(documentId);
            return mapToDto(document);
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentOptedOutException e) {
            logger.error("Resident was opted out, doc #" + documentId, e);
            throw new ResidentOptedOutException();
        } catch (com.scnsoft.eldermark.shared.exceptions.DocumentNotFoundException e) {
            logger.error("Document not found, doc #" + documentId, e);
            throw new DocumentNotFoundException(documentId);
        } catch (Exception e) {
            logger.error("Document download failed, doc #" + documentId, e);
            throw new InternalServerException();
        }
    }

    @Override
    public DocumentRetrieveDto generateCcd(Long residentId) throws ResidentNotFoundException, ResidentOptedOutException {
        if (residentId == null) {
            throw new ContractViolationException("residentId is required");
        }

        try {
            ReportGenerator generator = generatorFactory.getGenerator("ccd");
            Report report = generator.generate(residentId);
            return mapToDto(report);
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentNotFoundException e) {
            String message = "Resident #" + residentId + " not found";
            logger.error(message, e);
            throw new ResidentNotFoundException(residentId);
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentOptedOutException e) {
            logger.error("Resident #" + residentId + " has been excluded from HIE: CCD isn't available anymore.", e);
            throw new ResidentOptedOutException();
        } catch (Exception e) {
            logger.error("Internal error while generating CCD for resident #" + residentId, e);
            throw new InternalServerException();
        }
    }

    @Override
    public DocumentRetrieveDto generateFacesheet(Long residentId) throws ResidentNotFoundException, ResidentOptedOutException {
        if (residentId == null) {
            throw new ContractViolationException("residentId is required");
        }

        try {
            ReportGenerator generator = generatorFactory.getGenerator("facesheet");
            Report report = generator.generate(residentId);
            return mapToDto(report);
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentNotFoundException e) {
            String message = "Resident #" + residentId + " not found";
            logger.error(message, e);
            throw new ResidentNotFoundException(residentId);
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentOptedOutException e) {
            logger.error("Resident #" + residentId + " has been excluded from HIE: CCD isn't available anymore.", e);
            throw new ResidentOptedOutException();
        } catch (Exception e) {
            logger.error("Internal error while generating CCD for resident #" + residentId, e);
            throw new InternalServerException();
        }
    }

    private DocumentRetrieveDto mapToDto(Report r) throws IOException {
        DocumentRetrieveDto t = new DocumentRetrieveDto();
        t.setDocumentTitle(r.getDocumentTitle());
        t.setDocumentType(r.getDocumentType());
        t.setData(new DataHandler(new ByteDataSource(StreamUtils.copyToByteArray(r.getInputStream()))));
        t.setMimeType(r.getMimeType());
        return t;
    }

    private DocumentRetrieveDto mapToDto(DocumentBean document) throws FileNotFoundException {
        DocumentRetrieveDto t = new DocumentRetrieveDto();
        t.setDocumentTitle(document.getDocumentTitle());
        t.setDocumentType(DocumentType.CUSTOM);
        t.setData(new DataHandler(new FileDataSource(document.getFile())));
        t.setMimeType(document.getMimeType());
        return t;
    }
}
