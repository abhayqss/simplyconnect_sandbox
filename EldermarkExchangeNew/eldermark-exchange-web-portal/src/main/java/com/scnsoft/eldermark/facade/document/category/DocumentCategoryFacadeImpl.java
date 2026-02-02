package com.scnsoft.eldermark.facade.document.category;

import com.scnsoft.eldermark.beans.security.projection.dto.DocumentCategorySecurityFieldsAwareImpl;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryDto;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.service.document.category.DocumentCategoryService;
import com.scnsoft.eldermark.service.security.DocumentCategorySecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentCategoryFacadeImpl implements DocumentCategoryFacade {

    @Autowired
    private Converter<DocumentCategoryDto, DocumentCategory> entityConverter;

    @Autowired
    private Converter<DocumentCategory, DocumentCategoryDto> dtoConverter;

    @Autowired
    private Converter<DocumentCategory, DocumentCategoryItemDto> itemDtoConverter;

    @Autowired
    private DocumentCategoryService documentCategoryService;

    @Autowired
    private DocumentCategorySecurityService documentCategorySecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    @PreAuthorize("@documentCategorySecurityService.canViewList(#organizationId)")
    public Page<DocumentCategoryDto> findByOrganizationId(Long organizationId, Pageable pageable) {
        return documentCategoryService.findAllByOrganizationId(
                organizationId,
                PaginationUtils.applyEntitySort(pageable, DocumentCategoryDto.class)
            )
            .map(dtoConverter::convert)
            .map(it -> {
                it.setCanEdit(documentCategorySecurityService.canEdit(it.getId()));
                it.setCanDelete(documentCategorySecurityService.canDelete(it.getId()));
                return it;
            });
    }

    @Override
    @PreAuthorize("@documentCategorySecurityService.canViewList(#organizationId)")
    public List<DocumentCategoryItemDto> findByOrganizationId(Long organizationId) {
        return documentCategoryService.findAllByOrganizationId(organizationId).stream()
            .map(itemDtoConverter::convert)
            .sorted(Comparator.comparing(DocumentCategoryItemDto::getName))
            .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@documentCategorySecurityService.canAdd(#dto)")
    public Long add(DocumentCategoryDto dto) {
        return save(dto);
    }

    @Override
    @PreAuthorize("@documentCategorySecurityService.canEdit(#dto.id)")
    public Long edit(DocumentCategoryDto dto) {
        return save(dto);
    }

    @Override
    @PreAuthorize("@documentCategorySecurityService.canDelete(#id)")
    public void deleteById(Long id) {
        var category = documentCategoryService.findById(id);
        documentCategoryService.deleteAuditableEntity(category);
    }

    @Override
    @PreAuthorize("#categoryId == null " +
        "? @documentCategorySecurityService.canAdd(new com.scnsoft.eldermark.beans.security.projection.dto.DocumentCategorySecurityFieldsAwareImpl(#organizationId)) " +
        ": @documentCategorySecurityService.canEdit(#categoryId)"
    )
    public boolean validateUniqueInOrganization(@Nullable Long categoryId, Long organizationId, String name) {
        return documentCategoryService.isNameUniqueInOrganization(categoryId, organizationId, name);
    }

    @Override
    public boolean canAdd(Long organizationId) {
        return documentCategorySecurityService.canAdd(new DocumentCategorySecurityFieldsAwareImpl(organizationId));
    }

    @Override
    public boolean canViewList(Long organizationId) {
        return documentCategorySecurityService.canViewList(organizationId);
    }

    private Long save(DocumentCategoryDto dto) {
        var category = entityConverter.convert(dto);
        category.setUpdatedByEmployeeId(loggedUserService.getCurrentEmployeeId());
        return documentCategoryService.saveOrUpdate(category);
    }
}
