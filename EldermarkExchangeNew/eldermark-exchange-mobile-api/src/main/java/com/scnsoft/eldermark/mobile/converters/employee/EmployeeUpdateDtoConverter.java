package com.scnsoft.eldermark.mobile.converters.employee;

import com.scnsoft.eldermark.dto.employee.EmployeeUpdates;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeUpdateRequestDto;
import com.scnsoft.eldermark.service.ContactService;
import com.scnsoft.eldermark.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EmployeeUpdateDtoConverter implements Converter<EmployeeUpdateRequestDto, EmployeeUpdates> {

    @Autowired
    private ContactService contactService;

    @Autowired
    private StateService stateService;

    @Override
    public EmployeeUpdates convert(EmployeeUpdateRequestDto source) {

        var target = new EmployeeUpdates(contactService.findById(source.getId()));

        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setBirthDate(source.getBirthDate());
        target.setAvatarData(source.getAvatarData());
        target.setAvatarMimeType(source.getAvatarMimeType());
        target.setShouldDeleteAvatar(source.isShouldRemoveAvatar());
        target.setStreet(source.getStreet());
        target.setCity(source.getCity());
        target.setState(stateService.findById(source.getStateId()).orElseThrow());
        target.setZipCode(source.getZipCode());
        target.setCellPhone(source.getCellPhone());
        target.setHomePhone(source.getHomePhone());

        return target;
    }
}
