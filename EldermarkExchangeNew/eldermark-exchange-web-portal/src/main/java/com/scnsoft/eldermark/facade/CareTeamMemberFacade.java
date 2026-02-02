package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatClientCareTeamFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatCommunityCareTeamFilter;
import com.scnsoft.eldermark.dto.CareTeamMemberDto;
import com.scnsoft.eldermark.dto.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.dto.CareTeamMemberRoleAvatarAwareDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CareTeamMemberFacade {

    Page<CareTeamMemberListItemDto> find(CareTeamFilter careTeamFilter, Pageable pageable);

    CareTeamMemberDto findById(Long careTeamMemberId);

    void deleteById(Long careTeamMemberId);

    Long add(CareTeamMemberDto careTeamMemberDto);

    Long edit(CareTeamMemberDto careTeamMemberDto);

    List<IdentifiedNamedEntityDto> getContacts(Long organizationId, Long clientId, Long communityId);

    List<IdentifiedNamedEntityDto> getContactsOrganizations(Long clientId, Long communityId, CareTeamFilter.Affiliation affiliation);

    Long count(CareTeamFilter filter);

    List<CareTeamMemberRoleAvatarAwareDto> findChatAccessibleCommunityCareTeamMembers(AccessibleChatCommunityCareTeamFilter filter);

    List<CareTeamMemberRoleAvatarAwareDto> findChatAccessibleClientCareTeamMembers(AccessibleChatClientCareTeamFilter filter);

    List<CareTeamMemberRoleAvatarAwareDto> findVideoCallAccessibleClientCareTeamMembers(AccessibleChatClientCareTeamFilter filter);

    List<CareTeamMemberRoleAvatarAwareDto> findVideoCallAccessibleCommunityCareTeamMembers(AccessibleChatCommunityCareTeamFilter filter);

    Long getContactsCount(Long organizationId, Long clientId, Long communityId);
}