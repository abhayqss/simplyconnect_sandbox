package com.scnsoft.eldermark.converter.entity2dto.prospect;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.prospect.SecondOccupantDto;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.prospect.SecondOccupant;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class SecondOccupantDtoConverter implements Converter<SecondOccupant, SecondOccupantDto> {

    @Autowired
    private Converter<Address, AddressDto> clientAddressDtoConverter;

    @Override
    public SecondOccupantDto convert(SecondOccupant secondOccupant) {
        if (secondOccupant != null) {
            var target = new SecondOccupantDto();

            target.setFirstName(secondOccupant.getFirstName());
            target.setLastName(secondOccupant.getLastName());
            target.setMiddleName(secondOccupant.getMiddleName());
            target.setFullName(CareCoordinationUtils.getFullName(
                    secondOccupant.getFirstName(),
                    secondOccupant.getLastName()
            ));
            var secondOccupantPerson = secondOccupant.getPerson();
            if (secondOccupantPerson != null) {
                for (PersonTelecom telecom : secondOccupantPerson.getTelecoms()) {
                    if (PersonTelecomCode.EMAIL.name().equalsIgnoreCase(telecom.getUseCode())) {
                        target.setEmail(telecom.getValue());
                    }
                    if (PersonTelecomCode.MC.name().equalsIgnoreCase(telecom.getUseCode())) {
                        target.setCellPhone(telecom.getValue());
                    }
                }

                if (CollectionUtils.isNotEmpty(secondOccupantPerson.getAddresses())) {
                    target.setAddress(clientAddressDtoConverter.convert(secondOccupantPerson.getAddresses().get(0)));
                }
            }
            if (secondOccupant.getGender() != null) {
                target.setGenderId(secondOccupant.getGender().getId());
                target.setGender(secondOccupant.getGender().getDisplayName());
            }
            if (secondOccupant.getRace() != null) {
                target.setRaceId(secondOccupant.getRace().getId());
                target.setRace(secondOccupant.getRace().getDisplayName());
            }
            if (secondOccupant.getMaritalStatus() != null) {
                target.setMaritalStatusId(secondOccupant.getMaritalStatus().getId());
                target.setMaritalStatus(secondOccupant.getMaritalStatus().getDisplayName());
            }
            target.setSsn(secondOccupant.getSocialSecurity());
            target.setInsuranceNetworkId(secondOccupant.getInNetworkInsurance() != null ? secondOccupant.getInNetworkInsurance().getId() : null);
            target.setInsurancePaymentPlan(secondOccupant.getInsurancePlan());
            target.setBirthDate(DateTimeUtils.formatLocalDate(secondOccupant.getBirthDate()));

            if (secondOccupant.getVeteran() != null) {
                target.setVeteranStatusName(secondOccupant.getVeteran());
                target.setVeteranStatusTitle(secondOccupant.getVeteran().getTitle());
            }

            if (secondOccupant.getAvatar() != null) {
                target.setAvatarId(secondOccupant.getAvatar().getId());
                target.setAvatarName(secondOccupant.getAvatar().getAvatarName());
            }

            return target;
        }

        return null;
    }
}
