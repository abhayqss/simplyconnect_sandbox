package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.projection.IdNamesActiveAware;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.ClientNameCommunityIdListItemDto;
import com.scnsoft.eldermark.dto.ContactDto;
import com.scnsoft.eldermark.dto.TypeDto;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.ClientService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ContactItemDtoConverter implements Converter<Employee, ContactDto> {

    @Autowired
    private Converter<Address, AddressDto> addressDtoConverter;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private ClientService clientService;

    @Override
    public ContactDto convert(Employee source) {
        ContactDto target = new ContactDto();
        target.setId(source.getId());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setLogin(source.getLoginName());
        if (source.getCareTeamRole() != null) {
            target.setSystemRoleTitle(source.getCareTeamRole().getName());
            target.setSystemRoleId(source.getCareTeamRole().getId());
            target.setSystemRoleName(source.getCareTeamRole().getCode().getCode());
        }

        if (source.getStatus() != null) {
            TypeDto status = new TypeDto();
            status.setName(source.getStatus().toString());
            status.setTitle(source.getStatus().toString());
            target.setStatus(status);
        }
        if (source.getPerson() != null) {
            //commented since login=email in all contacts created via web
            //Phase 2 will include a solution for 4D contacts which may have login != email
            //target.setEmail(getPersonTelecomValue(source.getPerson(), PersonTelecomCode.EMAIL));
            target.setPhone(getPersonTelecomValue(source.getPerson(), PersonTelecomCode.WP));
            target.setFax(getPersonTelecomValue(source.getPerson(), PersonTelecomCode.FAX));
            target.setMobilePhone(getPersonTelecomValue(source.getPerson(), PersonTelecomCode.MC));
            target.setIsCommunityAddressUsed(source.getIsCommunityAddressUsed());
            target.setAddress(
                source.getIsCommunityAddressUsed()
                    ? extractCommunityAddress(source)
                    : extractPersonAddress(source)
            );
        }
        target.setEnableContact(!EmployeeStatus.INACTIVE.equals(source.getStatus()));
        target.setSecureMail(source.getSecureMessaging());
        target.setSecureMessagingEnabled(source.isSecureMessagingActive());
        if (source.getOrganization() != null) {
            target.setOrganizationName(source.getOrganization().getName());
            target.setOrganizationId(source.getOrganization().getId());
        }

        if (source.getCommunityId() != null) {
            Community community = communityDao.findById(source.getCommunityId()).orElse(null);
            target.setCommunityName(community != null ? community.getName() : null);
            target.setCommunityId(community != null ? community.getId() : null);
        }

        if (source.getAvatar() != null) {
            var avatar = source.getAvatar();
            target.setAvatarId(avatar.getId());
            target.setAvatarName(avatar.getAvatarName());
        }

        if (CollectionUtils.isNotEmpty(source.getAssociatedClientIds())) {
            target.setAssociatedClients(clientService.findAllByIds(source.getAssociatedClientIds()).stream()
                    .map(client -> new ClientNameCommunityIdListItemDto(client.getId(), client.getFullName(), client.getCommunityId(), client.getCommunityName()))
                    .collect(Collectors.toList()));
        }

        target.setQaIncidentReports(BooleanUtils.isTrue(source.getQaIncidentReports()));
        var clientsWithPrimaryContacts = clientService.findWithPrimaryContact(source.getId());
        if (CollectionUtils.isNotEmpty(clientsWithPrimaryContacts)) {
            var activeClientWithPrimaryContactsNames = clientsWithPrimaryContacts
                    .stream().filter(idNamesActiveAware -> BooleanUtils.isTrue(idNamesActiveAware.getActive()))
                    .map(IdNamesActiveAware::getFullName).collect(Collectors.toList());
            target.setActivePrimaryContactClientNames(activeClientWithPrimaryContactsNames);
            target.setInactivePrimaryContactClientsCount(clientsWithPrimaryContacts.size() - activeClientWithPrimaryContactsNames.size());
        }
        return target;
    }

    private AddressDto extractCommunityAddress(Employee source) {
        var communityAddresses = source.getCommunity().getAddresses();
        return CollectionUtils.isNotEmpty(communityAddresses)
            ? addressDtoConverter.convert(communityAddresses.get(0))
            : null;
    }

    private AddressDto extractPersonAddress(Employee source) {
        var personAddresses = source.getPerson().getAddresses();
        return CollectionUtils.isNotEmpty(personAddresses)
            ? addressDtoConverter.convert(personAddresses.get(0))
            : null;
    }

    private String getPersonTelecomValue(Person person, PersonTelecomCode code) {
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
