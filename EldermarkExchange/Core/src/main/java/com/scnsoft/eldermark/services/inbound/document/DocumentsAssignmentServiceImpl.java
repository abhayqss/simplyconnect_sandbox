package com.scnsoft.eldermark.services.inbound.document;

import com.scnsoft.eldermark.dao.DatabaseJpaDao;
import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationResidentJpaDao;
import com.scnsoft.eldermark.dao.inbound.document.DocumentAssignmentLogDao;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentInboundFile;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentLog;
import com.scnsoft.eldermark.exception.integration.inbound.document.DocumentAssignmentErrorType;
import com.scnsoft.eldermark.exception.integration.inbound.document.DocumentAssignmentInboundException;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.services.inbound.InboundFilesServiceRunCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Conditional(DocumentAssignmentRunCondition.class)
public class DocumentsAssignmentServiceImpl implements DocumentsAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentsAssignmentServiceImpl.class);

    private final CareCoordinationResidentJpaDao careCoordinationResidentJpaDao;
    private final DocumentFacade documentFacade;
    private final EmployeeService employeeService;
    private final DocumentAssignmentLogDao documentAssignmentLogDao;
    private final DatabaseJpaDao databaseJpaDao;

    //todo use Marco Channel or another?
    private static final String DOCUMENT_ASSIGNMENT_EMPLOYEE_LOGIN = "marcochannel@eldermark.com";
    private static final String DOCUMENT_ASSIGNMENT_EMPLOYEE_ORGANIZATION = "RBA";

    @Autowired
    public DocumentsAssignmentServiceImpl(CareCoordinationResidentJpaDao careCoordinationResidentJpaDao,
                                          DocumentFacade documentFacade,
                                          EmployeeService employeeService,
                                          DocumentAssignmentLogDao documentAssignmentLogDao, DatabaseJpaDao databaseJpaDao) {
        this.careCoordinationResidentJpaDao = careCoordinationResidentJpaDao;
        this.documentFacade = documentFacade;
        this.employeeService = employeeService;
        this.documentAssignmentLogDao = documentAssignmentLogDao;
        this.databaseJpaDao = databaseJpaDao;
    }


    @Override
    public Document uploadDocument(DocumentAssignmentInboundFile inboundFile) {
        validate(inboundFile);

        CareCoordinationResident resident = findResident(inboundFile);

        return saveDocument(resident, inboundFile.getFileTitle(), inboundFile.getFile());
    }

    private void validate(DocumentAssignmentInboundFile remoteFile) {
        if (StringUtils.isAnyEmpty(remoteFile.getOrganizationName(), remoteFile.getMpiPatientId())) {
            throw new DocumentAssignmentInboundException(DocumentAssignmentErrorType.REQUIRED_PARAM_MISSING);
        }
    }

    private CareCoordinationResident findResident(DocumentAssignmentInboundFile inboundFile) {
        Database database = databaseJpaDao.findByName(inboundFile.getOrganizationName());
        List<CareCoordinationResident> residents = careCoordinationResidentJpaDao.findAllActiveByMpiPatientIdAndAssigningAuthorityUniversal(
                inboundFile.getMpiPatientId(), database.getOid()
        );

        if (CollectionUtils.isNotEmpty(residents)) {
            return residents.get(0);
        }

        throw new DocumentAssignmentInboundException(DocumentAssignmentErrorType.REQUIRED_PARAM_MISSING);
    }

    private Document saveDocument(CareCoordinationResident resident, String fileTitle, final File document) {
        final DocumentMetadata documentMetadata = new DocumentMetadata.Builder()
                .setDocumentTitle(fileTitle)
                .setFileName(document.getName())
                .setMimeType(URLConnection.getFileNameMap().getContentTypeFor(document.getName()))
                .build();

        final Employee author = employeeService.getActiveEmployee(DOCUMENT_ASSIGNMENT_EMPLOYEE_LOGIN, DOCUMENT_ASSIGNMENT_EMPLOYEE_ORGANIZATION);

        return documentFacade.saveDocument(documentMetadata, resident.getId(), author.getId(), true,
                Collections.<Long>emptyList(), document);
    }

    @Override
    public DocumentAssignmentLog createDocumentAssignmentLog(DocumentAssignmentInboundFile inboundFile, Document document) {
        return createDocumentAssignmentLog(inboundFile, document, null);
    }

    @Override
    public DocumentAssignmentLog createDocumentAssignmentLog(DocumentAssignmentInboundFile inboundFile, DocumentAssignmentErrorType unassignedReason) {
        return createDocumentAssignmentLog(inboundFile, null, unassignedReason);
    }

    private DocumentAssignmentLog createDocumentAssignmentLog(DocumentAssignmentInboundFile inboundFile, Document document, DocumentAssignmentErrorType unassignedReason) {
        DocumentAssignmentLog assignmentLog = new DocumentAssignmentLog();

        assignmentLog.setReceivedTime(new Date());
        assignmentLog.setDocumentName(inboundFile.getFile().getName());
        assignmentLog.setInputPath(inboundFile.getInputPath());
        assignmentLog.setOrganizationName(inboundFile.getOrganizationName());
        assignmentLog.setDocument(document);
        assignmentLog.setUnassignedReason(unassignedReason);

        return documentAssignmentLogDao.save(assignmentLog);
    }

}
