package com.scnsoft.eldermark.service.document.category;

import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.service.ProjectingService;
import com.scnsoft.eldermark.service.basic.AuditableEntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

public interface DocumentCategoryService extends AuditableEntityService<DocumentCategory>, ProjectingService<Long> {

    List<DocumentCategory> findAllByOrganizationIdAndIds(Long organizationId, List<Long> ids);

    Page<DocumentCategory> findAllByOrganizationId(Long organizationId, Pageable pageable);

    List<DocumentCategory> findAllByOrganizationId(Long organizationId);

    List<DocumentCategory> findAllByCategoryChainIds(List<Long> categoryChainIds);

    Long saveOrUpdate(DocumentCategory documentCategory);

    boolean isNameUniqueInOrganization(@Nullable Long categoryId, Long organizationId, String name);

    boolean isUsed(Long id);

    Set<Long> findChainCategoryIdsByOrganizationIdAndIds(Long organizationId, List<Long> ids);

    Set<Long> findChainCategoryIdsByIds(List<Long> ids);
}
