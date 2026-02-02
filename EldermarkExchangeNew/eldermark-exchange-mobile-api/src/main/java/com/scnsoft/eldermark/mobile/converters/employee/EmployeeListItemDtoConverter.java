package com.scnsoft.eldermark.mobile.converters.employee;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeDto;
import com.scnsoft.eldermark.projection.EmployeeIdNameFavouriteOrgDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiFunction;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EmployeeListItemDtoConverter extends BaseEmployeeDtoConverter implements BiFunction<EmployeeIdNameFavouriteOrgDetails, PermissionFilter, EmployeeDto> {

    @Override
    public EmployeeDto apply(EmployeeIdNameFavouriteOrgDetails source, PermissionFilter permissionFilter) {
        var target = new EmployeeDto();

        fillIdNamesBirthDate(source, target);

        target.setOrganizationId(source.getOrganizationId());
        target.setOrganizationName(source.getOrganizationName());
        target.setCommunityId(source.getCommunityId());
        target.setCommunityName(source.getCommunityName());

        target.setRole(source.getCareTeamRoleName());
        target.setAvatarId(source.getAvatarId());
        target.setAvatarName(source.getAvatarAvatarName());
        target.setIsFavourite(source.getIsFavourite());
        target.setEmail(source.getLoginName());

        fillConversationsData(source.getId(), source.getTwilioUserSid(), target, permissionFilter);

        return target;
    }
}
