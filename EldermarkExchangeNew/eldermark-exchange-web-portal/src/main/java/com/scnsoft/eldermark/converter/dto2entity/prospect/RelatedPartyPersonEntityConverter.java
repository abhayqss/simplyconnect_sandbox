package com.scnsoft.eldermark.converter.dto2entity.prospect;

import com.scnsoft.eldermark.converter.PersonEntityConverter;
import com.scnsoft.eldermark.dto.NameDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.dto.prospect.RelatedPartyDto;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.service.ProspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.function.BiFunction;

@Component
@Transactional(readOnly = true)
public class RelatedPartyPersonEntityConverter extends PersonEntityConverter implements BiFunction<RelatedPartyDto, ProspectDto, Person> {

    @Autowired
    private ProspectService prospectService;

    @Override
    public Person apply(RelatedPartyDto relatedPartyDto, ProspectDto prospectDto) {
        Person target;
        if (prospectDto.getId() != null) {
            target = prospectService.findById(prospectDto.getId()).getRelatedPartyPerson();
        } else {
            target = createNewPerson(prospectDto.getOrganizationId());
        }

        var nameDto = new NameDto(relatedPartyDto.getFirstName(), relatedPartyDto.getLastName());

        var telecoms = new EnumMap<PersonTelecomCode, String>(PersonTelecomCode.class);
        telecoms.put(PersonTelecomCode.MC, relatedPartyDto.getCellPhone());
        telecoms.put(PersonTelecomCode.EMAIL, relatedPartyDto.getEmail());

        update(target, relatedPartyDto.getAddress(), nameDto, telecoms);

        return target;
    }
}
