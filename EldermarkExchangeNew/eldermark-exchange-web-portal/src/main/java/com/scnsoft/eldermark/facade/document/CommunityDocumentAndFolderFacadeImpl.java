package com.scnsoft.eldermark.facade.document;

import com.scnsoft.eldermark.beans.CommunityDocumentFilter;
import com.scnsoft.eldermark.dto.document.CommunityDocumentFilterDto;
import com.scnsoft.eldermark.dto.document.DocumentAndFolderItemDto;
import com.scnsoft.eldermark.entity.document.CommunityDocumentAndFolder;
import com.scnsoft.eldermark.entity.document.CommunityDocumentAndFolder_;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.document.CommunityDocumentAndFolderSecurityService;
import com.scnsoft.eldermark.service.document.CommunityDocumentAndFolderService;
import com.scnsoft.eldermark.service.document.DocumentAndFolderParentAware;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CommunityDocumentAndFolderFacadeImpl implements CommunityDocumentAndFolderFacade {

    @Autowired
    private Converter<CommunityDocumentAndFolder, DocumentAndFolderItemDto> itemDtoConverter;

    @Autowired
    private Converter<CommunityDocumentFilterDto, CommunityDocumentFilter> documentAndFolderFilterDtoConverter;

    @Autowired
    private CommunityDocumentAndFolderService documentAndFolderService;

    @Autowired
    private CommunityDocumentAndFolderSecurityService documentAndFolderSecurityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    @PreAuthorize(
        "@communityDocumentSecurityService.canViewList(" +
            "T(com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware).of(" +
                "#filterDto.communityId, #filterDto.folderId" +
            ")" +
        ")"
    )
    @Transactional(readOnly = true)
    public Page<DocumentAndFolderItemDto> find(CommunityDocumentFilterDto filterDto, Pageable pageable) {
        var documentFilter = Objects.requireNonNull(documentAndFolderFilterDtoConverter.convert(filterDto));
        documentFilter.setPermissionFilter(permissionFilterService.createPermissionFilterForCurrentUser());
        var finalPageable = PaginationUtils.applyEntitySort(pageable, DocumentAndFolderItemDto.class);
        return documentAndFolderService.find(documentFilter, applySortByType(finalPageable))
                .map(itemDtoConverter::convert);
    }

    @Override
    @PreAuthorize(
            "@communityDocumentSecurityService.canViewList(" +
                "T(com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware).of(" +
                    "#filterDto.communityId, #filterDto.folderId" +
                ")" +
            ")"
    )
    public Long count(CommunityDocumentFilterDto filterDto) {
        var documentFilter = Objects.requireNonNull(documentAndFolderFilterDtoConverter.convert(filterDto));
        documentFilter.setPermissionFilter(permissionFilterService.createPermissionFilterForCurrentUser());
        return documentAndFolderService.count(documentFilter);
    }

    @Override
    @PreAuthorize("@communityDocumentAndFolderSecurityService.canDownloadAll(#ids)")
    @Transactional(readOnly = true)
    public void download(List<String> ids, HttpServletResponse httpResponse) {
        var items = documentAndFolderService.findByIds(ids, DocumentAndFolderParentAware.class);

        var communityId = items.get(0).getCommunityId();
        var folderId = items.get(0).getFolderId();

        var inSameCommunity = items.stream().allMatch(it -> Objects.equals(it.getCommunityId(), communityId));
        var isSameFolder = items.stream().allMatch(it -> Objects.equals(it.getFolderId(), folderId));

        if (!inSameCommunity || !isSameFolder) {
            throw new ValidationException("Document and folder are not in the same folder or community");
        }

        var documentTree = documentAndFolderService.getDocumentTree(
                communityId,
                folderId,
                permissionFilterService.createPermissionFilterForCurrentUser(),
                ids
        );

        var bytes = WriterUtils.generateZip(documentTree, documentAndFolderService::readDocument);
        WriterUtils.copyBytesAsZipToResponse("Company Documents", bytes, httpResponse);
    }

    @Override
    @PreAuthorize("@documentFolderSecurityService.canViewList(T(com.scnsoft.eldermark.service.document.folder.DocumentFolderSecurityFieldsAware).of(#communityId))")
    @Transactional(readOnly = true)
    public Long getOldestDate(Long communityId) {
        return documentAndFolderService.getOldestDate(communityId)
            .map(DateTimeUtils::toEpochMilli)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean canViewList() {
        return documentAndFolderSecurityService.canViewList();
    }

    private Pageable applySortByType(Pageable finalPageable) {
        var sort = finalPageable.getSort();
        var orders = sort.get()
            .flatMap(order -> {
                if (Objects.equals(order.getProperty(), CommunityDocumentAndFolder_.TITLE)) {
                    return Stream.of(
                        new Sort.Order(
                            order.getDirection().isAscending() ? Sort.Direction.DESC : Sort.Direction.ASC,
                            CommunityDocumentAndFolder_.TYPE
                        ),
                        order
                    );
                } else {
                    return Stream.of(order);
                }
            })
            .collect(Collectors.toList());

        finalPageable = PaginationUtils.setSort(finalPageable, Sort.by(orders));
        return finalPageable;
    }
}
