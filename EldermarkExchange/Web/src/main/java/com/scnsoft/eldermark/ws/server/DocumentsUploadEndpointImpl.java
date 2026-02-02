package com.scnsoft.eldermark.ws.server;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.services.SaveDocumentCallbackImpl;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.eldermark.ws.server.dto.DocumentShareOptionsWsDto;
import com.scnsoft.eldermark.ws.server.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.activation.DataHandler;
import javax.jws.WebService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

//@WebService(
//        endpointInterface = "com.scnsoft.eldermark.ws.server.DocumentsUploadEndpoint",
//        targetNamespace = Constants.WEB_SERVICES_NAMESPACE_DOCUMENTS_UPLOAD,
//        wsdlLocation = "wsdl/documentsUpload.wsdl")
//@PreAuthorize(
//        SecurityExpressions.IS_ELDERMARK_USER)
public class DocumentsUploadEndpointImpl extends SpringBeanAutowiringSupport implements DocumentsUploadEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DocumentsUploadEndpointImpl.class);

    @Autowired
    private DocumentFacade documentFacade;

    @Override
    public Long uploadDocument(Long residentId, final String fileName, String mimeType,
                                 final DataHandler data, DocumentShareOptionsWsDto shareOptions)
            throws ResidentNotFoundException, ResidentOptedOutException, OrganizationNotFoundException,
            DocumentSharePolicyViolation {

        if (residentId == null) {
            throw new ContractViolationException("residentId is required");
        }

        if (fileName == null) {
            throw new ContractViolationException("fileName is required");
        }

        if (mimeType == null) {
            throw new ContractViolationException("mimeType is required");
        }

        if (data == null) {
            throw new ContractViolationException("data is required");
        }

        if (shareOptions == null ||
            shareOptions.getSharedWithAll() == null && shareOptions.getIdsOfOrganizationsToShareWith() == null) {
            throw new ContractViolationException("at least one of the shareOptions is required");
        }

        long authorId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

        try {
            DocumentMetadata metadata = new DocumentMetadata.Builder()
                    .setDocumentTitle(fileName)
                    .setFileName(fileName)
                    .setMimeType(mimeType)
                    .build();

            boolean isSharedWithAll = shareOptions.getSharedWithAll();

            List<Long> idsOfOrganizationsToShareWith = shareOptions.getIdsOfOrganizationsToShareWith();
            if (idsOfOrganizationsToShareWith == null) {
                idsOfOrganizationsToShareWith = Collections.emptyList();
            }

            Document document = documentFacade.saveDocument(metadata, residentId, authorId, isSharedWithAll,
                    idsOfOrganizationsToShareWith, new SaveDocumentCallbackImpl() {
                @Override
                public void saveToFile(File file) {
                    try {
                        FileCopyUtils.copy(data.getInputStream(), new FileOutputStream(file));
                    } catch (IOException e) {
                        throw new FileIOException("Failed to save file " + fileName, e);
                    }
                }
            });
            return document.getId();
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentNotFoundException e) {
            logger.error("Resident #" + residentId + " not found", e);
            throw new ResidentNotFoundException(residentId);
        } catch (com.scnsoft.eldermark.shared.exceptions.ResidentOptedOutException e) {
            logger.error("Resident #" + residentId + " has been excluded from HIE: document upload isn't available anymore", e);
            throw new ResidentOptedOutException();
        } catch (com.scnsoft.eldermark.facades.exceptions.DatabaseNotFoundException e) {
            logger.error("Organization #" + e.getDatabaseId() + " not found", e);
            throw new OrganizationNotFoundException(e.getDatabaseId());
        } catch (Exception e) {
            logger.error("Failed to upload document for resident #" + residentId, e);
            throw new InternalServerException();
        }
    }
}
