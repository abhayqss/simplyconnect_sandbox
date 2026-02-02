package com.scnsoft.eldermark.converter.dto2entity.prospect;

import com.scnsoft.eldermark.converter.PersonEntityConverter;
import com.scnsoft.eldermark.dto.NameDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.prospect.SecondOccupant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.function.BiFunction;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class SecondOccupantPersonEntityConverter extends PersonEntityConverter implements BiFunction<SecondOccupant, ProspectDto, Person> {

    @Override
    public Person apply(SecondOccupant secondOccupant, ProspectDto prospectDto) {
        Person target;
        if (secondOccupant.getPerson() != null) {
            target = secondOccupant.getPerson();
        } else {
            target = createNewPerson(prospectDto.getOrganizationId());
        }
        var nameDto = new NameDto(prospectDto.getFirstName(), prospectDto.getLastName());

        var telecoms = new EnumMap<PersonTelecomCode, String>(PersonTelecomCode.class);
        telecoms.put(PersonTelecomCode.MC, prospectDto.getCellPhone());
        telecoms.put(PersonTelecomCode.EMAIL, prospectDto.getEmail());

        update(target, prospectDto.getAddress(), nameDto, telecoms);

        return target;
    }
}