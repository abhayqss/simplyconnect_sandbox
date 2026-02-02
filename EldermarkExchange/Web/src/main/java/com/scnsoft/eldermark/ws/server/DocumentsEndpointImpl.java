package com.scnsoft.eldermark.ws.server;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.ws.server.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.jws.WebService;
import java.util.List;

//@WebService(
//        endpointInterface = "com.scnsoft.eldermark.ws.server.DocumentsEndpoint",
//        targetNamespace = Constants.WEB_SERVICES_NAMESPACE_DOCUMENTS,
//        wsdlLocation = "wsdl/documents.wsdl")
//@PreAuthorize(
//        SecurityExpressions.IS_ELDERMARK_USER)
public class DocumentsEndpointImpl extends SpringBeanAutowiringSupport implements DocumentsEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DocumentsEndpointImpl.class);

    @Autowired
    private DocumentFacade documentFacade;

    @Override
    public List<DocumentDto> queryForDocuments(Long residentId) throws ResidentNotFoundException, ResidentOptedOutException {
        if (residentId == null) {
            throw new ContractViolationException("residentId is required");
        }

        try {
            return documentFacade.queryForDocuments(residentId);
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentNotFoundException e) {
            logger.error("Resident #" + residentId + " not found", e);
            throw new ResidentNotFoundException(residentId);
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentOptedOutException e) {
            logger.error("Resident #" + residentId + " has been excluded from HIE, documents are unavailable", e);
            throw new ResidentOptedOutException();
        } catch (Exception e) {
            logger.error("Failed to query documents for resident #" + residentId, e);
            throw new InternalServerException();
        }
    }

    @Override
    public String deleteDocument(Long documentId) throws DocumentNotFoundException, ResidentOptedOutException {
        if (documentId == null) {
            throw new ContractViolationException("documentId is required");
        }

        try {
            documentFacade.deleteDocument(documentId);
            return "Document has been successfully deleted.";
        } catch (com.scnsoft.eldermark.shared.exceptions.DocumentNotFoundException e) {
            logger.error("Document #" + documentId + " not found", e);
            throw new DocumentNotFoundException(documentId);
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentOptedOutException e) {
            logger.error("Cannot delete document #" + documentId + ": resident has been excluded from HIE", e);
            throw new ResidentOptedOutException();
        } catch (Exception e) {
            logger.error("Failed to delete document #" + documentId, e);
            throw new InternalServerException();
        }
    }

    public void setDocumentFacade(DocumentFacade documentFacade) {
        this.documentFacade = documentFacade;
    }
}
