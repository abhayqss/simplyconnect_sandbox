package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.beans.DocumentFolderFilter;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.document.folder.DocumentFolderDao;
import com.scnsoft.eldermark.dao.document.folder.DocumentFolderPermissionDao;
import com.scnsoft.eldermark.dao.document.folder.DocumentFolderPermissionSpecificationGenerator;
import com.scnsoft.eldermark.dao.document.folder.DocumentFolderSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderParentAware;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermission;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.document.DocumentService;
import com.scnsoft.eldermark.service.document.DocumentTreeItem;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class DocumentFolderServiceImpl implements DocumentFolderService {

    public static final String TEMPLATE_FOLDER_DEFAULT_NAME = "E-sign Documents and Forms";

    @Autowired
    private DocumentFolderDao folderDao;

    @Autowired
    private DocumentFolderPermissionDao folderPermissionDao;

    @Autowired
    private DocumentFolderPermissionLevelService folderPermissionLevelService;

    @Autowired
    private DocumentFolderSpecificationGenerator folderSpecGenerator;

    @Autowired
    private DocumentFolderPermissionSpecificationGenerator folderPermissionSpecGenerator;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @Autowired
    private CommunityService communityService;

    @Autowired
    @Qualifier("documentService")
    private DocumentService documentService;

    @Override
    @Transactional(readOnly = true)
    public DocumentFolder findById(Long id) {
        return folderDao.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentFolder> find(DocumentFolderFilter filter) {
        return filter.getCommunityIds().stream()
                .flatMap(communityId -> findByCommunityId(communityId, filter).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Long save(DocumentFolder folder) {
        if (folder.getId() != null) {
            folder.setUpdateTime(Instant.now());
        } else {
            folder.setCreationTime(Instant.now());
        }
        updateFolderType(folder);
        folderDao.save(folder);
        updateChildrenSecurity(folder);
        return folder.getId();
    }

    private void updateFolderType(DocumentFolder folder) {
        var folderType = folder.getParentId() == null
                ? DocumentFolderType.REGULAR
                : findById(folder.getParentId()).getType();
        folder.setType(folderType);
        if (folder.getType() == DocumentFolderType.TEMPLATE) {
            folder.setIsSecurityEnabled(false);
            folder.getPermissions().clear();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderPermission> resolveFolderPermissions(DocumentFolder folder) {
        if (folder != null && folder.getIsSecurityEnabled()) {
            return folder.getPermissions().stream()
                    .map(it -> new FolderPermission(it.getEmployeeId(), it.getPermissionLevel()))
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderPermission> resolveFolderPermissions(Long folderId) {
        return folderId != null
                ? resolveFolderPermissions(findById(folderId))
                : List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public void validateParentCommunity(Long communityId, Long parentId) {
        if (parentId != null) {
            var parentFolder = folderDao.findById(parentId, CommunityIdAware.class)
                    .orElseThrow(() -> new ValidationException("Invalid parent folder id"));
            if (!parentFolder.getCommunityId().equals(communityId)) {
                throw new ValidationException("Provided community id should be the same as for parent folder");
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateParent(Long folderId, Long parentId) {
        if (parentId != null) {
            var folder = findById(folderId);

            var children = findChildren(folder, DocumentFolderParentAware.class);

            var parentIsSubFolder = children.stream()
                    .map(IdAware::getId)
                    .anyMatch(childId -> Objects.equals(childId, parentId));

            if (parentIsSubFolder) {
                throw new ValidationException("Parent id is invalid");
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateUniqueness(Long folderId, Long communityId, Long parentId, String name) {
        if (!isUnique(folderId, communityId, parentId, name)) {
            throw new ValidationException("Folder already exists");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validatePermissions(Long communityId, Long parentId, Boolean isSecurityEnabled, List<FolderPermission> permissions) {
        if (parentId != null) {
            var parent = findById(parentId);

            if (parent.getIsSecurityEnabled()) {
                if (!isSecurityEnabled) {
                    throw new ValidationException("Cannot disable security for folder in secured parent folder");
                }

                var parentPermissions = resolveFolderPermissions(parentId);

                if (!containsAllAdminPermissions(permissions, parentPermissions)) {
                    throw new ValidationException("It's not possible to remove admin from child folder");
                }

                if (!haveSameEmployees(permissions, parentPermissions)) {
                    throw new ValidationException("It's not possible to add permissions to users that don't have access to parent folders");
                }
                return;
            } else if (parent.getType() == DocumentFolderType.TEMPLATE && isSecurityEnabled) {
                throw new ValidationException("It's not possible to add permissions to templates folder");
            }
        }

        if (isSecurityEnabled) {
            var employeeIds = permissions.stream()
                    .map(FolderPermission::getEmployeeId)
                    .collect(Collectors.toList());
            hasViewCommunityDocumentsAccess(communityId, employeeIds);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUnique(Long folderId, Long communityId, Long parentId, String name) {
        return folderId == null
                ? !folderDao.existsByCommunityIdAndParentIdAndNameAndDeletionTimeIsNull(communityId, parentId, name)
                : !folderDao.existsByCommunityIdAndParentIdAndNameAndIdNotAndDeletionTimeIsNull(communityId, parentId, name, folderId);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentFolder getDefaultFolder(
            Employee creator,
            Long folderId,
            Long parentFolderId,
            Long communityId,
            String name,
            boolean isSecurityEnabled
    ) {
        var parent = parentFolderId != null ? findById(parentFolderId) : null;

        var defaultFolder = new DocumentFolder();
        defaultFolder.setId(folderId);
        defaultFolder.setParentId(parentFolderId);
        defaultFolder.setName(name);
        defaultFolder.setCommunity(communityService.get(communityId));
        defaultFolder.setIsSecurityEnabled(isSecurityEnabled);

        if (parent != null) {
            defaultFolder.setType(parent.getType());

            if (CollectionUtils.isNotEmpty(parent.getCategoryChainIds())) {
                defaultFolder.setCategoryChainIds(parent.getCategoryChainIds());
            }

            if (parent.getType() == DocumentFolderType.TEMPLATE) {
                defaultFolder.setIsSecurityEnabled(false);
            } else {
                defaultFolder.setIsSecurityEnabled(parent.getIsSecurityEnabled() || isSecurityEnabled);
            }
        } else {
            defaultFolder.setType(DocumentFolderType.REGULAR);
            defaultFolder.setIsSecurityEnabled(isSecurityEnabled);
        }

        if (defaultFolder.getIsSecurityEnabled()) {
            initDefaultPermissions(creator, defaultFolder);
        }

        return defaultFolder;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employee> getEmployeesAvailableForFolderPermissions(Long parentFolderId, Long communityId, PermissionContactFilter filter, Pageable pageable) {
        var byFilter = employeeSpecificationGenerator.byFullNameLike(filter.getContactFullName());
        if (parentFolderId != null) {
            var parent = findById(parentFolderId);
            if (parent.getIsSecurityEnabled()) {
                var employeeIds = parent.getPermissions().stream()
                        .map(DocumentFolderPermission::getEmployeeId)
                        .collect(Collectors.toList());

                return employeeDao.findAll(byFilter.and(employeeSpecificationGenerator.byIdIn(employeeIds)), pageable);
            }
        }

        return employeeDao.findAll(byFilter.and(employeeSpecificationGenerator.byCommunityDocumentViewAccess(communityId)), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<DocumentFolder>> getChildrenMap(Long communityId) {
        return getChildrenMap(communityId, DocumentFolder.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<DocumentFolder>> getChildrenMap(Long communityId, List<DocumentFolderType> types) {
        return getChildrenMap(communityId, types, DocumentFolder.class);
    }

    @Override
    public void temporaryDelete(Long folderId, Employee deletedByEmployee) {
        var temporaryDeletionTime = Instant.now();
        var folder = folderDao.findById(folderId).orElseThrow();
        if (ObjectUtils.anyNotNull(folder.getTemporaryDeletionTime(), folder.getDeletionTime())) {
            throw new BusinessException("The folder has been already deleted");
        }
        temporaryDeleteFolderWithFilesAndChildrenIfNotAlreadyDeleted(folder, temporaryDeletionTime, deletedByEmployee);
    }

    @Override
    public void permanentlyDelete(Long folderId, Employee deletedByEmployee) {
        var deletionTime = Instant.now();
        var folder = folderDao.findById(folderId).orElseThrow();
        if (folder.getDeletionTime() != null) {
            throw new BusinessException("The folder has been already deleted");
        }
        permanentlyDeleteFolderWithFilesAndChildrenIfNotAlreadyDeleted(folder, deletionTime, deletedByEmployee);
    }

    @Override
    public void restore(Long folderId, Employee restoredByEmployee) {
        var restorationTime = Instant.now();
        var folder = folderDao.findById(folderId).orElseThrow();
        if (folder.getTemporaryDeletionTime() == null || folder.getDeletionTime() != null) {
            throw new BusinessException("The folder can't be restored as it is not temporary deleted");
        }
        var initialFolderTemporaryDeletedTime = folder.getTemporaryDeletionTime();
        restoreFolderWithFilesAndChildren(folder, initialFolderTemporaryDeletedTime, restorationTime, restoredByEmployee);
    }

    private List<DocumentFolderPermission> getChildFoldersPermissions(Long id) {
        if (id == null) {
            return List.of();
        }

        var folderIds = findChildrenWithSecurityEnabled(findById(id))
                .map(IdAware::getId)
                .collect(Collectors.toList());

        return folderPermissionDao.findAll(
                folderPermissionSpecGenerator.unique()
                        .and(folderPermissionSpecGenerator.byFolderIdIn(folderIds))
        );
    }

    private void initDefaultPermissions(Employee creator, DocumentFolder folder) {

        var permissionByEmployeeId = new HashMap<Long, DocumentFolderPermission>();

        var adminPermissionLevel = folderPermissionLevelService.findByCode(DocumentFolderPermissionLevelCode.ADMIN);
        var viewerPermissionLevel = folderPermissionLevelService.findByCode(DocumentFolderPermissionLevelCode.VIEWER);

        var creatorPermissions = new DocumentFolderPermission();
        creatorPermissions.setFolder(folder);
        creatorPermissions.setEmployee(creator);
        creatorPermissions.setPermissionLevel(adminPermissionLevel);

        permissionByEmployeeId.put(creatorPermissions.getEmployeeId(), creatorPermissions);

        if (folder.getParentId() != null) {
            resolveFolderPermissions(folder.getParentId()).stream()
                    .map(it -> {
                        var newPermission = new DocumentFolderPermission();
                        newPermission.setFolder(folder);
                        newPermission.setEmployee(employeeDao.getOne(it.getEmployeeId()));
                        newPermission.setPermissionLevel(it.getPermissionLevel());
                        return newPermission;
                    })
                    .forEach(it -> permissionByEmployeeId.putIfAbsent(it.getEmployeeId(), it));
        } else {
            getChildFoldersPermissions(folder.getId()).stream()
                    .map(it -> {
                        var newPermission = new DocumentFolderPermission();
                        newPermission.setFolder(folder);
                        newPermission.setEmployee(it.getEmployee());
                        newPermission.setPermissionLevel(viewerPermissionLevel);
                        return newPermission;
                    })
                    .forEach(it -> permissionByEmployeeId.putIfAbsent(it.getEmployeeId(), it));
        }

        folder.setPermissions(new ArrayList<>(permissionByEmployeeId.values()));
    }

    private Stream<DocumentFolderParentWithSecurityAware> findChildrenWithSecurityEnabled(DocumentFolder folder) {
        return findChildren(folder, DocumentFolderParentWithSecurityAware.class)
                .stream()
                .filter(DocumentFolderParentWithSecurityAware::getIsSecurityEnabled);
    }

    private void hasViewCommunityDocumentsAccess(Long communityId, List<Long> employeeIds) {

        var hasViewPermissions = employeeSpecificationGenerator.byCommunityDocumentViewAccess(communityId);
        var byEmployeeIds = employeeSpecificationGenerator.byIdIn(employeeIds);
        var isSuperAdmin = employeeSpecificationGenerator.isSuperAdmin();

        if (employeeDao.count(byEmployeeIds.and(hasViewPermissions.or(isSuperAdmin))) != employeeIds.size()) {
            throw new ValidationException("Invalid permissions");
        }
    }

    private void updateChildrenSecurity(DocumentFolder folder) {
        if (folder.getIsSecurityEnabled()) {
            var childIds = getFolderIdTree(folder.getCommunityId(), folder.getId(), f -> true)
                    .childValuesList();

            var adminIds = folder.getPermissions().stream()
                    .filter(it -> it.getPermissionLevel().getCode() == DocumentFolderPermissionLevelCode.ADMIN)
                    .map(DocumentFolderPermission::getEmployeeId)
                    .collect(Collectors.toList());
            addPermissionsForUsers(childIds, adminIds, DocumentFolderPermissionLevelCode.ADMIN);

            var employeeIds = folder.getPermissions().stream()
                    .map(DocumentFolderPermission::getEmployeeId)
                    .collect(Collectors.toList());
            deletePermissionsForUsersNotInList(childIds, employeeIds);

            folderDao.updateSecurityEnabled(childIds, true);
        }
    }

    private void addPermissionsForUsers(List<Long> folderIds, List<Long> employeeIds, DocumentFolderPermissionLevelCode permissionLevel) {
        var byFolderId = folderPermissionSpecGenerator.byFolderIdIn(folderIds);
        var byEmployeeId = folderPermissionSpecGenerator.byEmployeeIdIn(employeeIds);
        var permissions = folderPermissionDao.findAll(byFolderId.and(byEmployeeId));

        permissions.forEach(it -> it.setPermissionLevel(folderPermissionLevelService.findByCode(permissionLevel)));

        var permissionFolderToEmployeeIds = permissions.stream()
                .map(it -> Pair.of(it.getFolderId(), it.getEmployeeId()))
                .collect(Collectors.toSet());

        folderIds.forEach(folderId -> employeeIds.forEach(employeeId -> {
            if (!permissionFolderToEmployeeIds.contains(Pair.of(folderId, employeeId))) {
                var permission = new DocumentFolderPermission();
                permission.setFolder(findById(folderId));
                permission.setEmployee(employeeDao.getOne(employeeId));
                permission.setPermissionLevel(folderPermissionLevelService.findByCode(permissionLevel));
                permissions.add(permission);
            }
        }));

        folderPermissionDao.saveAll(permissions);
    }

    private void deletePermissionsForUsersNotInList(List<Long> folderIds, List<Long> employeeIds) {
        folderPermissionDao.deleteByFolder_IdInAndEmployeeIdNotIn(folderIds, employeeIds);
    }

    /**
     * @param folder target folder
     * @return Child folder list not including <code>folder</code>
     */
    private <T extends DocumentFolderParentAware> List<T> findChildren(DocumentFolder folder, Class<T> projectionClass) {

        var childrenMap = getChildrenMap(folder.getCommunityId(), projectionClass);

        var queue = new LinkedList<Long>();
        queue.push(folder.getId());

        var allChildren = new ArrayList<T>();

        while (!queue.isEmpty()) {
            var childId = queue.poll();
            if (childrenMap.containsKey(childId)) {
                var children = childrenMap.get(childId);

                children.stream()
                        .map(DocumentFolderParentAware::getId)
                        .forEach(queue::push);

                allChildren.addAll(children);
            }
        }
        return allChildren;
    }

    private boolean haveSameEmployees(List<FolderPermission> permissions, List<FolderPermission> parentPermissions) {

        var parentEmployeeIds = parentPermissions.stream()
                .map(FolderPermission::getEmployeeId)
                .collect(Collectors.toSet());

        return permissions.stream()
                .map(FolderPermission::getEmployeeId)
                .allMatch(parentEmployeeIds::contains);
    }

    private boolean containsAllAdminPermissions(List<FolderPermission> permissions, List<FolderPermission> parentPermissions) {

        var admins = permissions.stream()
                .filter(it -> DocumentFolderPermissionLevelCode.ADMIN.equals(it.getPermissionLevel().getCode()))
                .map(FolderPermission::getEmployeeId)
                .collect(Collectors.toSet());

        return parentPermissions.stream()
                .filter(it -> DocumentFolderPermissionLevelCode.ADMIN.equals(it.getPermissionLevel().getCode()))
                .map(FolderPermission::getEmployeeId)
                .allMatch(admins::contains);
    }

    private <T extends DocumentFolderParentAware> Map<Long, List<T>> getChildrenMap(Long communityId, Class<T> projectionClass) {
        return getChildrenMap(projectionClass, folderSpecGenerator.byCommunityId(communityId));
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentTreeItem<Long> getFolderIdTree(Long communityId, Long rootFolderId, Predicate<DocumentFolder> filter) {
        var childrenMap = getChildrenMap(communityId);
        return buildFolderIdTree(rootFolderId, childrenMap, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocumentFolder> findByCommunityIdAndIdIn(Long communityId, Set<Long> folderIds) {
        return folderDao.findByCommunityIdAndIdIn(communityId, folderIds);
    }

    private <T extends DocumentFolderParentAware> Map<Long, List<T>> getChildrenMap(
            Long communityId, List<DocumentFolderType> types, Class<T> projectionClass
    ) {
        var byCommunityId = folderSpecGenerator.byCommunityId(communityId);
        var byTypes = folderSpecGenerator.byTypes(types);
        return getChildrenMap(projectionClass, byCommunityId.and(byTypes));
    }

    private <T extends DocumentFolderParentAware> HashMap<Long, List<T>> getChildrenMap(
            Class<T> projectionClass,
            Specification<DocumentFolder> specification
    ) {
        var childrenMap = new HashMap<Long, List<T>>();

        folderDao.findAll(specification, projectionClass)
                .forEach(folder ->
                        childrenMap.computeIfAbsent(folder.getParentId(), (k) -> new ArrayList<>())
                                .add(folder)
                );

        return childrenMap;
    }

    @Override
    @Transactional
    public Long createTemplateFolder(Long communityId) {
        var folder = new DocumentFolder();
        folder.setType(DocumentFolderType.TEMPLATE);
        folder.setName(TEMPLATE_FOLDER_DEFAULT_NAME);
        folder.setIsSecurityEnabled(false);
        folder.setCreationTime(Instant.now());
        folder.setCommunity(communityService.findById(communityId));
        return folderDao.save(folder).getId();
    }

    @Override
    public List<DocumentFolder> findDefaultTemplateFolders(List<Long> communityIds) {
        return folderDao.findAll(
                folderSpecGenerator.byCommunityIdIn(communityIds)
                        .and(folderSpecGenerator.byTypes(List.of(DocumentFolderType.TEMPLATE)))
                        .and(folderSpecGenerator.byParentFolderId(null))
        );
    }

    @Override
    public List<DocumentFolder> findByIdIn(Collection<Long> folderIds) {
        return folderDao.findByIdIn(folderIds, DocumentFolder.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentFolder> findByDocumentSignatureTemplateId(Long documentSignatureTemplateId) {
        return folderDao.findAll(folderSpecGenerator.byDocumentSignatureTemplateId(documentSignatureTemplateId));
    }


    @Override
    public <P> P findById(Long id, Class<P> projection) {
        return folderDao.findById(id, projection).orElseThrow();
    }

    @Override
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return folderDao.findByIdIn(ids, projection);
    }

    private DocumentTreeItem<Long> buildFolderIdTree(
            Long folderId,
            Map<Long, List<DocumentFolder>> childrenMap,
            Predicate<DocumentFolder> folderFilter
    ) {
        if (childrenMap.containsKey(folderId)) {
            var children = childrenMap.get(folderId).stream()
                    .filter(folderFilter)
                    .map(it -> buildFolderIdTree(it.getId(), childrenMap, folderFilter))
                    .collect(Collectors.toList());
            return DocumentTreeItem.folder(folderId, children);
        } else {
            return DocumentTreeItem.file(folderId);
        }
    }

    private void temporaryDeleteFolderWithFilesAndChildrenIfNotAlreadyDeleted(
            DocumentFolder folder,
            Instant temporaryDeletionTime,
            Employee deletedByEmployee
    ) {
        folder = setTemporaryDeletedIfNotAlreadyDeleted(folder, temporaryDeletionTime, deletedByEmployee);
        folderDao.save(folder);
        documentService.temporaryDeleteDocumentsInFolderIfNotAlreadyTemporaryOrPermanentlyDeleted(
                folder.getId(),
                deletedByEmployee,
                temporaryDeletionTime
        );
        var children = folderDao.findByParentId(folder.getId());
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(documentFolder -> temporaryDeleteFolderWithFilesAndChildrenIfNotAlreadyDeleted(
                    documentFolder,
                    temporaryDeletionTime,
                    deletedByEmployee
            ));
        }
    }

    private DocumentFolder setTemporaryDeletedIfNotAlreadyDeleted(
            DocumentFolder documentFolder,
            Instant temporaryDeletionTime,
            Employee deletedByEmployee) {
        if (documentFolder.getTemporaryDeletionTime() == null && documentFolder.getDeletionTime() == null) {
            documentFolder.setTemporaryDeletedBy(deletedByEmployee);
            documentFolder.setTemporaryDeletionTime(temporaryDeletionTime);
            documentFolder.setUpdateTime(temporaryDeletionTime);
            documentFolder.setRestoredBy(null);
            documentFolder.setRestorationTime(null);
        }
        return documentFolder;
    }

    private void permanentlyDeleteFolderWithFilesAndChildrenIfNotAlreadyDeleted(
            DocumentFolder folder,
            Instant deletionTime,
            Employee deletedByEmployee
    ) {
        if (folder.getTemporaryDeletionTime() == null) {
            throw new BusinessException("Trying to permanently delete folder that is not temporary deleted. Id: " + folder.getId());
        }
        folder = setDeletedIfNotAlreadyDeleted(folder, deletionTime, deletedByEmployee);
        folderDao.save(folder);
        documentService.permanentlyDeleteDocumentsInFolderIfNotAlreadyPermanentlyDeleted(
                folder.getId(),
                deletedByEmployee,
                deletionTime
        );
        var children = folderDao.findByParentId(folder.getId());
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(documentFolder -> permanentlyDeleteFolderWithFilesAndChildrenIfNotAlreadyDeleted(
                    documentFolder,
                    deletionTime,
                    deletedByEmployee
            ));
        }
    }

    private DocumentFolder setDeletedIfNotAlreadyDeleted(
            DocumentFolder documentFolder,
            Instant deletionTime,
            Employee deletedByEmployee) {
        if (documentFolder.getDeletionTime() == null) {
            documentFolder.setDeletedBy(deletedByEmployee);
            documentFolder.setDeletionTime(deletionTime);
            documentFolder.setUpdateTime(deletionTime);
        }
        return documentFolder;
    }

    private void restoreFolderWithFilesAndChildren(
            DocumentFolder folder,
            Instant initialFolderTemporaryDeletedTime,
            Instant restorationTime,
            Employee restoredByEmployee) {
        folder = setRestoredIfTemporaryDeletedAtTime(folder, initialFolderTemporaryDeletedTime, restorationTime, restoredByEmployee);
        folderDao.save(folder);
        documentService.restoreDocumentsInFolderIfTemporaryDeletedAtTime(
                folder.getId(),
                restoredByEmployee,
                restorationTime,
                initialFolderTemporaryDeletedTime
        );
        var children = folderDao.findByParentId(folder.getId());
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(documentFolder -> restoreFolderWithFilesAndChildren(
                    documentFolder,
                    initialFolderTemporaryDeletedTime,
                    restorationTime,
                    restoredByEmployee
            ));
        }
    }

    private DocumentFolder setRestoredIfTemporaryDeletedAtTime(
            DocumentFolder documentFolder,
            Instant initialFolderTemporaryDeletedTime,
            Instant restorationTime,
            Employee restoredByEmployee) {
        if (initialFolderTemporaryDeletedTime.equals(documentFolder.getTemporaryDeletionTime())
                && documentFolder.getDeletionTime() == null) {
            documentFolder.setRestoredBy(restoredByEmployee);
            documentFolder.setRestorationTime(restorationTime);
            documentFolder.setUpdateTime(restorationTime);
            documentFolder.setTemporaryDeletionTime(null);
            documentFolder.setTemporaryDeletedBy(null);
        }
        return documentFolder;
    }

    private List<DocumentFolder> findByCommunityId(Long communityId, DocumentFolderFilter filter) {
        var result = new ArrayList<DocumentFolder>();
        var folderChildrenMap = getChildrenMap(communityId, filter.getTypes());

        filterFoldersByPermissions(
                result,
                getPermissionPredicate(filter),
                folderChildrenMap,
                null
        );

        return result;
    }

    private void filterFoldersByPermissions(
            ArrayList<DocumentFolder> result,
            Predicate<DocumentFolder> hasPermissionPredicate,
            Map<Long, List<DocumentFolder>> childrenMap,
            Long folderId
    ) {
        if (childrenMap.containsKey(folderId)) {
            childrenMap.get(folderId).forEach(folder -> {
                if (hasPermissionPredicate.test(folder)) {
                    result.add(folder);
                    filterFoldersByPermissions(result, hasPermissionPredicate, childrenMap, folder.getId());
                }
            });
        }
    }

    private Predicate<DocumentFolder> getPermissionPredicate(DocumentFolderFilter filter) {
        var employeeIds = filter.getPermissionFilter().getAllEmployeeIds();
        var permissionLevels = filter.getPermissionLevels();
        return folder -> {
            if (folder.getDeletionTime() != null || folder.getTemporaryDeletionTime() != null) {
                return false;
            }
            if (folder.getIsSecurityEnabled()) {
                return folder.getPermissions().stream()
                        .filter(it -> employeeIds.contains(it.getEmployeeId()))
                        .anyMatch(it -> permissionLevels.contains(it.getPermissionLevel().getCode()));
            } else {
                return true;
            }
        };
    }
}
