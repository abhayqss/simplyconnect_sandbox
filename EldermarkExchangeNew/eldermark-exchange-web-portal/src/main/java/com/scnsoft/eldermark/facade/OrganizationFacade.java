package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityFilter;
import com.scnsoft.eldermark.beans.OrganizationFilter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.dto.directory.DirOrganizationListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrganizationFacade {

    List<DirOrganizationListItemDto> findAll(OrganizationFilter filter);

    Page<OrganizationListItemDto> find(Pageable pageable, String name);

    Long add(OrganizationDto organizationDto);

    Long edit(OrganizationDto organizationDto);

    OrganizationBaseDto findById(Long id, Boolean marketplaceDataIncluded);

    Long count();

    boolean canAdd();

    FileBytesDto downloadLogo(Long organizationId);

    OrganizationUniquenessDto validateUniqueFields(String oid, String name, String companyId, Long organizationId);

    List<IdentifiedTitledEntityDto> findChatAccessible(ConversationParticipantAccessibilityFilter filter);

    OrganizationPermissionsDto getPermissions();
}
