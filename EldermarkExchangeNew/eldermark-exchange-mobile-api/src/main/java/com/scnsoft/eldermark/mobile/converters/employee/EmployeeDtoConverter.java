package com.scnsoft.eldermark.mobile.converters.employee;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeDto;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EmployeeDtoConverter extends BaseEmployeeDtoConverter implements Converter<Employee, EmployeeDto> {

    @Autowired
    private Converter<Address, AddressDto> addressDtoConverter;

    @Override
    public EmployeeDto convert(Employee source) {
        var requestedByEmployeeId = loggedUserService.getCurrentEmployeeId();
        var target = new EmployeeDto();

        fillIdNamesBirthDate(source, target);

        target.setOrganizationId(source.getOrganizationId());
        target.setOrganizationName(source.getOrganization().getName());

        target.setCommunityId(source.getCommunityId());
        target.setCommunityName(Optional.ofNullable(source.getCommunity()).map(Community::getName).orElse(null));

        target.setEmail(source.getLoginName());
        target.setRole(Optional.ofNullable(source.getCareTeamRole()).map(CareTeamRole::getName).orElse(null));
        target.setIsFavourite(CollectionUtils.emptyIfNull(source.getAddedAsFavouriteToEmployeeIds()).contains(requestedByEmployeeId));

        if (source.getAvatar() != null) {
            target.setAvatarId(source.getAvatar().getId());
            target.setAvatarName(source.getAvatar().getAvatarName());
        }

        fillConversationsData(source.getId(), source.getTwilioUserSid(), target);

        Optional.ofNullable(source.getPerson())
                .map(Person::getAddresses)
                .stream()
                .flatMap(List::stream)
                .findFirst()
                .map(addressDtoConverter::convert)
                .ifPresent(target::setAddress);

        PersonTelecomUtils.find(source.getPerson(), PersonTelecomCode.MC)
                .map(PersonTelecom::getNormalized)
                .ifPresent(target::setCellPhone);

        PersonTelecomUtils.find(source.getPerson(), PersonTelecomCode.HP)
                .map(PersonTelecom::getNormalized)
                .ifPresent(target::setHomePhone);

        return target;
    }
}
