package com.scnsoft.eldermark.converter.prospect;

import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.SecondOccupant;
import com.scnsoft.eldermark.entity.prospect.history.ProspectHistory;
import com.scnsoft.eldermark.entity.prospect.history.SecondOccupantHistory;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
@Component
public class ProspectHistoryConverter implements Converter<Prospect, ProspectHistory> {

    @Autowired
    private Converter<SecondOccupant, SecondOccupantHistory> secondOccupantHistoryConverter;

    @Override
    public ProspectHistory convert(Prospect source) {
        var target = new ProspectHistory();
        target.setProspect(source);
        target.setOrganization(source.getOrganization());
        target.setExternalId(source.getExternalId());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setMiddleName(source.getMiddleName());
        target.setInNetworkInsurance(source.getInNetworkInsurance());
        target.setInsurancePlan(source.getInsurancePlan());
        target.setGender(source.getGender());
        target.setSocialSecurity(source.getSocialSecurity());
        target.setMaritalStatus(source.getMaritalStatus());
        target.setRace(source.getRace());
        target.setBirthDate(source.getBirthDate());
        target.setVeteran(source.getVeteran());

        PersonTelecomUtils.find(source.getPerson(), PersonTelecomCode.MC)
                .map(PersonTelecom::getNormalized)
                .ifPresent(target::setCellPhone);

        PersonTelecomUtils.find(source.getPerson(), PersonTelecomCode.EMAIL)
                .map(PersonTelecom::getNormalized)
                .ifPresent(target::setEmail);

        Optional.ofNullable(source.getPerson())
                .map(Person::getAddresses)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .ifPresent(address -> {
                    target.setCity(address.getCity());
                    target.setZip(address.getPostalCode());
                    target.setState(address.getState());
                    target.setStreet(address.getStreetAddress());
                });

        target.setCommunity(source.getCommunity());
        target.setMoveInDate(source.getMoveInDate());
        target.setRentalAgreementDate(source.getRentalAgreementDate());
        target.setAssessmentDate(source.getAssessmentDate());
        target.setReferralSource(source.getReferralSource());
        target.setNotes(source.getNotes());
        target.setRelatedPartyFirstName(source.getRelatedPartyFirstName());
        target.setRelatedPartyLastName(source.getRelatedPartyLastName());

        PersonTelecomUtils.find(source.getRelatedPartyPerson(), PersonTelecomCode.MC)
                .map(PersonTelecom::getNormalized)
                .ifPresent(target::setRelatedPartyCellPhone);

        PersonTelecomUtils.find(source.getRelatedPartyPerson(), PersonTelecomCode.EMAIL)
                .map(PersonTelecom::getNormalized)
                .ifPresent(target::setRelatedPartyEmail);

        Optional.ofNullable(source.getRelatedPartyPerson())
                .map(Person::getAddresses)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .ifPresent(address -> {
                    target.setRelatedPartyCity(address.getCity());
                    target.setRelatedPartyZip(address.getPostalCode());
                    target.setRelatedPartyState(address.getState());
                    target.setRelatedPartyStreet(address.getStreetAddress());
                });

        target.setRelatedPartyRelationship(source.getRelatedPartyRelationship());
        Optional.ofNullable(source.getSecondOccupant())
                .map(secondOccupantHistoryConverter::convert)
                .ifPresent(target::setSecondOccupantHistory);
        target.setDeactivationDate(source.getDeactivationDate());
        target.setActivationDate(source.getActivationDate());
        target.setActivationComment(source.getActivationComment());
        target.setDeactivationComment(source.getDeactivationComment());
        target.setActive(source.getActive());
        target.setDeactivationReason(source.getDeactivationReason());
        target.setCreatedDate(source.getCreatedDate());
        target.setLastModifiedDate(source.getLastModifiedDate());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setCreatedBy(source.getCreatedBy());

        return target;
    }
}
