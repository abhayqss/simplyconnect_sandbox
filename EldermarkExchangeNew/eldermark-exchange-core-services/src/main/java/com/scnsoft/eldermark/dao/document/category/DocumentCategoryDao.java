package com.scnsoft.eldermark.dao.document.category;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;

public interface DocumentCategoryDao extends AppJpaRepository<DocumentCategory, Long> {

    boolean existsByOrganizationIdAndNameAndArchived(Long organizationId, String name, boolean archived);

    boolean existsByIdNotAndOrganizationIdAndNameAndArchived(Long id, Long organizationId, String name, boolean archived);

    boolean existsByIdAndOrganizationIdAndArchived(Long id, Long organizationId, boolean archived);
}
