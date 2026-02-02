package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.SimpleDto;
import com.scnsoft.eldermark.shared.carecoordination.community.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by averazub on 3/23/2016.
 */
@Transactional
public interface CommunityCrudService {
    CommunityViewDto getCommunityDetails(Long id);
    CommunityCreateDto getCommunityCrudDetails(Long id);
    String getCommunityName(Long id);
    Page<CommunityListItemDto> listDto();
    Page<CommunityListItemDto> filterListDto();
    Page<CommunityListItemDto> listDto(Pageable pageRequest, CommunityFilterDto communityFilterDto);
    Page<CommunityListItemDto> listDto(Pageable pageRequest,Long databaseId,boolean filter);
    Page<CommunityListItemDto> listDto(Pageable pageRequest,Long databaseId,boolean filter, CommunityFilterDto communityFilterDto);
    List<CommunityListItemDto>listDto(final Long databaseId);
    CommunityViewDto create(Long databaseId, CommunityCreateDto community, boolean createdAutomatically);
    CommunityViewDto update(Long databaseId, Long communityId, CommunityCreateDto community);
    CommunityViewDto updateData(Long databaseId, Long communityId, CommunityCreateDto community);
    void deleteCommunity(Long communityId);

    Boolean checkIfUnique(CommunityCreateDto data);

    Long getOrCreateCommunityFromSchema(Long databaseId, com.scnsoft.eldermark.schema.Community source);

    Integer getCommunityCountForCurrentUser();
    Integer getCommunityCountForDatabase(Long databaseId);

    void checkViewAccessToCommunitiesOrThrow(Long communityId);
    void checkViewAccessToCommunitiesOrThrow(Long communityId, Long databaseId);
    void checkViewAccessToCommunitiesOrThrow(List<Long> communityIds, boolean filter);
    boolean checkAddEditCareTeamAccessToCommunity(Long communityId, Long careTeamId, Long careTeamEmployeeSelect);

    Long getCommunityIdByOrgAndCommunityOid(String orgOid, String communityOid);

    boolean hasAffiliatedCommunitiesForResident(Long patientId);

    List<SimpleDto>  getCopySettingsCommunities(Long communityId);

    void copySettings(Long communityId, Long copyFromCommunityId);

    Boolean isShowCopySettings(Long communityId, Long databaseId);
    List<Long> getUserCommunityIds(boolean filter, Long employeeId, boolean throwIfCantView);
}
