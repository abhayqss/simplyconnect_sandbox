package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dao.ClientCareTeamMemberDao;
import com.scnsoft.eldermark.dao.CommunityCareTeamMemberDao;
import com.scnsoft.eldermark.dao.ExternalEmployeeInboundReferralCommunityDao;
import com.scnsoft.eldermark.dto.ContactListItemDto;
import com.scnsoft.eldermark.dto.ContactMembershipDto;
import com.scnsoft.eldermark.dto.TypeDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.security.ContactSecurityService;
import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ContactsListDtoConverter implements ListAndItemConverter<EmployeeBasic, ContactListItemDto> {

    @Autowired
    private ClientCareTeamMemberDao clientCareTeamMemberDao;

    @Autowired
    private CommunityCareTeamMemberDao communityCareTeamMemberDao;

    @Autowired
    private ContactSecurityService contactSecurityService;

    @Autowired
    private ExternalEmployeeInboundReferralCommunityDao externalEmployeeInboundReferralCommunityDao;

    @Override
    public ContactListItemDto convert(EmployeeBasic source) {
        ContactListItemDto target = new ContactListItemDto();
        target.setId(source.getId());
        target.setFullName(source.getFullName());
        target.setLogin(source.getLoginName());

        if (StringUtils.isNotEmpty(source.getStatus().toString())) {
            TypeDto status = new TypeDto();
            status.setName(source.getStatus().toString());
            status.setTitle(source.getStatus().toString());
            target.setStatus(status);
        }

        target.setCanEdit(contactSecurityService.canEdit(source.getId(), CareTeamRoleService.ANY_TARGET_ROLE));
        target.setCanEditRole(contactSecurityService.canEditRole(source.getCareTeamRole()));
        if (source.getCareTeamRole() != null) {
            target.setSystemRoleTitle(source.getCareTeamRole().getName());
        }

        if (source.getPerson() != null) {
            target.setEmail(getPersonTelecomValue(source.getPerson(), PersonTelecomCode.EMAIL));
            target.setPhone(getPersonTelecomValue(source.getPerson(), PersonTelecomCode.WP));
        }
        if (source.getLastSessionDateTime() != null){
            target.setLastSessionDate(source.getLastSessionDateTime().toEpochMilli());
        }

        setMemberships(source, target);
        target.setAvatarId(source.getAvatarId());
        return target;
    }

    private void setMemberships(EmployeeBasic source, ContactListItemDto dto) {
        ContactMembershipDto memberships = new ContactMembershipDto();

        List<IdentifiedTitledEntityDto> relatedClients = clientCareTeamMemberDao.findByEmployee_Id(source.getId()).stream()
                .filter(Objects::nonNull)
                .map(clientCareTeamMember -> new IdentifiedTitledEntityDto(clientCareTeamMember.getClient().getId(), clientCareTeamMember.getClient().getFullName()))
                .filter(StreamUtils.distinctByKey(IdentifiedTitledEntityDto::getId))
                .collect(Collectors.toList());
        memberships.setClients(relatedClients);
        memberships.setClientCount((long) relatedClients.size());

        List<IdentifiedTitledEntityDto> relatedCommunities = communityCareTeamMemberDao.findByEmployeeId(source.getId())
                .stream()
                .filter(Objects::nonNull)
                .map(communityCareTeamMember -> new IdentifiedTitledEntityDto(communityCareTeamMember.getCommunity().getId(), communityCareTeamMember.getCommunity().getName()))
                .filter(StreamUtils.distinctByKey(IdentifiedTitledEntityDto::getId))
                .collect(Collectors.toList());
        memberships.setCommunities(relatedCommunities);
        memberships.setCommunityCount((long) relatedCommunities.size());
        List<IdentifiedTitledEntityDto> referralsProcessing = externalEmployeeInboundReferralCommunityDao.findByEmployeeId(source.getId())
                .stream()
                .map(ExternalEmployeeInboundReferralCommunity::getCommunity)
                .map(community -> new IdentifiedTitledEntityDto(community.getId(), community.getName()))
                .collect(Collectors.toList());
        memberships.setReferralsProcessing(referralsProcessing);
        memberships.setReferralsProcessingCount((long) referralsProcessing.size());
        dto.setMemberships(memberships);
    }

    private String getPersonTelecomValue(final Person person, final PersonTelecomCode code) {
        if (CollectionUtils.isNotEmpty(person.getTelecoms())) {
            for (PersonTelecom telecom : person.getTelecoms()) {
                if (code.toString().equals(telecom.getUseCode())) {
                    return telecom.getValue();
                }
            }
        }
        return null;
    }
}
