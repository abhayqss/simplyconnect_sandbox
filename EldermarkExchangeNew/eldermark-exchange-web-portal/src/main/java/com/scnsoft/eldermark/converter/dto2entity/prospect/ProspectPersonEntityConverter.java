package com.scnsoft.eldermark.converter.dto2entity.prospect;

import com.scnsoft.eldermark.converter.PersonEntityConverter;
import com.scnsoft.eldermark.dto.NameDto;
import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.service.ProspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;

@Component
@Transactional(readOnly = true)
public class ProspectPersonEntityConverter extends PersonEntityConverter implements Converter<ProspectDto, Person> {

    @Autowired
    private ProspectService prospectService;

    @Override
    public Person convert(ProspectDto source) {
        Person target;
        if (source.getId() != null) {
            target = prospectService.findById(source.getId()).getPerson();
        } else {
            target = createNewPerson(source.getOrganizationId());
        }

        var nameDto = new NameDto(source.getFirstName(), source.getLastName());

        var telecoms = new EnumMap<PersonTelecomCode, String>(PersonTelecomCode.class);
        telecoms.put(PersonTelecomCode.MC, source.getCellPhone());
        telecoms.put(PersonTelecomCode.EMAIL, source.getEmail());

        update(target, source.getAddress(), nameDto, telecoms);

        return target;
    }
}