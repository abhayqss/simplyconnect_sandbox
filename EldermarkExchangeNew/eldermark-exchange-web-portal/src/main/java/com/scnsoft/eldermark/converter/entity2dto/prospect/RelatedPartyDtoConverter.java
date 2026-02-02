package com.scnsoft.eldermark.converter.entity2dto.prospect;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.prospect.RelatedPartyDto;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class RelatedPartyDtoConverter implements Converter<Prospect, RelatedPartyDto> {

    @Autowired
    private Converter<Address, AddressDto> clientAddressDtoConverter;

    @Override
    public RelatedPartyDto convert(Prospect prospect) {
        var target = new RelatedPartyDto();
        target.setFirstName(prospect.getRelatedPartyFirstName());
        target.setLastName(prospect.getRelatedPartyLastName());
        target.setFullName(CareCoordinationUtils.getFullName(
                prospect.getRelatedPartyFirstName(),
                prospect.getRelatedPartyLastName()
        ));
        var relatedPartyPerson = prospect.getRelatedPartyPerson();
        if (relatedPartyPerson != null) {
            for (PersonTelecom telecom : relatedPartyPerson.getTelecoms()) {
                if (PersonTelecomCode.EMAIL.name().equalsIgnoreCase(telecom.getUseCode())) {
                    target.setEmail(telecom.getValue());
                }
                if (PersonTelecomCode.MC.name().equalsIgnoreCase(telecom.getUseCode())) {
                    target.setCellPhone(telecom.getValue());
                }
            }

            if (CollectionUtils.isNotEmpty(relatedPartyPerson.getAddresses())) {
                target.setAddress(clientAddressDtoConverter.convert(relatedPartyPerson.getAddresses().get(0)));
            }
        }
        var relatedPartyRelationship = prospect.getRelatedPartyRelationship();
        if (relatedPartyRelationship != null) {
            target.setRelationshipTypeName(relatedPartyRelationship);
            target.setRelationshipTypeTitle(relatedPartyRelationship.getTitle());
        }

        return target;
    }
}
