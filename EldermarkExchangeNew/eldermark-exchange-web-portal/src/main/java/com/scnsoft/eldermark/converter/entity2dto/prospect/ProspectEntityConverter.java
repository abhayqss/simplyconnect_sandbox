package com.scnsoft.eldermark.converter.entity2dto.prospect;

import com.scnsoft.eldermark.beans.projection.IdNameOrganizationIdNameAware;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.client.PrimaryContactDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.dto.prospect.RelatedPartyDto;
import com.scnsoft.eldermark.dto.prospect.SecondOccupantDto;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.ProspectPrimaryContact;
import com.scnsoft.eldermark.entity.prospect.SecondOccupant;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.service.security.ProspectSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ProspectEntityConverter implements Converter<Prospect, ProspectDto> {

    @Autowired
    private ProspectSecurityService prospectSecurityService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private Converter<ProspectPrimaryContact, PrimaryContactDto> prospectPrimaryContactDtoConverter;

    @Autowired
    private Converter<Address, AddressDto> clientAddressDtoConverter;

    @Autowired
    private Converter<Prospect, RelatedPartyDto> relatedPartyDtoConverter;

    @Autowired
    private Converter<SecondOccupant, SecondOccupantDto> secondOccupantDtoConverter;

    @Override
    public ProspectDto convert(Prospect source) {
        var target = new ProspectDto();

        target.setId(source.getId());
        if (source.getGender() != null) {
            target.setGenderId(source.getGender().getId());
            target.setGender(source.getGender().getDisplayName());
        }
        if (source.getRace() != null) {
            target.setRaceId(source.getRace().getId());
            target.setRace(source.getRace().getDisplayName());
        }
        if (source.getMaritalStatus() != null) {
            target.setMaritalStatusId(source.getMaritalStatus().getId());
            target.setMaritalStatus(source.getMaritalStatus().getDisplayName());
        }
        target.setIsActive(source.getActive());
        target.setAssessmentDate(source.getAssessmentDate() != null ? source.getAssessmentDate().toEpochMilli() : null);
        target.setBirthDate(DateTimeUtils.formatLocalDate(source.getBirthDate()));
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setMiddleName(source.getMiddleName());
        target.setFullName(CareCoordinationUtils.getFullName(source.getFirstName(), source.getMiddleName(), source.getLastName()));
        target.setInsuranceNetworkId(source.getInNetworkInsurance() != null ? source.getInNetworkInsurance().getId() : null);
        target.setInsurancePaymentPlan(source.getInsurancePlan());
        target.setMoveInDate(source.getMoveInDate() != null ? source.getMoveInDate().toEpochMilli() : null);
        target.setNotes(source.getNotes());
        target.setPrimaryContact(prospectPrimaryContactDtoConverter.convert(source.getPrimaryContact()));
        target.setReferralSource(source.getReferralSource());
        target.setRentalAgreementSignedDate(source.getRentalAgreementDate() != null ? source.getRentalAgreementDate().toEpochMilli() : null);
        target.setSsn(source.getSocialSecurity());

        if (source.getPerson() != null) {
            for (PersonTelecom telecom : source.getPerson().getTelecoms()) {
                if (PersonTelecomCode.EMAIL.name().equalsIgnoreCase(telecom.getUseCode())) {
                    target.setEmail(telecom.getValue());
                }
                if (PersonTelecomCode.MC.name().equalsIgnoreCase(telecom.getUseCode())) {
                    target.setCellPhone(telecom.getValue());
                }
            }

            if (CollectionUtils.isNotEmpty(source.getPerson().getAddresses())) {
                target.setAddress(clientAddressDtoConverter.convert(source.getPerson().getAddresses().get(0)));
            }
        }
        if (source.getRelatedPartyPerson() != null) {
            target.setRelatedParty(relatedPartyDtoConverter.convert(source));
        }
        target.setSecondOccupant(secondOccupantDtoConverter.convert(source.getSecondOccupant()));
        var communityAware = communityService.findById(source.getCommunityId(), IdNameOrganizationIdNameAware.class);
        target.setCommunityId(communityAware.getId());
        target.setCommunityTitle(communityAware.getName());
        target.setOrganizationId(communityAware.getOrganizationId());
        target.setOrganizationTitle(communityAware.getOrganizationName());

        if (source.getVeteran() != null) {
            target.setVeteranStatusName(source.getVeteran());
            target.setVeteranStatusTitle(source.getVeteran().getTitle());
        }

        if (source.getAvatar() != null) {
            target.setAvatarId(source.getAvatar().getId());
            target.setAvatarName(source.getAvatar().getAvatarName());
        }

        boolean canEdit = prospectSecurityService.canEdit(source.getId());
        target.setCanEdit(canEdit);
        target.setCanEditSsn(canEdit && prospectSecurityService.canEditSsn(source.getId()));

        return target;
    }

}
