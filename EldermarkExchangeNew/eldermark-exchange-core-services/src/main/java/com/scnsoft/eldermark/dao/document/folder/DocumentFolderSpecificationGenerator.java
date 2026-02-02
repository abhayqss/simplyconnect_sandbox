package com.scnsoft.eldermark.dao.document.folder;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class DocumentFolderSpecificationGenerator {

    public Specification<DocumentFolder> byCommunityId(Long communityId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(DocumentFolder_.communityId), communityId);
    }

    public Specification<DocumentFolder> byCommunityIdIn(Collection<Long> communityIds) {
        return (root, query, criteriaBuilder) ->
                root.get(DocumentFolder_.communityId).in(communityIds);
    }

    public Specification<DocumentFolder> byTypes(List<DocumentFolderType> types) {
        return (root, query, criteriaBuilder) -> root.get(DocumentFolder_.type).in(types);
    }

    public Specification<DocumentFolder> byParentFolderId(Long parentId) {
        return (root, query, criteriaBuilder) -> {
            var parentIdPath = root.get(DocumentFolder_.parentId);
            return parentId == null
                    ? criteriaBuilder.isNull(parentIdPath)
                    : criteriaBuilder.equal(parentIdPath, parentId);
        };
    }

    public Specification<DocumentFolder> byDocumentSignatureTemplateId(Long documentSignatureTemplateId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            var documentSignatureTemplatesJoin = JpaUtils.getOrCreateSetJoin(root, DocumentFolder_.documentSignatureTemplates);
            return criteriaBuilder.equal(documentSignatureTemplatesJoin.get(DocumentSignatureTemplate_.id), documentSignatureTemplateId);
        };
    }
}
