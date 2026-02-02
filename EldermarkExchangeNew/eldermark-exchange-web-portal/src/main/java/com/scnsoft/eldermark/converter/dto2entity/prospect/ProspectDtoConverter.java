package com.scnsoft.eldermark.converter.dto2entity.prospect;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.dto.prospect.RelatedPartyDto;
import com.scnsoft.eldermark.dto.prospect.SecondOccupantDto;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.ProspectPrimaryContact;
import com.scnsoft.eldermark.entity.prospect.SecondOccupant;
import com.scnsoft.eldermark.service.CcdCodeService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.InsuranceNetworkService;
import com.scnsoft.eldermark.service.ProspectService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.function.BiFunction;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ProspectDtoConverter implements ItemConverter<ProspectDto, Prospect> {

    @Autowired
    private ProspectService prospectService;

    @Autowired
    private InsuranceNetworkService inNetworkInsuranceService;

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private Converter<ProspectDto, Person> prospectPersonEntityConverter;

    @Autowired
    private BiFunction<SecondOccupantDto, ProspectDto, SecondOccupant> secondOccupantEntityConverter;

    @Autowired
    private BiFunction<RelatedPartyDto, ProspectDto, Person> relatedPartyPersonEntityConverter;

    @Autowired
    private Converter<ProspectDto, ProspectPrimaryContact> prospectPrimaryContactConverter;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    public Prospect convert(ProspectDto source) {
        var target = source.getId() != null
                ? prospectService.findById(source.getId())
                : new Prospect();

        convert(source, target);

        return target;
    }

    @Override
    public void convert(ProspectDto source, Prospect target) {
        var community = communityService.findById(source.getCommunityId());

        target.setCommunityId(source.getCommunityId());
        target.setCommunity(community);

        target.setOrganizationId(source.getOrganizationId());
        target.setOrganization(community.getOrganization());

        target.setBirthDate(DateTimeUtils.parseDateToLocalDate(source.getBirthDate()));
        var ssn = CareCoordinationUtils.normalizePhone(
                StringUtils.isBlank(source.getSsn()) ? null : source.getSsn()
        );

        target.setSocialSecurity(ssn);
        target.setPerson(prospectPersonEntityConverter.convert(source));
        target.setGender(source.getGenderId() != null ? ccdCodeService.findById(source.getGenderId()) : null);
        target.setRace(source.getRaceId() != null ? ccdCodeService.findById(source.getRaceId()) : null);
        target.setMaritalStatus(source.getMaritalStatusId() != null ? ccdCodeService.findById(source.getMaritalStatusId()) : null);
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setMiddleName(source.getMiddleName());
        target.setInNetworkInsurance(source.getInsuranceNetworkId() != null ?
                inNetworkInsuranceService.getById(source.getInsuranceNetworkId()) : null);
        target.setInsurancePlan(source.getInsurancePaymentPlan());
        target.setPrimaryContact(prospectPrimaryContactConverter.convert(source));

        target.setMoveInDate(source.getMoveInDate() != null ? Instant.ofEpochMilli(source.getMoveInDate()) : null);
        target.setRentalAgreementDate(source.getRentalAgreementSignedDate() != null ? Instant.ofEpochMilli(source.getRentalAgreementSignedDate()) : null);
        target.setAssessmentDate(source.getAssessmentDate() != null ? Instant.ofEpochMilli(source.getAssessmentDate()) : null);
        target.setReferralSource(source.getReferralSource());
        target.setNotes(source.getNotes());
        target.setVeteran(source.getVeteranStatusName());

        var relatedPartyDto = source.getRelatedParty();
        if (relatedPartyDto != null) {
            target.setRelatedPartyFirstName(relatedPartyDto.getFirstName());
            target.setRelatedPartyLastName(relatedPartyDto.getLastName());
            target.setRelatedPartyRelationship(relatedPartyDto.getRelationshipTypeName());
            target.setRelatedPartyPerson(relatedPartyPersonEntityConverter.apply(relatedPartyDto, source));
        }

        var secondOccupantDto = source.getSecondOccupant();
        if (secondOccupantDto != null) {
            target.setSecondOccupant(secondOccupantEntityConverter.apply(secondOccupantDto, source));
        } else {
            target.setSecondOccupant(null);
        }
        target.setActive(true);
        var currentEmployee = loggedUserService.getCurrentEmployee();
        target.setUpdatedBy(currentEmployee);
        target.setUpdatedById(currentEmployee.getId());
        if (target.getId() == null) {
            target.setCreatedBy(currentEmployee);
            target.setCreatedById(currentEmployee.getId());
        }
    }
}
