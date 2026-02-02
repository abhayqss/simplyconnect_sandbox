package com.scnsoft.eldermark.openxds.api.facade;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.ClientDocumentDao;
import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.ClientDocumentUploadData;
import com.scnsoft.eldermark.entity.document.Document_;
import com.scnsoft.eldermark.entity.document.SharingOption;
import com.scnsoft.eldermark.openxds.api.dto.XdsDocumentDto;
import com.scnsoft.eldermark.openxds.api.dto.XdsDocumentRegistrySyncItem;
import com.scnsoft.eldermark.openxds.api.dto.XdsDocumentRegistrySyncResult;
import com.scnsoft.eldermark.openxds.api.exceptions.OpenXdsApiException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.document.ClientDocumentService;
import com.scnsoft.eldermark.service.document.UploadClientDocumentService;
import com.scnsoft.eldermark.service.document.cda.CdaImportService;
import com.scnsoft.eldermark.service.document.cda.generator.CcdGeneratorService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.xds.XdsRegistryConnectorService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DocumentUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class XdsDocumentFacadeImpl implements XdsDocumentFacade {

    private static final Logger logger = LoggerFactory.getLogger(XdsDocumentFacadeImpl.class);

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private ClientDocumentService documentService;

    @Autowired
    private UploadClientDocumentService uploadDocumentService;

    @Autowired
    private ClientDocumentDao clientDocumentDao;

    @Autowired
    private XdsRegistryConnectorService xdsRegistryConnectorService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CdaImportService cdaImportService;

    @Autowired
    private CcdGeneratorService ccdGeneratorService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Value("${xds.document.author.company}")
    private String documentAuthorCompany;

    @Value("${xds.document.author.login}")
    private String documentAuthorLogin;

    @Value("^${home.community.id}\\.3\\.\\d+_CCD$")
    private String generatedCcdPattern;

    @Value("${xds.registry.enabled}")
    private Boolean xdsRegistryEnabled;

    @PostConstruct
    public void init() {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            throw new IllegalStateException("Xds registry should be disabled in openxds-api module " +
                    "to prevent calling registry backs");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public XdsDocumentRegistrySyncResult synchronizeAllDocumentsWithXdsRegistry(Instant from) {
        logger.info("Called synchronizeAllDocumentsWithXdsRegistry");
        Objects.requireNonNull(from);

        var documentIds = CareCoordinationUtils.toIdsSet(documentDao.findAll(createdOrDeletedNotEarlier(from), IdAware.class));
        var syncResult = new XdsDocumentRegistrySyncResult();
        logger.info("Found {} documents to be synced", documentIds.size());
        syncResult.setTotalDocumentsFound(documentIds.size());
        syncResult.setDetails(new ArrayList<>(documentIds.size()));

        for (Long id : documentIds) {
            var item = new XdsDocumentRegistrySyncItem();
            item.setDocumentId(id);
            syncResult.getDetails().add(item);

            try {
                var doc = documentDao.findById(id, SyncDocumentProjection.class).orElseThrow();
                var existsInExchange = doc.getVisible();
                if (existsInExchange && StringUtils.isEmpty(doc.getHash())) {
                    var newHash = documentService.calculateDocumentHash(doc);
                    doc = doc.withHash(newHash);
                }

                var clientId = getDocumentClientId(id);
                var docResponse = xdsRegistryConnectorService.synchronizeDocWithRepository(doc, clientId);
                item.setRegistryResponse(docResponse);
            } catch (Exception e) {
                logger.info("Document [{}] sync failed: " + e.getMessage());
                item.setError(true);
                item.setErrorMessage(e.getMessage());
            }
        }

        return syncResult;
    }

    private Specification<Document> createdOrDeletedNotEarlier(Instant when) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.greaterThanOrEqualTo(root.get(Document_.creationTime), when),
                        criteriaBuilder.greaterThanOrEqualTo(root.get(Document_.deletionTime), when)
                );
    }

    @Override
    @Transactional
    public Long uploadDocument(XdsDocumentDto uploadDocument) {
        Objects.requireNonNull(uploadDocument, "Method parameters cannot be null");
        Objects.requireNonNull(uploadDocument.getUuid(), "Uuid should not be null");
        Objects.requireNonNull(uploadDocument.getUniqueId(), "Unique id should not be null");
        Objects.requireNonNull(uploadDocument.getResidentId(), "Resident id should not be null");
        Objects.requireNonNull(uploadDocument.getContent(), "Document content should not be null");

        Assert.isTrue(!DocumentUtils.hash(uploadDocument.getContent()).equals(uploadDocument.getHash()), "Provided hash doesn't math file content");

        var client = Optional.ofNullable(clientService.findById(uploadDocument.getResidentId()))
                .orElseThrow(() -> new OpenXdsApiException("Client [" + uploadDocument.getResidentId() + "] not found"));

        var document = saveDocument(uploadDocument, client);

        logger.info("Parsing CCD; residentId = [{}] ; document title = [{}] ; document id = [{}]",
                uploadDocument.getResidentId(),
                uploadDocument.getTitle(),
                document.getId());

        final ByteArrayInputStream cdaInputStream = new ByteArrayInputStream(uploadDocument.getContent());

        try {
            cdaImportService.importXml(cdaInputStream, client, client.getCommunity(), CdaImportService.ImportMode.APPEND, document);
        } catch (Exception e) {
            logger.warn("CDA parsing failed; Saving only raw file without parsing; residentId = [{}] ; document title = [{}] ; document id = [{}]",
                    uploadDocument.getResidentId(),
                    uploadDocument.getTitle(),
                    document.getId(),
                    e);
        }
        return document.getId();
    }

    private Document saveDocument(XdsDocumentDto uploadDocument, Client client) {
        Document documentOld = findDocumentByUniqueIdAndVisible(uploadDocument);
        if (documentOld != null) {
            if (documentOld.getHash().equals(uploadDocument.getHash())) {
                //The same document is saved previously. Allow it to proceed.
                return documentOld;
            }
            logger.warn("document with unique id [{}] already exists", uploadDocument.getUniqueId());
            throw new OpenXdsApiException("document with unique id [" + uploadDocument.getUniqueId() + "] already exists in repository");
        }

        Employee author = employeeService.getActiveOrInactiveEmployee(documentAuthorLogin, documentAuthorCompany);
        if (author == null) {
            throw new OpenXdsApiException("Document author not found");
        }

        var documentUploadData = new ClientDocumentUploadData(uploadDocument.getTitle(), uploadDocument.getTitle(),
                uploadDocument.getMimeType(), new ByteArrayInputStream(uploadDocument.getContent()), client, author,
                SharingOption.ALL)
                .withUniqueId(uploadDocument.getUniqueId());

        return uploadDocumentService.upload(documentUploadData);
    }

    private Document findDocumentByUniqueIdAndVisible(XdsDocumentDto uploadDocument) {
        var pair = findDocumentByUniqueIdAndVisible(uploadDocument.getUniqueId());

        if (pair == null) {
            return null;
        }

        var document = pair.getFirst();
        var documentClient = pair.getSecond();
        if (!documentClient.getId().equals(uploadDocument.getResidentId())) {
            throw new OpenXdsApiException("Document [" + document.getId() +
                    "] with the same unique id [" + uploadDocument.getUniqueId() +
                    "] already exists for another visible client [" + documentClient.getId() + "]");
        }

        return document;
    }

    private boolean isCommunityInvisible(Community community) {
        return Boolean.TRUE.equals(community.getInactive()) || Boolean.TRUE.equals(community.getTestingTraining());
    }

    private Long getDocumentClientId(Long documentId) {
        return clientDocumentDao.findById(documentId, ClientIdAware.class).orElseThrow().getClientId();
    }

    @Override
    @Transactional(readOnly = true)
    public XdsDocumentDto getDocument(String documentUniqueId) {
        XdsDocumentDto dest = new XdsDocumentDto();

        Long residentId;
        byte[] content;
        if (documentUniqueId.matches(generatedCcdPattern)) {
            String[] ar = documentUniqueId.split("\\.");
            residentId = Long.parseLong(ar[ar.length - 2]);

            try {
                var report = ccdGeneratorService.generate(residentId, true);
                content = IOUtils.toByteArray(report.getInputStream());
                String hash = DocumentUtils.hash(content);

                dest.setHash(hash);
                dest.setMimeType("text/xml");
                dest.setTitle("CCD.xml");

            } catch (IOException e) {
                throw new OpenXdsApiException("Exception during CCD generation document generation", e);
            }


        } else {
            var document = findDocumentByUniqueIdAndVisibleOrThrow(documentUniqueId);
            try(var docInputStream = documentService.readDocument(document)) {
                content = IOUtils.toByteArray(docInputStream);

                residentId = getDocumentClientId(document.getId());

                dest.setUuid(document.getUuid());
                dest.setHash(document.getHash());
                dest.setMimeType(document.getMimeType());
                dest.setTitle(document.getDocumentTitle());

            } catch (IOException e) {
                throw new OpenXdsApiException("Failed to get file " + document.getOriginalFileName(), e);
            }
        }

        dest.setUniqueId(documentUniqueId);
        dest.setContent(content);
        dest.setResidentId(residentId);
        return dest;
    }

    @Override
    @Transactional
    public void deleteDocument(String documentUniqueId) {
        try {
            var curEmployee = loggedUserService.getCurrentEmployee();
            var doc = findDocumentByUniqueIdAndVisibleOrThrow(documentUniqueId);
            documentService.deleteDocumentFile(doc);
            documentService.markInvisible(doc.getId(), curEmployee);
        } catch (Exception e) {
            throw new OpenXdsApiException("Failed to delete file with unique id" + documentUniqueId, e);
        }
    }

    private Document findDocumentByUniqueIdAndVisibleOrThrow(String uniqueId) {
        var pair = findDocumentByUniqueIdAndVisible(uniqueId);

        if (pair == null) {
            throw new OpenXdsApiException("Document with uniqueId = [" + uniqueId + "] is not found or not visible");
        }

        return pair.getFirst();
    }

    private Pair<Document, Client> findDocumentByUniqueIdAndVisible(String uniqueId) {
        Document document = documentDao.findFirstByUniqueId(uniqueId);
        if (document == null || !document.getVisible()) {
            return null;
        }

        Long documentClientId = getDocumentClientId(document.getId());
        if (documentClientId == null) {
            return null;
        }

        var documentClient = clientService.getById(documentClientId);
        if (BooleanUtils.isNotFalse(documentClient.getOptOut()) || isCommunityInvisible(documentClient.getCommunity())) {
            return null;
        }

        return new Pair<>(document, documentClient);
    }
}
