package com.scnsoft.eldermark.dao.document.folder;

import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermission;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermission_;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentFolderPermissionSpecificationGenerator {

    public Specification<DocumentFolderPermission> byFolderIdIn(List<Long> folderIds) {
        return (root, query, criteriaBuilder) ->
            CollectionUtils.isNotEmpty(folderIds)
                ? root.get(DocumentFolderPermission_.folderId).in(folderIds)
                : criteriaBuilder.or();
    }

    public Specification<DocumentFolderPermission> byEmployeeIdIn(List<Long> employeeIds) {
        return (root, query, criteriaBuilder) ->
            CollectionUtils.isNotEmpty(employeeIds)
                ? root.get(DocumentFolderPermission_.employeeId).in(employeeIds)
                : criteriaBuilder.or();
    }

    public Specification<DocumentFolderPermission> unique() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.and();
        };
    }
}
