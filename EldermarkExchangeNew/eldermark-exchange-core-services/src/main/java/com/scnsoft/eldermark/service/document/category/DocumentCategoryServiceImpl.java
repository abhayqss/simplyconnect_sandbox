package com.scnsoft.eldermark.service.document.category;

import com.scnsoft.eldermark.dao.document.category.DocumentCategoryDao;
import com.scnsoft.eldermark.dao.specification.DocumentCategorySpecificationGenerator;
import com.scnsoft.eldermark.entity.basic.HistoryIdsAware;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.basic.BaseAuditableService;
import com.scnsoft.eldermark.service.document.DocumentService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentCategoryServiceImpl extends BaseAuditableService<DocumentCategory> implements DocumentCategoryService {

    @Autowired
    private DocumentCategoryDao documentCategoryDao;

    @Autowired
    private DocumentCategorySpecificationGenerator specificationGenerator;

    @Autowired
    @Qualifier("documentService")
    private DocumentService documentService;

    @Override
    @Transactional(readOnly = true)
    public DocumentCategory findById(Long id) {
        return documentCategoryDao.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentCategory> findAllByOrganizationIdAndIds(Long organizationId, List<Long> ids) {
        return documentCategoryDao.findAll(
                notArchivedByOrganizationId(organizationId)
                        .and(specificationGenerator.byIdIn(ids))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentCategory> findAllByOrganizationId(Long organizationId, Pageable pageable) {
        return documentCategoryDao.findAll(notArchivedByOrganizationId(organizationId), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentCategory> findAllByOrganizationId(Long organizationId) {
        return documentCategoryDao.findAll(notArchivedByOrganizationId(organizationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentCategory> findAllByCategoryChainIds(List<Long> categoryChainIds) {
        var byChainId = specificationGenerator.byChainIdIn(categoryChainIds);
        var byId = specificationGenerator.byIdIn(categoryChainIds);
        var notArchived = specificationGenerator.notArchived();

        return documentCategoryDao.findAll(notArchived.and(byChainId.or(byId)));
    }

    @Override
    public DocumentCategory save(DocumentCategory entity) {
        return documentCategoryDao.save(entity);
    }

    @Override
    public Long saveOrUpdate(DocumentCategory documentCategory) {
        if (documentCategory.getId() == null) {
            validateDocumentNameUniqueness(documentCategory);
            return createAuditableEntity(documentCategory);
        } else {
            validateDocumentCategoryExistence(documentCategory);
            validateDocumentNameUniqueness(documentCategory);
            return updateAuditableEntity(documentCategory);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNameUniqueInOrganization(@Nullable Long categoryId, Long organizationId, String name) {
        if (categoryId == null) {
            return !documentCategoryDao.existsByOrganizationIdAndNameAndArchived(organizationId, name, false);
        } else {
            return !documentCategoryDao.existsByIdNotAndOrganizationIdAndNameAndArchived(categoryId, organizationId, name, false);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsed(Long id) {
        return documentCategoryDao.findById(id, HistoryIdsAware.class)
                .map(documentService::existsByCategory)
                .orElse(false);
    }

    @Override
    public DocumentCategory createTransientClone(DocumentCategory entity) {
        var clone = new DocumentCategory();
        clone.setName(entity.getName());
        clone.setColor(entity.getColor());
        clone.setOrganizationId(entity.getOrganizationId());
        clone.setUpdatedByEmployeeId(entity.getUpdatedByEmployeeId());
        return clone;
    }

    private Specification<DocumentCategory> notArchivedByOrganizationId(Long organizationId) {
        return specificationGenerator.byOrganizationId(organizationId)
                .and(specificationGenerator.notArchived());
    }

    private void validateDocumentNameUniqueness(DocumentCategory category) {
        if (!isNameUniqueInOrganization(category.getId(), category.getOrganizationId(), category.getName())) {
            throw new ValidationException("Category '" + category.getName() + "' already exists");
        }
    }

    private void validateDocumentCategoryExistence(DocumentCategory documentCategory) {
        if (!documentCategoryDao.existsByIdAndOrganizationIdAndArchived(
                documentCategory.getId(),
                documentCategory.getOrganizationId(),
                false
        )) {
            throw new BusinessException(BusinessExceptionType.NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> findChainCategoryIdsByOrganizationIdAndIds(Long organizationId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }
        var notArchivedByOrganizationId = notArchivedByOrganizationId(organizationId);
        var byIdIn = specificationGenerator.byIdIn(ids);

        var historyAwares = documentCategoryDao.findAll(notArchivedByOrganizationId.and(byIdIn), HistoryIdsAware.class);
        if (historyAwares.size() != ids.size()) {
            throw new ValidationException("categoryIds are invalid");
        }

        var categoryChainIds = historyAwares
                .stream()
                .map(HistoryIdsAware::resolveHistoryId)
                .collect(Collectors.toSet());

        return categoryChainIds;
    }

    @Override
    public Set<Long> findChainCategoryIdsByIds(List<Long> ids) {
        return documentCategoryDao.findAll(specificationGenerator.byIdIn(ids).and(specificationGenerator.notArchived()), HistoryIdsAware.class).stream()
                .map(HistoryIdsAware::resolveHistoryId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return documentCategoryDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return documentCategoryDao.findByIdIn(ids, projection);
    }
}
