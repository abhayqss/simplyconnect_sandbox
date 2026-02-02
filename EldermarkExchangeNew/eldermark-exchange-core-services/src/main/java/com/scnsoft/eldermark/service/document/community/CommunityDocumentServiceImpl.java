package com.scnsoft.eldermark.service.document.community;

import com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.dao.CommunityDocumentDao;
import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.entity.document.CommunityDocumentEditableData;
import com.scnsoft.eldermark.entity.document.community.CommunityDocument;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.document.BaseDocumentService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class CommunityDocumentServiceImpl extends BaseDocumentService implements CommunityDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(CommunityDocumentServiceImpl.class);

    @Autowired
    private CommunityDocumentDao communityDocumentDao;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private DocumentFolderService folderService;

    @Override
    @Transactional
    public CommunityDocument findById(long id) {
        var document = communityDocumentDao.findById(id).orElseThrow();
        return checkDocument(document, false);
    }

    private CommunityDocument checkDocument(CommunityDocument document, boolean visibleOnly) {
        if (visibleOnly && !document.getVisible()) {
            throw new BusinessException(BusinessExceptionType.DOCUMENT_NOT_VISIBLE);
        }
        Long communityId = document.getCommunityId();
        if (communityId == null || isInvisible(communityId)) {
            throw new BusinessException(BusinessExceptionType.DOCUMENT_NOT_VISIBLE);
        }

        return document;
    }

    private boolean isInvisible(Long communityId) {
        return !communityService.isEligibleForDiscovery(communityId);
    }

    @Override
    public CommunityDocumentSecurityFieldsAware findSecurityAwareEntity(Long aLong) {
        return communityDocumentDao.findById(aLong, CommunityDocumentSecurityFieldsAware.class).orElseThrow();
    }

    @Override
    public List<CommunityDocumentSecurityFieldsAware> findSecurityAwareEntities(Collection<Long> longs) {
        return communityDocumentDao.findByIdIn(longs, CommunityDocumentSecurityFieldsAware.class);
    }

    @Override
    @Transactional
    public Long edit(CommunityDocumentEditableData documentEditableData) {
        var id = super.edit(documentEditableData);
        var doc = documentDao.findById(id).orElseThrow();
        if (documentEditableData.getParentId() != null) {
            doc.setFolder(folderService.findById(documentEditableData.getParentId()));
        } else {
            doc.setFolder(null);
        }
        if (documentEditableData.getCommunityId() != null) {
            doc.setCommunity(communityService.findById(documentEditableData.getCommunityId()));
        }
        documentDao.save(doc);
        return doc.getId();
    }
}
