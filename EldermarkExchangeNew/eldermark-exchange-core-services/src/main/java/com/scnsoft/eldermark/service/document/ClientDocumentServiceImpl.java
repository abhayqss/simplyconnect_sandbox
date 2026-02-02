package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.beans.DocumentCount;
import com.scnsoft.eldermark.beans.InternalClientDocumentFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientDocumentSecurityAwareEntity;
import com.scnsoft.eldermark.dao.ClientDocumentDao;
import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.dao.specification.ClientDocumentSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.DocumentEditableData;
import com.scnsoft.eldermark.entity.document.DocumentFieldsAware;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.XdsCommunicationException;
import com.scnsoft.eldermark.service.xds.XdsRegistryConnectorService;
import com.scnsoft.eldermark.utils.CustomSortUtils;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientDocumentServiceImpl extends BaseDocumentService implements ClientDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(ClientDocumentServiceImpl.class);

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private ClientDocumentSpecificationGenerator clientDocumentSpecificationGenerator;

    @Autowired
    private ClientDocumentDao clientDocumentDao;

    @Autowired
    private XdsRegistryConnectorService xdsRegistryConnectorService;

    @Autowired
    private DocumentFileService documentFileService;

    @Override
    @Transactional
    public ClientDocument findById(long id) {
        var document = clientDocumentDao.findById(id).orElseThrow();
        return checkDocument(document, false);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return clientDocumentDao.findById(id, projection).orElseThrow();
    }

    @Override
    public List<ClientDocument> findAllByIds(Collection<Long> ids) {
        return clientDocumentDao.findAllById(ids).stream()
                .map(document -> checkDocument(document, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<ClientDocument> find(InternalClientDocumentFilter documentFilter, PermissionFilter permissionFilter, Pageable pageRequest) {
        var byFilter = clientDocumentSpecificationGenerator.byFilterAndMerged(documentFilter);
        var hasAccess = clientDocumentSpecificationGenerator.hasAccess(permissionFilter);
        var withExpressionSort = CustomSortUtils.<ClientDocument>withExpressionSort(pageRequest.getSort());

        var documents = clientDocumentDao.findAll(
                byFilter.and(hasAccess.and(withExpressionSort)),
                CustomSortUtils.unsortedPage(pageRequest)
        );
        return new PageImpl<>(updateIfCda(documents.getContent()), documents.getPageable(), documents.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(InternalClientDocumentFilter documentFilter, PermissionFilter permissionFilter) {
        var byFilter = clientDocumentSpecificationGenerator.byFilterAndMerged(documentFilter);
        var hasAccess = clientDocumentSpecificationGenerator.hasAccess(permissionFilter);
        return clientDocumentDao.count(byFilter.and(hasAccess));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentCount> countGroupedBySignatureStatus(
            InternalClientDocumentFilter documentFilter,
            PermissionFilter permissionFilter
    ) {
        var byFilter = clientDocumentSpecificationGenerator.byFilterAndMerged(documentFilter);
        var hasAccess = clientDocumentSpecificationGenerator.hasAccess(permissionFilter);
        return clientDocumentDao.countGroupedBySignatureStatus(byFilter.and(hasAccess));
    }

    @Override
    public List<ClientDocument> updateIfCda(List<ClientDocument> documents) {
        if (documents == null) {
            return null;
        }
        return documents.stream().map(this::updateIfCda).collect(Collectors.toList());
    }

    @Override
    public ClientDocument updateIfCda(ClientDocument document) {
        if (document.getIsCDA() == null) {
            var isCda = defineIsCdaDocument(document);
            documentDao.setIsCda(document.getId(), isCda);
            document = clientDocumentDao.findById(document.getId()).orElseThrow();
        }
        return document;
    }

    @Override
    public <T extends DocumentFieldsAware> boolean defineIsCdaDocument(T document) {
        try (var inputStream = documentFileService.loadDocument(document)) {
            //try to parse as CDA document
            CDAUtil.load(inputStream);
            logger.info("Document with id=[{}] parsed as CDA", document.getId());
            return true;
        } catch (Exception ex) {
            logger.info("Document with id=[{}] not parsed as CDA, reason is: ", document.getId(), ex);
            return false;
        }
    }

    private ClientDocument checkDocument(ClientDocument document, boolean visibleOnly) {
        if (visibleOnly && !document.getVisible()) {
            throw new BusinessException(BusinessExceptionType.DOCUMENT_NOT_VISIBLE);
        }

        Client client = document.getClient();
        if (client == null || isInvisible(client)) {
            throw new BusinessException(BusinessExceptionType.DOCUMENT_NOT_VISIBLE);
        }

        return updateIfCda(document);
    }

    private boolean isInvisible(Client client) {
        return !communityService.isEligibleForDiscovery(client.getCommunity());
    }

    @Override
    public ClientDocumentSecurityAwareEntity findSecurityAwareEntity(Long aLong) {
        return clientDocumentDao.findById(aLong, ClientDocumentSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    public List<ClientDocumentSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> longs) {
        return clientDocumentDao.findByIdIn(longs, ClientDocumentSecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instant> findOldestDate(InternalClientDocumentFilter documentFilter, PermissionFilter permissionFilter) {
        var byFilter = clientDocumentSpecificationGenerator.byFilterAndMerged(documentFilter);
        var hasAccess = clientDocumentSpecificationGenerator.hasAccess(permissionFilter);
        return clientDocumentDao.findMinDate(byFilter.and(hasAccess));
    }

    @Override
    @Transactional
    public void markInvisible(long id, Employee curEmployee) {
        super.markInvisible(id, curEmployee);
        var doc = documentDao.findById(id).orElseThrow();
        xdsRegistryConnectorService.deprecateDocumentInRepository(doc.getUuid());
    }

    @Override
    @Transactional
    public Long edit(DocumentEditableData documentEditableData) {
        var id = super.edit(documentEditableData);
        var doc = documentDao.findById(id).orElseThrow();
        try {
            xdsRegistryConnectorService.updateDocumentTitleInRepository(doc);
        } catch (XdsCommunicationException e) {
            logger.warn("Failed to update document in XDS registry", e);
        }
        return id;
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findDocumentShouldBeSignedByEmployeeId(Long employeeId, Collection<Long> associatedClientIds, PermissionFilter permissionFilter, Class<P> projectionClass, Sort sort, int limit) {
        var hasAccess = clientDocumentSpecificationGenerator.hasAccess(permissionFilter);
        var shouldBeSignedByEmployeeId = clientDocumentSpecificationGenerator.shouldBeSignedByEmployeeId(
                employeeId, associatedClientIds);
        return clientDocumentDao.findAll(hasAccess.and(shouldBeSignedByEmployeeId), projectionClass, sort, limit);
    }
}
