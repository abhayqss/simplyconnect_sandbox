package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentCategorySpecificationGenerator {

    public Specification<DocumentCategory> notArchived() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isFalse(root.get(DocumentCategory_.archived));
    }

    public Specification<DocumentCategory> byOrganizationId(Long organizationId) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(DocumentCategory_.organizationId), organizationId);
    }

    public Specification<DocumentCategory> byIdIn(List<Long> ids) {
        return (root, query, criteriaBuilder) ->
            root.get(DocumentCategory_.id).in(ids);
    }

    public Specification<DocumentCategory> byChainIdIn(List<Long> chainIds) {
        return (root, query, criteriaBuilder) ->
            root.get(DocumentCategory_.chainId).in(chainIds);
    }
}
