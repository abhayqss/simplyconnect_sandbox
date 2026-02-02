package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.CommunityDocumentFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.document.*;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CommunityDocumentAndFolderSpecificationGenerator {

    public Specification<CommunityDocumentAndFolder> byFilter(CommunityDocumentFilter filter) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            predicates.add(criteriaBuilder.equal(root.get(CommunityDocumentAndFolder_.communityId), filter.getCommunityId()));

            predicates.add(
                    filter.getFolderId() != null
                            ? criteriaBuilder.equal(root.get(CommunityDocumentAndFolder_.folderId), filter.getFolderId())
                            : criteriaBuilder.isNull(root.get(CommunityDocumentAndFolder_.folderId))
            );

            predicates.add(
                    Specification.not(byType(DocumentAndFolderType.TEMPLATE))
                            .or(byAccessibleTemplateStatuses(filter.getPermissionFilter()))
                            .toPredicate(root, query, criteriaBuilder)
            );

            if (StringUtils.isNotBlank(filter.getTitle())) {
                predicates.add(criteriaBuilder.like(
                        root.get(CommunityDocumentAndFolder_.title),
                        SpecificationUtils.wrapWithWildcards(filter.getTitle())
                ));
            }

            if (StringUtils.isNotBlank(filter.getDescription())) {
                predicates.add(criteriaBuilder.like(
                        root.get(CommunityDocumentAndFolder_.description),
                        SpecificationUtils.wrapWithWildcards(filter.getDescription())
                ));
            }

            if (filter.getFromDate() != null) {
                predicates.add(
                        criteriaBuilder.or(
                                // lastModifiedTime is null only for templates and template folder
                                root.get(CommunityDocumentAndFolder_.lastModifiedTime).isNull(),
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.get(CommunityDocumentAndFolder_.lastModifiedTime),
                                        filter.getFromDate()
                                )
                        )
                );
            }

            if (filter.getToDate() != null) {
                predicates.add(
                        criteriaBuilder.or(
                                // lastModifiedTime is null only for templates and template folder
                                root.get(CommunityDocumentAndFolder_.lastModifiedTime).isNull(),
                                criteriaBuilder.lessThanOrEqualTo(
                                        root.get(CommunityDocumentAndFolder_.lastModifiedTime),
                                        filter.getToDate()
                                )
                        )
                );
            }

            if (!filter.getIncludeDeleted()) {
                predicates.add(criteriaBuilder.isNull(root.get(CommunityDocumentAndFolder_.temporaryDeletionTime)));
            }

            if (CollectionUtils.isNotEmpty(filter.getCategoryChainIds()) || filter.getIncludeNotCategorized()) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.and(
                                        criteriaBuilder.equal(root.get(CommunityDocumentAndFolder_.type), DocumentAndFolderType.CUSTOM),
                                        root.get(CommunityDocumentAndFolder_.id).in(getDocumentIdsByCategories(filter, query, criteriaBuilder))
                                ),
                                criteriaBuilder.and(
                                        criteriaBuilder.equal(root.get(CommunityDocumentAndFolder_.type), DocumentAndFolderType.FOLDER),
                                        root.get(CommunityDocumentAndFolder_.id).in(getDocumentFoldersIdsByCategories(filter, query, criteriaBuilder))
                                ),
                                !filter.getIncludeNotCategorized()
                                        ? criteriaBuilder.or()
                                        : root.get(CommunityDocumentAndFolder_.type).in(
                                        Arrays.stream(DocumentAndFolderType.values())
                                                .filter(it -> it != DocumentAndFolderType.CUSTOM && it != DocumentAndFolderType.FOLDER)
                                                .collect(Collectors.toList())
                                )
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Subquery<String> getDocumentFoldersIdsByCategories(CommunityDocumentFilter filter, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var subQuery = query.subquery(String.class);
        var subRoot = subQuery.from(DocumentFolder.class);
        subQuery.select(criteriaBuilder.concat("f", subRoot.get(DocumentFolder_.id).as(String.class)));

        var joinCategories = subRoot.join(DocumentFolder_.categoryChainIds, JoinType.LEFT);

        var byCategories = CollectionUtils.isNotEmpty(filter.getCategoryChainIds())
                ? joinCategories.in(filter.getCategoryChainIds())
                : criteriaBuilder.disjunction();

        var notCategorized = filter.getIncludeNotCategorized()
                ? joinCategories.isNull()
                : criteriaBuilder.disjunction();

        subQuery.where(criteriaBuilder.or(byCategories, notCategorized));
        return subQuery;
    }

    private Subquery<String> getDocumentIdsByCategories(CommunityDocumentFilter filter, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var subQuery = query.subquery(String.class);
        var subRoot = subQuery.from(Document.class);
        subQuery.select(subRoot.get(Document_.id).as(String.class));

        var joinCategories = subRoot.join(Document_.categoryChainIds, JoinType.LEFT);

        var byCategories = CollectionUtils.isNotEmpty(filter.getCategoryChainIds())
                ? joinCategories.in(filter.getCategoryChainIds())
                : criteriaBuilder.disjunction();

        var notCategorized = filter.getIncludeNotCategorized()
                ? joinCategories.isNull()
                : criteriaBuilder.disjunction();

        subQuery.where(criteriaBuilder.or(byCategories, notCategorized));
        return subQuery;
    }

    public Specification<CommunityDocumentAndFolder> byFolderId(Long folderId) {
        return (root, query, criteriaBuilder) -> {
            var folderIdPath = root.get(CommunityDocumentAndFolder_.folderId);
            if (folderId == null) {
                return criteriaBuilder.isNull(folderIdPath);
            } else {
                return criteriaBuilder.equal(folderIdPath, folderId);
            }
        };
    }

    public Specification<CommunityDocumentAndFolder> byFolderIds(List<Long> folderIds) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isNotEmpty(folderIds)) {
                var folderIdPath = root.get(CommunityDocumentAndFolder_.folderId);
                if (folderIds.contains(null)) {
                    var ids = folderIds.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    return criteriaBuilder.or(
                            criteriaBuilder.isNull(folderIdPath),
                            folderIdPath.in(ids)
                    );
                } else {
                    return folderIdPath.in(folderIds);
                }
            } else {
                return criteriaBuilder.or();
            }
        };
    }

    public Specification<CommunityDocumentAndFolder> byCommunityId(Long communityId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CommunityDocumentAndFolder_.communityId), communityId);
    }

    public Specification<CommunityDocumentAndFolder> byType(DocumentAndFolderType type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CommunityDocumentAndFolder_.type), type);
    }

    public Specification<CommunityDocumentAndFolder> byTypeIn(Collection<DocumentAndFolderType> types) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get(CommunityDocumentAndFolder_.TYPE)).value(types);
    }

    public Specification<CommunityDocumentAndFolder> excludeTemporarilyDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get(CommunityDocumentAndFolder_.temporaryDeletionTime));
    }

    public Specification<CommunityDocumentAndFolder> excludePermanentlyDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get(CommunityDocumentAndFolder_.deletionTime));
    }

    public Specification<CommunityDocumentAndFolder> byAccessibleTemplateStatuses(PermissionFilter permissionFilter) {

        return (root, query, criteriaBuilder) -> {

            var templateStatus = root.get(CommunityDocumentAndFolder_.templateStatus);

            if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
                return templateStatus.in(List.of(DocumentSignatureTemplateStatus.COMPLETED, DocumentSignatureTemplateStatus.DRAFT));
            }

            if (permissionFilter.hasPermission(Permission.DOCUMENT_TEMPLATE_MODIFY_IF_ASSOCIATED_ORGANIZATION)) {

                var employeeOrganizationIds = permissionFilter.getEmployees(Permission.DOCUMENT_TEMPLATE_MODIFY_IF_ASSOCIATED_ORGANIZATION).stream()
                        .map(BasicEntity::getOrganizationId)
                        .collect(Collectors.toSet());

                var documentOrganizationId = JpaUtils.getOrCreateJoin(root, CommunityDocumentAndFolder_.community).get(Community_.organizationId);

                return criteriaBuilder.or(
                        criteriaBuilder.equal(templateStatus, DocumentSignatureTemplateStatus.COMPLETED),
                        criteriaBuilder.and(
                                criteriaBuilder.equal(templateStatus, DocumentSignatureTemplateStatus.DRAFT),
                                documentOrganizationId.in(employeeOrganizationIds)
                        )
                );
            }

            return criteriaBuilder.equal(templateStatus, DocumentSignatureTemplateStatus.COMPLETED);
        };
    }
}
