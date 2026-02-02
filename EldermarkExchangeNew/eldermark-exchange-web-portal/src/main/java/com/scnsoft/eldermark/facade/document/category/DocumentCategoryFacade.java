package com.scnsoft.eldermark.facade.document.category;

import com.scnsoft.eldermark.dto.document.category.DocumentCategoryDto;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;

public interface DocumentCategoryFacade {

    Page<DocumentCategoryDto> findByOrganizationId(Long organizationId, Pageable pageable);

    List<DocumentCategoryItemDto> findByOrganizationId(Long organizationId);

    Long add(DocumentCategoryDto documentCategoryDto);

    Long edit(DocumentCategoryDto documentCategoryDto);

    void deleteById(Long categoryId);

    boolean validateUniqueInOrganization(@Nullable Long categoryId, Long organizationId, String name);

    boolean canAdd(Long organizationId);

    boolean canViewList(Long organizationId);
}
