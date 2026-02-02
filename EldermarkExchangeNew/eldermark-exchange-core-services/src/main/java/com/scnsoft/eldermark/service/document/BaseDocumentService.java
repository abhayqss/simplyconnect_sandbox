package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.basic.HistoryIdsAware;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.DocumentEditableData;
import com.scnsoft.eldermark.entity.document.DocumentFileFieldsAware;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.document.category.DocumentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Service("documentService")
public class BaseDocumentService implements DocumentService {

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private DocumentFileService documentFileService;

    @Autowired
    private DocumentCategoryService documentCategoryService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    protected CommunityService communityService;

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return documentDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return documentDao.findByIdIn(ids, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCategory(HistoryIdsAware category) {
        var categoryChainId = category.resolveHistoryId();
        return documentDao.existsByCategoryChainIdsContains(categoryChainId);
    }

    @Override
    public Document findDocumentById(Long id) {
        return documentDao.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public void temporaryDelete(long id, Employee curEmployee) {
        var document = findDocumentById(id);
        temporaryDelete(document, curEmployee);
    }

    @Override
    @Transactional
    public void temporaryDelete(Document document, Employee curEmployee) {
        if (document.getTemporaryDeletionTime() != null) {
            throw new BusinessException("The document has been already temporarily deleted");
        }
        document.setTemporaryDeletedBy(curEmployee);
        document.setTemporaryDeletionTime(Instant.now());
        document.setRestoredBy(null);
        document.setRestorationTime(null);
        documentDao.save(document);
    }

    @Override
    @Transactional
    public void restore(long id, Employee curEmployee) {
        var document = findDocumentById(id);
        if (document.getTemporaryDeletionTime() == null) {
            throw new BusinessException("Can not restore without temporarily deletion");
        }
        document.setTemporaryDeletedBy(null);
        document.setTemporaryDeletionTime(null);
        document.setRestoredBy(curEmployee);
        document.setRestorationTime(Instant.now());
        documentDao.save(document);
    }

    @Override
    @Transactional
    public void markInvisible(long id, Employee curEmployee) {
        Document document = documentDao.findById(id).orElseThrow();
        document.setDeletedBy(curEmployee);
        document.setDeletionTime(Instant.now());
        document.setVisible(false);
        documentDao.save(document);
    }

    @Override
    public InputStream readDocument(DocumentFileFieldsAware document) {
        return documentFileService.loadDocument(document);
    }

    @Override
    public byte[] readDocumentAsBytes(DocumentFileFieldsAware document) {
        return documentFileService.loadDocumentAsBytes(document);
    }

    @Override
    public void deleteDocumentFile(DocumentFileFieldsAware document) {
        documentFileService.delete(document);
    }

    @Override
    public String calculateDocumentHash(DocumentFileFieldsAware document) {
        return documentFileService.calculateDocumentHash(document);
    }

    @Override
    @Transactional
    public Long edit(DocumentEditableData documentEditableData) {
        var document = documentDao.findById(documentEditableData.getId()).orElseThrow();
        var organization = getOrganizationId(document);
        document.setDocumentTitle(documentEditableData.getTitle());
        document.setDescription(documentEditableData.getDescription());
        document.setUpdateTime(Instant.now());
        var categoryChainIds = documentCategoryService.findChainCategoryIdsByOrganizationIdAndIds(organization.getId(), documentEditableData.getCategoryIds());
        if (document.getCategoryChainIds() == null) {
            document.setCategoryChainIds(categoryChainIds);
        } else {
            document.getCategoryChainIds().clear();
            document.getCategoryChainIds().addAll(categoryChainIds);
        }
        documentDao.save(document);

        return document.getId();
    }

    @Override
    @Transactional
    public void temporaryDeleteDocumentsInFolderIfNotAlreadyTemporaryOrPermanentlyDeleted(Long folderId, Employee curEmployee, Instant temporaryDeletionTime) {
        var documents = documentDao.findAllByFolder_Id(folderId);
        documents.forEach(document -> {
            if (document.getTemporaryDeletionTime() == null && document.getDeletionTime() == null) {
                document.setTemporaryDeletedBy(curEmployee);
                document.setTemporaryDeletionTime(temporaryDeletionTime);
                document.setRestoredBy(null);
                document.setRestorationTime(null);
            }
        });
        documentDao.saveAll(documents);
    }

    @Override
    @Transactional
    public void permanentlyDeleteDocumentsInFolderIfNotAlreadyPermanentlyDeleted(Long folderId, Employee curEmployee, Instant deletionTime) {
        var documents = documentDao.findAllByFolder_Id(folderId);
        documents.forEach(document -> {
            if (document.getDeletionTime() == null) {
                if (document.getTemporaryDeletionTime() == null) {
                    throw new BusinessException("Trying to permanently remove document that is  not temporary deleted. ID: " + document.getId());
                }
                document.setDeletedBy(curEmployee);
                document.setDeletionTime(deletionTime);
                document.setVisible(false);
            }
        });
        documentDao.saveAll(documents);
    }

    @Override
    @Transactional
    public void restoreDocumentsInFolderIfTemporaryDeletedAtTime(
            Long folderId,
            Employee restoredByEmployee,
            Instant restorationTime,
            Instant initialFolderTemporaryDeletedTime) {
        var documents = documentDao.findAllByFolder_Id(folderId);
        documents.forEach(document -> {
            if (initialFolderTemporaryDeletedTime.equals(document.getTemporaryDeletionTime()) &&
                    document.getDeletionTime() == null) {
                document.setRestoredBy(restoredByEmployee);
                document.setRestorationTime(restorationTime);
                document.setTemporaryDeletionTime(null);
                document.setTemporaryDeletedBy(null);
            }
        });
        documentDao.saveAll(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findDocumentById(Long id, Class<P> projection) {
        return documentDao.findById(id, projection).orElseThrow();
    }

    private Document findDocumentById(long id) {
        var document = documentDao.findById(id).orElseThrow();
        if (!document.getVisible()) {
            throw new BusinessException(BusinessExceptionType.DOCUMENT_NOT_VISIBLE);
        }
        return document;
    }

    private Organization getOrganizationId(Document doc) {
        if (doc.getClientOrganizationAlternativeId() != null) {
            return organizationService.findByAlternativeId(doc.getClientOrganizationAlternativeId());
        } else if (doc.getCommunity() != null) {
            return doc.getCommunity().getOrganization();
        } else {
            throw new IllegalStateException("Unexpected document type");
        }
    }
}
