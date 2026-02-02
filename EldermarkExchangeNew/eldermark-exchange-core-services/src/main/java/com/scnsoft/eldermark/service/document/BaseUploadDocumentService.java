package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.entity.document.BaseUploadData;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.service.document.category.DocumentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseUploadDocumentService<T extends BaseUploadData> implements UploadDocumentService<T> {

    @Autowired
    protected DocumentDao documentDao;

    @Autowired
    protected DocumentFileService documentFileService;

    @Autowired
    protected DocumentCategoryService documentCategoryService;

    @Override
    @Transactional
    public Document upload(T data) {
        Document document = new Document();
        document.setAuthorOrganizationAlternativeId(data.getAuthor().getOrganization().getAlternativeId());
        document.setAuthorLegacyId(data.getAuthor().getLegacyId());
        document.setUuid(UUID.randomUUID().toString());

        documentFileService.save(document, data.getInputStream());

        document.setHash(documentFileService.calculateDocumentHash(document));
        document.setCreationTime(Instant.now());
        document.setDocumentTitle(data.getTitle());
        document.setOriginalFileName(data.getOriginalFileName());
        document.setMimeType(data.getMimeType());
        document.setSize((int) documentFileService.calculateDocumentSize(document));
        document.setVisible(true);
        document.setDescription(data.getDescription());
        document.setCategoryChainIds(documentCategoryService.findChainCategoryIdsByOrganizationIdAndIds(data.getOrganization().getId(), data.getCategoryIds()));

        return document;
    }
}
