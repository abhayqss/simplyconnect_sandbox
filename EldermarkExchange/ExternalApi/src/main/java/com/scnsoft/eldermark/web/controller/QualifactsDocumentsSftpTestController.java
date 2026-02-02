package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.services.inbound.document.qualifacts.QualifactsDocumentAssignmentListenerRunCondition;
import com.scnsoft.eldermark.services.integration.qualifacts.QualifactsDocumentsGateway;
import com.scnsoft.eldermark.services.DocumentService;
import com.scnsoft.eldermark.services.SaveDocumentCallbackImpl;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.eldermark.shared.web.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController()
@Profile("!prod")
@RequestMapping("test/")
@Conditional(QualifactsDocumentAssignmentListenerRunCondition.class)
public class QualifactsDocumentsSftpTestController {

    private final DocumentFacade documentFacade;
    private final DocumentService documentService;
    private final QualifactsDocumentsGateway qualifactsDocumentsGateway;

    @Autowired
    public QualifactsDocumentsSftpTestController(DocumentFacade documentFacade, DocumentService documentService, QualifactsDocumentsGateway qualifactsDocumentsGateway) {
        this.documentFacade = documentFacade;
        this.documentService = documentService;
        this.qualifactsDocumentsGateway = qualifactsDocumentsGateway;
    }


    @PostMapping(value = "/qsi/sftp/{residentId}")
    public Response<Void> testSftpUpload(
            @PathVariable(value = "residentId") Long residentId,
            @RequestPart("document") final MultipartFile document
    ) {
        Long documentId = documentFacade.saveDocument(new DocumentMetadata.Builder()
                        .setDocumentTitle(document.getOriginalFilename())
                        .setFileName(document.getOriginalFilename())
                        .setMimeType(document.getContentType()).build(),
                residentId, 1L, true, (List<Long>) Collections.EMPTY_LIST, new SaveDocumentCallbackImpl() {
            @Override
            public void saveToFile(File file) {
                try {
                    FileCopyUtils.copy(document.getInputStream(), new FileOutputStream(file));
                } catch (IOException e) {
                    throw new FileIOException("Failed to save file " + document.getOriginalFilename(), e);
                }
            }
        }).getId();
        qualifactsDocumentsGateway.sendDocumentToQualifacts(documentService.findDocument(documentId));
        return Response.successResponse();
    }


}

