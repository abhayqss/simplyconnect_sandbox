package com.scnsoft.eldermark.converter.prospect;

import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.prospect.SecondOccupant;
import com.scnsoft.eldermark.entity.prospect.history.SecondOccupantHistory;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
@Component
public class SecondOccupantHistoryConverter implements Converter<SecondOccupant, SecondOccupantHistory> {

    @Override
    public SecondOccupantHistory convert(SecondOccupant source) {
        var target = new SecondOccupantHistory();
        target.setOrganization(source.getOrganization());
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

        return target;
    }
}
