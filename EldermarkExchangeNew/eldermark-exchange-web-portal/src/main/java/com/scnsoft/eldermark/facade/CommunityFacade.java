package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityCommunityFilter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.directory.DirCommunityListItemDto;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityFacade {

    List<DirCommunityListItemDto> findNonBlankByOrgId(Long organizationId, Boolean isMarketplaceEnabledOnly);

    Page<CommunityListItemDto> findByOrgId(Long organizationId, Pageable pageable);

    Long add(CommunityDto communityDto);

    Long edit(CommunityDto communityDto);

    CommunityDto findById(Long id, Boolean marketplaceDataIncluded);

    boolean isExistsAffiliated(Long id);

    Page<CommunityDeviceTypeDto> findDeviceTypeByCommunityId(Long communityId, Pageable pageable);

    Long saveDeviceType(Long communityId, CommunityDeviceTypeDto communityDeviceTypeDto);

    CommunityDeviceTypeDto findDeviceTypeById(Long deviceTypeId);

    Long count(Long organizationId);

    boolean canAdd(Long organizationId);

    FileBytesDto downloadLogo(Long communityId);

    CommunityUniquenessDto validateUniqueFields(Long organizationId, String oid, String name);

    List<IdentifiedTitledEntityDto> findChatAccessible(ConversationParticipantAccessibilityCommunityFilter filter);

    FileBytesDto downloadPictureById(Long pictureId);

    List<ServiceTypeListItemDto> getServices(Long communityId);

    CommunityPermissionsDto getPermissions(Long organizationId);
}
