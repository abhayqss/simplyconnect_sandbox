package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.beans.CommunityDocumentFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.document.CommunityDocumentAndFolderDao;
import com.scnsoft.eldermark.dao.specification.CommunityDocumentAndFolderSpecificationGenerator;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.document.CommunityDocumentAndFolder;
import com.scnsoft.eldermark.entity.document.DocumentAndFolderType;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermission;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevel;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateService;
import com.scnsoft.eldermark.util.document.DocumentAndFolderUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CommunityDocumentAndFolderServiceImpl implements CommunityDocumentAndFolderService {

    @Autowired
    private CommunityDocumentAndFolderDao dao;

    @Autowired
    private CommunityDocumentAndFolderSpecificationGenerator specificationGenerator;

    @Autowired
    private DocumentFolderService folderService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentSignatureTemplateService signatureTemplateService;

    private final Map<DocumentAndFolderType, Function<CommunityDocumentAndFolder, InputStream>> documentReaderMap =
            Map.of(
                    DocumentAndFolderType.CUSTOM, doc -> documentService.readDocument(doc),
                    DocumentAndFolderType.TEMPLATE, doc -> new ByteArrayInputStream(
                            signatureTemplateService.getTemplatePdf(
                                    new DocumentSignatureTemplateContext(doc.getTemplate(), doc.getCommunity())
                            )
                    )
            );

    @Override
    public Page<CommunityDocumentAndFolder> find(CommunityDocumentFilter documentFilter, Pageable pageable) {
        return dao.findAll(
                specificationGenerator.byFilter(documentFilter)
                        .and(specificationGenerator.excludePermanentlyDeleted()),
                pageable
        );
    }

    @Override
    public long count(CommunityDocumentFilter documentFilter) {
        return dao.count(
                specificationGenerator.byFilter(documentFilter)
                        .and(specificationGenerator.excludePermanentlyDeleted())
        );
    }

    @Override
    public <T> List<T> findByIds(List<String> ids, Class<T> projectionType) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        var items = dao.findByIdIn(ids, projectionType);
        if (CollectionUtils.isEmpty(items) || ids.size() != items.size()) {
            throw new ValidationException("Invalid document and folder ids");
        }
        return items;
    }

    @Override
    public long countByParentId(Long parentFolderId) {
        return dao.count(specificationGenerator.byFolderId(parentFolderId));
    }

    @Override
    public DocumentTreeItem<CommunityDocumentAndFolder> getDocumentTree(Long communityId, Long rootFolderId, PermissionFilter permissionFilter) {
        return getDocumentTree(communityId, rootFolderId, permissionFilter, null);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentTreeItem<CommunityDocumentAndFolder> getDocumentTree(
            Long communityId,
            Long rootFolderId,
            PermissionFilter permissionFilter,
            List<String> filterIds
    ) {
        Predicate<DocumentFolder> folderFilter = folder -> {

            if (folder.getTemporaryDeletionTime() != null && folder.getDeletionTime() != null) {
                return false;
            }

            if (folder.getIsSecurityEnabled()) {
                var employeeIds = permissionFilter.getAllEmployeeIds();
                return folder.getPermissions()
                        .stream()
                        .filter(it -> employeeIds.contains(it.getEmployeeId()))
                        .map(DocumentFolderPermission::getPermissionLevel)
                        .map(DocumentFolderPermissionLevel::getCode)
                        .anyMatch(it -> it.isWiderOrEqualTo(DocumentFolderPermissionLevelCode.VIEWER));
            } else {
                return true;
            }
        };

        var folderIdTree = folderService.getFolderIdTree(communityId, rootFolderId, folderFilter);
        var folderIds = folderIdTree.valuesList();

        var folders = dao.findAllById(
                folderIds.stream()
                        .filter(Objects::nonNull)
                        .map(DocumentAndFolderUtils::toFolderId)
                        .collect(Collectors.toSet())
        );
        var documents = findDocumentsByCommunityIdAndFolderIds(communityId, folderIds, permissionFilter);
        var documentsAndFolders = Stream.concat(folders.stream(), documents.stream())
                .collect(Collectors.toList());

        var childrenMap = buildChildrenMap(documentsAndFolders);

        var rootFolder = findFolderInListById(folders, rootFolderId);

        if (filterIds != null) {
            var treeItems = childrenMap.get(rootFolderId).stream()
                    .filter(it -> filterIds.contains(it.getId()))
                    .flatMap(it -> Stream.ofNullable(buildDocumentAndFolderTree(childrenMap, it)))
                    .collect(Collectors.toList());

            return DocumentTreeItem.folder(rootFolder, treeItems);
        } else {
            return buildDocumentAndFolderTree(childrenMap, rootFolder);
        }
    }

    private Map<Long, List<CommunityDocumentAndFolder>> buildChildrenMap(List<CommunityDocumentAndFolder> documentsAndFolders) {
        var childrenMap = new HashMap<Long, List<CommunityDocumentAndFolder>>();
        documentsAndFolders
                .forEach(entity ->
                        childrenMap.computeIfAbsent(entity.getFolderId(), (k) -> new ArrayList<>())
                                .add(entity)
                );
        return childrenMap;
    }

    private CommunityDocumentAndFolder findFolderInListById(List<CommunityDocumentAndFolder> folders, Long rootFolderId) {

        if (rootFolderId == null) return null;

        return folders.stream()
                .filter(it -> Objects.equals(DocumentAndFolderUtils.toFolderId(rootFolderId), it.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Optional<Instant> getOldestDate(Long communityId) {
        return dao.findFirstByCommunityIdAndLastModifiedTimeIsNotNullOrderByLastModifiedTimeAsc(communityId)
                .map(CommunityDocumentAndFolder::getLastModifiedTime);
    }

    @Override
    public InputStream readDocument(CommunityDocumentAndFolder document) {
        return Objects.requireNonNull(documentReaderMap.get(document.getType()))
                .apply(document);
    }

    @Override
    public <P> P fetchByIdAndType(String id, DocumentAndFolderType type, Class<P> projectionClass) {
        return dao.findByIdAndType(id, type, projectionClass);
    }

    private List<CommunityDocumentAndFolder> findDocumentsByCommunityIdAndFolderIds(
            Long communityId,
            List<Long> folderIds,
            PermissionFilter permissionFilter
    ) {
        return dao.findAll(
                specificationGenerator.byFolderIds(folderIds)
                        .and(specificationGenerator.byCommunityId(communityId))
                        .and(specificationGenerator.excludePermanentlyDeleted())
                        .and(specificationGenerator.excludeTemporarilyDeleted())
                        .and(specificationGenerator.byTypeIn(DocumentAndFolderType.documentTypes()))
                        .and(
                                Specification.not(specificationGenerator.byType(DocumentAndFolderType.TEMPLATE))
                                        .or(specificationGenerator.byAccessibleTemplateStatuses(permissionFilter))
                        )
        );
    }

    private DocumentTreeItem<CommunityDocumentAndFolder> buildDocumentAndFolderTree(
            Map<Long, List<CommunityDocumentAndFolder>> childrenMap,
            CommunityDocumentAndFolder entity
    ) {
        if (entity == null || entity.getType().isFolderType()) {
            var folderId = entity != null ? DocumentAndFolderUtils.getFolderId(entity.getId()) : null;
            if (childrenMap.containsKey(folderId)) {
                var children = childrenMap.get(folderId).stream()
                        .flatMap(it -> Stream.ofNullable(buildDocumentAndFolderTree(childrenMap, it)))
                        .collect(Collectors.toList());
                return DocumentTreeItem.folder(entity, children);
            } else {
                return DocumentTreeItem.folder(entity, List.of());
            }
        } else {
            return DocumentTreeItem.file(entity);
        }
    }
}
