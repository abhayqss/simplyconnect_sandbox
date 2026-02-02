package com.scnsoft.eldermark.services.inbound.marco;

import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.dao.externalapi.MarcoDocumentsDao;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoIntegrationDocument;
import com.scnsoft.eldermark.exception.integration.marco.MarcoInboundException;
import com.scnsoft.eldermark.exception.integration.marco.MarcoUnassignedReason;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.SaveDocumentCallbackImpl;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.FluentIterable;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Conditional(MarcoInboundFilesServiceRunCondition.class)
public class MarcoDocumentsServiceImpl implements MarcoDocumentsService {

    private static final Logger logger = LoggerFactory.getLogger(MarcoDocumentsServiceImpl.class);

    private static final String MARCO_EMPLOYEE_LOGIN = "marcochannel@eldermark.com";
    private static final String MARCO_EMPLOYEE_ORGANIZATION = "RBA";

    private final MarcoCareCoordinationResidentService marcoResidentService;
    private final MarcoDocumentsDao marcoDocumentsDao;
    private final DocumentFacade documentFacade;
    private final EmployeeService employeeService;
    private final DocumentDao documentDao;

    @Autowired
    public MarcoDocumentsServiceImpl(MarcoCareCoordinationResidentService residentsService, MarcoDocumentsDao marcoDocumentsDao,
                                     DocumentFacade documentFacade, EmployeeService employeeService, DocumentDao documentDao) {
        this.marcoResidentService = residentsService;
        this.marcoDocumentsDao = marcoDocumentsDao;
        this.documentFacade = documentFacade;
        this.employeeService = employeeService;
        this.documentDao = documentDao;
    }

    @Override
    @Transactional
    public Document uploadDocument(MarcoDocumentMetadata metadata, File document) {
        try {
            if (!isDocumentValid(document) || !isMetadataValid(metadata)) {
                throw new MarcoInboundException(MarcoUnassignedReason.REQUIRED_PARAM_MISSING);
            }

            final Date dateOfBirth = parseDate(metadata.getDateOfBirthStr());
            metadata.setDateOfBirth(dateOfBirth);

            final CareCoordinationResident resident = resolveResident(metadata);
            final String fileTitle = generateFileTitle(metadata, document);
            final Document assignedDocument = saveMarcoDocument(resident, fileTitle, document);
            return assignedDocument;
        } catch (MarcoInboundException me) {
            throw me;
        } catch (Exception e) {
            throw new MarcoInboundException(e);
        }
    }

    private String generateFileTitle(MarcoDocumentMetadata metadata, File document) {
        String fileTitle = metadata.getFileTitle();
        if (StringUtils.isEmpty(fileTitle)) {
            return document.getName();
        }
        if (fileTitle.contains(".")) { // has extension
            return fileTitle;
        }
        final int extensionStart = document.getName().lastIndexOf('.');
        if (extensionStart == -1) {
            return fileTitle;
        }
        return fileTitle + document.getName().substring(extensionStart);
    }

    private boolean isDocumentValid(File document) {
        return document != null && document.exists();
    }

    private boolean isMetadataValid(MarcoDocumentMetadata metadata) {
        return StringUtils.isNoneEmpty(
                metadata.getOrganizationName(),
                metadata.getDateOfBirthStr()
        ) && (StringUtils.isNoneEmpty(metadata.getFirstName(),
                metadata.getLastName()) || StringUtils.isNotEmpty(metadata.getFullName()));
    }

    private Date parseDate(String dateOfBirthStr) throws ParseException {
        return DateUtils.parseDate(dateOfBirthStr.trim(), "MM/dd/yyyy", "yyyy-MM-dd");
    }

    private CareCoordinationResident resolveResident(MarcoDocumentMetadata metadata) {
        final List<CareCoordinationResident> matchingResidents = marcoResidentService.getPatientDetailsByIdentityFields(metadata);
        if (CollectionUtils.isEmpty(matchingResidents)) {
            throw new MarcoInboundException(MarcoUnassignedReason.RESIDENT_NOT_FOUND);
        }


        final CareCoordinationResident residentWithMpi = findFirstResidentWithMpi(matchingResidents);
        if (residentWithMpi != null) {
            return residentWithMpi;
        }

        return matchingResidents.get(0);
    }

    private CareCoordinationResident findFirstResidentWithMpi(List<CareCoordinationResident> matchingResidents) {
        for (CareCoordinationResident resident : matchingResidents) {
            if (CollectionUtils.isNotEmpty(resident.getMpi()) && hasValidMpi(resident.getMpi())) {
                return resident;
            }
        }
        return null;
    }

    private boolean hasValidMpi(Set<MPI> mpi) {
        return FluentIterable.of(mpi).anyMatch(new Predicate<MPI>() {
            @Override
            public boolean evaluate(MPI mpi) {
                return mpi != null && StringUtils.isNotEmpty(mpi.getPatientId());
            }
        });
    }

    private Document saveMarcoDocument(CareCoordinationResident resident, String fileTitle, final File document) {
        final DocumentMetadata documentMetadata = new DocumentMetadata.Builder()
                .setDocumentTitle(fileTitle)
                .setFileName(document.getName())
                .setMimeType(URLConnection.getFileNameMap().getContentTypeFor(document.getName()))
                .build();

        final Employee author = employeeService.getActiveEmployee(MARCO_EMPLOYEE_LOGIN, MARCO_EMPLOYEE_ORGANIZATION);

        return documentFacade.saveDocument(documentMetadata, resident.getId(), author.getId(), true,
                Collections.<Long>emptyList(), new SaveDocumentCallbackImpl() {
                    @Override
                    public void saveToFile(File file) {
                        try {
                            FileCopyUtils.copy(new FileInputStream(document), new FileOutputStream(file));
                        } catch (IOException e) {
                            logger.error("Failed to save file, due to - {}", e.getMessage());
                            throw new FileIOException("Failed to save file " + document.getName(), e);
                        }
                    }
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MarcoIntegrationDocument createNewMarcoIntegrationDocumentLog(MarcoDocumentMetadata metadata, Document document) {
        MarcoIntegrationDocument marcoIntegrationDocument = createNewMarcoIntegrationDocumentLog(metadata, document, null);
        document.setMarcoIntegrationDocument(marcoIntegrationDocument);
        documentDao.updateDocument(document);
        return marcoIntegrationDocument;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MarcoIntegrationDocument createNewMarcoIntegrationDocumentLog(MarcoDocumentMetadata metadata, MarcoUnassignedReason unassignedReason) {
        return createNewMarcoIntegrationDocumentLog(metadata, null, unassignedReason);
    }

    private MarcoIntegrationDocument createNewMarcoIntegrationDocumentLog(MarcoDocumentMetadata metadata, Document document, MarcoUnassignedReason unassignedReason) {
        MarcoIntegrationDocument marcoIntegrationDocument = new MarcoIntegrationDocument();
        marcoIntegrationDocument.setOrganizationName(metadata.getOrganizationName());
        marcoIntegrationDocument.setReceivedTime(new Date());
        marcoIntegrationDocument.setFirstName(metadata.getFirstName());
        marcoIntegrationDocument.setLastName(metadata.getLastName());
        marcoIntegrationDocument.setFullName(metadata.getFullName());
        marcoIntegrationDocument.setDateOfBirthStr(metadata.getDateOfBirthStr());
        marcoIntegrationDocument.setSsn(metadata.getSsn());
        marcoIntegrationDocument.setFileTitle(metadata.getFileTitle());
        marcoIntegrationDocument.setAuthor(metadata.getAuthor());
        marcoIntegrationDocument.setDocumentOriginalName(metadata.getDocumentOriginalName());
        marcoIntegrationDocument.setDocument(document);
        marcoIntegrationDocument.setUnassignedReason(unassignedReason);
        return save(marcoIntegrationDocument);
    }

    private MarcoIntegrationDocument save(MarcoIntegrationDocument documentLog) {
        return marcoDocumentsDao.save(documentLog);
    }
}
