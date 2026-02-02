package com.scnsoft.eldermark.facade.document.folder;

import com.scnsoft.eldermark.beans.DocumentFolderFilter;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.document.folder.*;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevel;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.document.CommunityDocumentAndFolderService;
import com.scnsoft.eldermark.service.document.folder.*;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode.ADMIN;
import static com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode.UPLOADER;

@Service
public class DocumentFolderFacadeImpl implements DocumentFolderFacade {

    @Autowired
    private ItemConverter<DocumentFolderDto, DocumentFolder> folderEntityConverter;

    @Autowired
    private Converter<DocumentFolderFilterDto, DocumentFolderFilter> folderFilterConverter;

    @Autowired
    private Converter<DocumentFolder, DocumentFolderDto> folderDtoConverter;

    @Autowired
    private Converter<DocumentFolder, DocumentFolderItemDto> folderItemDtoConverter;

    @Autowired
    private Converter<DocumentFolderPermissionLevel, DocumentFolderPermissionLevelDto> folderPermissionLevelDtoConverter;

    @Autowired
    private Converter<Employee, PermissionContactDto> permissionContactDtoConverter;

    @Autowired
    private DocumentFolderService folderService;

    @Autowired
    private DocumentFolderPermissionLevelService folderPermissionLevelService;

    @Autowired
    @Qualifier("documentFolderSecurityService")
    private DocumentFolderSecurityService folderSecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private CommunityDocumentAndFolderService documentAndFolderService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private CommunityService communityService;

    @Override
    @PreAuthorize("@documentFolderSecurityService.canView(#folderId)")
    @Transactional(readOnly = true)
    public DocumentFolderDto findById(Long folderId) {
        var folder = folderService.findById(folderId);
        return folderDtoConverter.convert(folder);
    }

    @Override
    @PreAuthorize(
            "#filterDto.organizationId != null " +
                    "? @documentFolderSecurityService.canViewListInOrganization(#filterDto.organizationId) " +
                    ": @documentFolderSecurityService.canViewListInCommunities(#filterDto.communityIds)"
    )
    @Transactional(readOnly = true)
    public List<DocumentFolderItemDto> getList(DocumentFolderFilterDto filterDto) {

        var filter = Objects.requireNonNull(folderFilterConverter.convert(filterDto));

        return folderService.find(filter).stream()
                .map(folderItemDtoConverter::convert)
                .sorted(
                        Comparator.comparing(DocumentFolderItemDto::getName)
                                .thenComparingLong(DocumentFolderItemDto::getId)
                )
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@documentFolderSecurityService.canAdd(#dto)")
    @Transactional
    public Long add(DocumentFolderDto dto) {
        validate(dto);
        var folder = Objects.requireNonNull(folderEntityConverter.convert(dto));
        folder.setAuthor(loggedUserService.getCurrentEmployee());
        return folderService.save(folder);
    }

    @Override
    @PreAuthorize("@documentFolderSecurityService.canEdit(#dto.id)")
    @Transactional
    public Long edit(DocumentFolderDto dto) {
        var folder = folderService.findById(dto.getId());
        validate(dto);
        folderEntityConverter.convert(dto, folder);
        return folderService.save(folder);
    }

    @Override
    @PreAuthorize(
            "#id == null" +
                    " ? @documentFolderSecurityService.canAdd(T(com.scnsoft.eldermark.service.document.folder.DocumentFolderSecurityFieldsAware).of(#communityId, #parentId))" +
                    " : @documentFolderSecurityService.canEdit(#id)"
    )
    @Transactional(readOnly = true)
    public boolean validateUniqueness(Long id, Long parentId, Long communityId, String name) {
        return folderService.isUnique(id, communityId, parentId, name);
    }

    @Override
    public List<DocumentFolderPermissionLevelDto> findPermissionLevels() {
        return folderPermissionLevelService.findAll().stream()
                .sorted(Comparator.comparing(it -> it.getCode().getPriority()))
                .map(folderPermissionLevelDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize(
            "#folderId == null" +
                    " ? @documentFolderSecurityService.canAdd(T(com.scnsoft.eldermark.service.document.folder.DocumentFolderSecurityFieldsAware).of(#communityId, #parentFolderId))" +
                    " : @documentFolderSecurityService.canEdit(#folderId)"
    )
    @Transactional(readOnly = true)
    public DocumentFolderDto getDefaultFolder(Long folderId, Long parentFolderId, Long communityId, String name, boolean isSecurityEnabled) {
        var defaultFolder = folderService.getDefaultFolder(
                loggedUserService.getCurrentEmployee(),
                folderId,
                parentFolderId,
                communityId,
                name,
                isSecurityEnabled
        );
        return Objects.requireNonNull(folderDtoConverter.convert(defaultFolder));
    }

    @Override
    @PreAuthorize(
            "#folderId == null" +
                    " ? @documentFolderSecurityService.canAdd(T(com.scnsoft.eldermark.service.document.folder.DocumentFolderSecurityFieldsAware).of(#communityId, #parentFolderId))" +
                    " : @documentFolderSecurityService.canEdit(#folderId)"
    )
    @Transactional(readOnly = true)
    public Page<PermissionContactDto> getContacts(
            Long folderId,
            Long parentFolderId,
            Long communityId,
            PermissionContactFilter filter,
            Pageable pageable
    ) {
        Long id = parentFolderId != null
                ? parentFolderId
                : (folderId != null ? folderService.findById(folderId).getParentId() : null);

        var employees = folderService.getEmployeesAvailableForFolderPermissions(
                id,
                communityId,
                filter,
                PaginationUtils.applyEntitySort(pageable, PermissionContactDto.class)
        );

        return employees.map(permissionContactDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long parentFolderId, Long communityId) {
        return folderSecurityService.canAdd(DocumentFolderSecurityFieldsAware.of(communityId, parentFolderId));
    }

    @Override
    public boolean canView(Long folderId, Long communityId) {
        return folderSecurityService.canViewList(DocumentFolderSecurityFieldsAware.of(communityId, folderId));
    }

    @Override
    @PreAuthorize("@documentFolderSecurityService.canDownload(#id)")
    @Transactional(readOnly = true)
    public void download(Long id, HttpServletResponse httpResponse) {
        var folder = folderService.findById(id);

        var documentTree = documentAndFolderService.getDocumentTree(
                folder.getCommunityId(),
                folder.getId(),
                permissionFilterService.createPermissionFilterForCurrentUser()
        );

        var bytes = WriterUtils.generateZip(documentTree, documentAndFolderService::readDocument);
        WriterUtils.copyBytesAsZipToResponse("Company Documents", bytes, httpResponse);
    }

    @Override
    @PreAuthorize("@documentFolderSecurityService.canDelete(#id)")
    @Transactional
    public void delete(Long id, boolean isTemporary) {
        var curEmployee = loggedUserService.getCurrentEmployee();
        if (isTemporary) {
            folderService.temporaryDelete(id, curEmployee);
        } else {
            folderService.permanentlyDelete(id, curEmployee);
        }
    }

    @Override
    @PreAuthorize("@documentFolderSecurityService.canRestore(#id)")
    @Transactional
    public void restore(Long id) {
        var curEmployee = loggedUserService.getCurrentEmployee();
        folderService.restore(id, curEmployee);
    }

    @Override
    @PreAuthorize(
            "#organizationId != null " +
                    "? @documentFolderSecurityService.canViewListInOrganization(#organizationId) " +
                    ": @documentFolderSecurityService.canViewListInCommunities(#communityIds)"
    )
    public List<DocumentFolderItemDto> getDefaultTemplateFolders(Long organizationId, List<Long> communityIds) {

        List<Long> finalCommunityIds;
        if (organizationId != null) {
            finalCommunityIds = communityService.findAllByOrgId(organizationId).stream()
                    .map(IdAware::getId)
                    .collect(Collectors.toList());
        } else {
            finalCommunityIds = communityIds;
        }

        return folderService.findDefaultTemplateFolders(finalCommunityIds).stream()
                .map(folderItemDtoConverter::convert)
                .collect(Collectors.toList());
    }

    private void extractFoldersByPermission(
            ArrayList<DocumentFolder> result,
            Predicate<DocumentFolder> hasPermissionPredicate,
            Map<Long, List<DocumentFolder>> childrenMap,
            Long folderId
    ) {
        if (childrenMap.containsKey(folderId)) {
            childrenMap.get(folderId).forEach(folder -> {
                if (hasPermissionPredicate.test(folder)) {
                    result.add(folder);
                    extractFoldersByPermission(result, hasPermissionPredicate, childrenMap, folder.getId());
                }
            });
        }
    }

    private Predicate<DocumentFolder> getHasPermissionPredicate(Long employeeId, boolean canUpload) {

        Predicate<DocumentFolderPermissionLevelCode> permissionLevelPredicate = canUpload
                ? level -> level == ADMIN || level == UPLOADER
                : level -> true;

        return folder -> {
            if (folder.getDeletionTime() != null || folder.getTemporaryDeletionTime() != null) {
                return false;
            }
            if (folder.getIsSecurityEnabled()) {
                return folder.getPermissions().stream()
                        .filter(it -> Objects.equals(employeeId, it.getEmployeeId()))
                        .anyMatch(it -> permissionLevelPredicate.test(it.getPermissionLevel().getCode()));
            } else {
                return true;
            }
        };
    }

    //todo - move validations to service layer
    private void validate(DocumentFolderDto dto) {
        if (dto.getId() == null) {
            folderService.validateParentCommunity(dto.getCommunityId(), dto.getParentId());
        } else {
            folderService.validateParent(dto.getId(), dto.getParentId());
        }

        folderService.validateUniqueness(dto.getId(), dto.getCommunityId(), dto.getParentId(), dto.getName());

        folderService.validatePermissions(
                dto.getCommunityId(),
                dto.getParentId(),
                dto.getIsSecurityEnabled(),
                CollectionUtils.isEmpty(dto.getPermissions())
                        ? List.of()
                        : dto.getPermissions().stream()
                        .map(it -> new FolderPermission(it.getContactId(), folderPermissionLevelService.findById(it.getPermissionLevelId())))
                        .collect(Collectors.toList())
        );
    }
}
