package com.scnsoft.eldermark.api.external.converter;


import com.scnsoft.eldermark.api.shared.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.converter.PersonEntityConverter;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.NameDto;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.SecondOccupant;
import com.scnsoft.eldermark.service.CcdCodeService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.InsuranceNetworkService;
import com.scnsoft.eldermark.service.ProspectService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Optional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ProspectEntityConverter extends PersonEntityConverter implements Converter<ProspectDto, Prospect> {

    @Autowired
    private ProspectService prospectService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private InsuranceNetworkService inNetworkInsuranceService;

    @Override
    public Prospect convert(ProspectDto prospectDto) {
        var target = Optional.ofNullable(prospectDto.getId())
                .map(prospectService::findById)
                .orElseGet(() -> new Prospect());
        fillProspect(target, prospectDto);
        return target;
    }

    private void fillProspect(Prospect target, ProspectDto source) {
        var community = communityService.findById(source.getCommunityId());

        target.setCommunityId(source.getCommunityId());
        target.setCommunity(community);
        target.setOrganizationId(community.getOrganizationId());
        target.setOrganization(community.getOrganization());
        target.setExternalId(source.getIdentifier());

        target.setBirthDate(DateTimeUtils.parseDateToLocalDate(source.getBirthDate()));
        var ssn = CareCoordinationUtils.normalizePhone(
                StringUtils.isBlank(source.getSsn()) ? null : source.getSsn()
        );

        target.setSocialSecurity(ssn);

        Person targetPerson = Optional.ofNullable(target.getPerson()).orElseGet(() -> createNewPerson(community.getOrganizationId()));
        var nameDto = new NameDto(source.getFirstName(), source.getLastName());

        var telecoms = new EnumMap<PersonTelecomCode, String>(PersonTelecomCode.class);
        telecoms.put(PersonTelecomCode.MC, source.getPhone());
        telecoms.put(PersonTelecomCode.EMAIL, source.getEmail());

        var addressDto = new AddressDto();
        addressDto.setZip(source.getZip());
        addressDto.setCity(source.getCity());
        addressDto.setStreet(source.getStreet());
        addressDto.setStateAbbr(source.getState().toString());
        update(targetPerson, addressDto, nameDto, telecoms);
        target.setPerson(targetPerson);


        target.setGender(source.getGender() != null ? ccdCodeService.findByCodeAndValueSet(source.getGender().getCcdCode(), ValueSetEnum.GENDER) : null);
        target.setRace(source.getRace() != null ? ccdCodeService.findByCodeAndValueSet(source.getRace().getCcdCode(), ValueSetEnum.RACE) : null);
        target.setMaritalStatus(source.getMaritalStatus() != null ? ccdCodeService.findByCodeAndValueSet(source.getMaritalStatus().getCcdCode(), ValueSetEnum.MARITAL_STATUS) : null);
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setMiddleName(source.getMiddleName());
        target.setInNetworkInsurance(source.getInNetworkInsuranceId() != null ?
                inNetworkInsuranceService.getById(source.getInNetworkInsuranceId()) : null);
        target.setInsurancePlan(source.getInsurancePlan());
        target.setMoveInDate(source.getMoveInDate() != null ? Instant.ofEpochMilli(source.getMoveInDate()) : null);
        target.setRentalAgreementDate(source.getRentalAgreementDate() != null ? Instant.ofEpochMilli(source.getRentalAgreementDate()) : null);
        target.setAssessmentDate(source.getAssessmentDate() != null ? Instant.ofEpochMilli(source.getAssessmentDate()) : null);
        target.setReferralSource(source.getReferralSource());
        target.setNotes(source.getNotes());
        target.setVeteran(source.getVeteran());


        target.setRelatedPartyFirstName(source.getRelatedPartyFirstName());
        target.setRelatedPartyLastName(source.getRelatedPartyLastName());
        target.setRelatedPartyRelationship(source.getRelatedPartyRelationship());

        if (!StringUtils.isAllBlank(source.getRelatedPartyFirstName(), source.getRelatedPartyLastName())) {
            Person targetRelatedPartyPerson = Optional.ofNullable(target.getRelatedPartyPerson()).orElseGet(() -> createNewPerson(community.getOrganizationId()));
            var relatedPartyNameDto = new NameDto(source.getRelatedPartyFirstName(), source.getRelatedPartyLastName());

            var relatedPartyTelecoms = new EnumMap<PersonTelecomCode, String>(PersonTelecomCode.class);
            relatedPartyTelecoms.put(PersonTelecomCode.MC, source.getRelatedPartyPhone());
            relatedPartyTelecoms.put(PersonTelecomCode.EMAIL, source.getRelatedPartyEmail());

            var relatedPartyAddressDto = new AddressDto();
            relatedPartyAddressDto.setZip(source.getRelatedPartyZip());
            relatedPartyAddressDto.setCity(source.getRelatedPartyCity());
            relatedPartyAddressDto.setStreet(source.getRelatedPartyStreet());
            relatedPartyAddressDto.setStateAbbr(source.getRelatedPartyState().toString());
            update(targetRelatedPartyPerson, relatedPartyAddressDto, relatedPartyNameDto, relatedPartyTelecoms);

            target.setRelatedPartyPerson(targetRelatedPartyPerson);
        } else {
            target.setRelatedPartyPerson(null);
        }


        if (!StringUtils.isAllBlank(source.getSecondOccupantFirstName(), source.getSecondOccupantLastName())) {
            SecondOccupant targetSecondOccupant = Optional.ofNullable(target.getSecondOccupant()).orElseGet(SecondOccupant::new);
            fillSecondOccupant(source, targetSecondOccupant, community.getOrganization());
            target.setSecondOccupant(targetSecondOccupant);
        } else {
            target.setSecondOccupant(null);
        }

        target.setActive(true);

    }

    private void fillSecondOccupant(ProspectDto prospectDto, SecondOccupant target, Organization organization) {

        Person targetSecondOccupantPerson = Optional.ofNullable(target.getPerson()).orElseGet(() -> createNewPerson(organization.getId()));
        var secondOccupantNameDto = new NameDto(prospectDto.getSecondOccupantFirstName(), prospectDto.getSecondOccupantLastName());

        var secondOccupantTelecoms = new EnumMap<PersonTelecomCode, String>(PersonTelecomCode.class);
        secondOccupantTelecoms.put(PersonTelecomCode.MC, prospectDto.getSecondOccupantPhone());
        secondOccupantTelecoms.put(PersonTelecomCode.EMAIL, prospectDto.getSecondOccupantEmail());

        var secondOccupantAddressDto = new AddressDto();
        secondOccupantAddressDto.setZip(prospectDto.getSecondOccupantZip());
        secondOccupantAddressDto.setCity(prospectDto.getSecondOccupantCity());
        secondOccupantAddressDto.setStreet(prospectDto.getSecondOccupantStreet());
        secondOccupantAddressDto.setStateAbbr(prospectDto.getSecondOccupantState() != null ? prospectDto.getSecondOccupantState().toString() : null);
        update(targetSecondOccupantPerson, secondOccupantAddressDto, secondOccupantNameDto, secondOccupantTelecoms);

        target.setPerson(targetSecondOccupantPerson);


        target.setBirthDate(DateTimeUtils.parseDateToLocalDate(prospectDto.getSecondOccupantBirthDate()));
        var ssn = CareCoordinationUtils.normalizePhone(
                StringUtils.isBlank(prospectDto.getSecondOccupantSsn()) ? null : prospectDto.getSecondOccupantSsn()
        );
        target.setSocialSecurity(ssn);
        target.setGender(prospectDto.getSecondOccupantGender() != null ? ccdCodeService.findByCodeAndValueSet(prospectDto.getSecondOccupantGender().getCcdCode(), ValueSetEnum.GENDER) : null);
        target.setRace(prospectDto.getSecondOccupantRace() != null ? ccdCodeService.findByCodeAndValueSet(prospectDto.getSecondOccupantRace().getCcdCode(), ValueSetEnum.RACE) : null);
        target.setMaritalStatus(prospectDto.getSecondOccupantMaritalStatus() != null ? ccdCodeService.findByCodeAndValueSet(prospectDto.getSecondOccupantMaritalStatus().getCcdCode(), ValueSetEnum.MARITAL_STATUS) : null);

        target.setFirstName(prospectDto.getSecondOccupantFirstName());
        target.setLastName(prospectDto.getSecondOccupantLastName());
        target.setMiddleName(prospectDto.getSecondOccupantMiddleName());
        target.setInNetworkInsurance(prospectDto.getSecondOccupantInNetworkInsuranceId() != null ?
                inNetworkInsuranceService.getById(prospectDto.getSecondOccupantInNetworkInsuranceId()) : null);
        target.setInsurancePlan(prospectDto.getSecondOccupantInsurancePlan());
        target.setVeteran(prospectDto.getSecondOccupantVeteran());
        target.setOrganization(organization);
    }


}
