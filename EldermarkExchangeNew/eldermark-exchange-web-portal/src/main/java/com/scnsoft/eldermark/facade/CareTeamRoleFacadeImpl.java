package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.RoleDto;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CareTeamRoleFacadeImpl implements CareTeamRoleFacade {

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private Converter<CareTeamRole, RoleDto> careTeamRoleDtoConverter;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> findEditableSystemRoles() {
        //todo when linked employees are implemented in phase 2, this method should also accept organization id because
        //different linked account can have different role in requested organization and therefore matrix will be different
        var loggedEmployee = loggedUserService.getCurrentEmployee();

        return careTeamRoleService.findContactEditableRoles(loggedEmployee.getCareTeamRole())
                .stream()
                .map(careTeamRoleDtoConverter::convert)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> findEditableClientCareTeamMemberRoles(Long employeeId) {
        //todo when linked employees are implemented in phase 2, this method should also accept organization id because
        //different linked account can have different roles in requested organization and therefore matrix will be different
        var loggedEmployee = loggedUserService.getCurrentEmployee();

        var editableClientCareTeamMemberRolesStream = careTeamRoleService.findClientCareTeamMemberEditableRoles(loggedEmployee.getCareTeamRole()).stream();

        if (employeeId != null) {
            var allowedCtmRoles = careTeamRoleService.findAllowedCtmRolesForEmployee(employeeId);
            editableClientCareTeamMemberRolesStream = editableClientCareTeamMemberRolesStream
                .filter(role -> allowedCtmRoles.stream().anyMatch(allowedRole -> Objects.equals(role.getId(), allowedRole.getId())));
        }

        return editableClientCareTeamMemberRolesStream
            .map(careTeamRoleDtoConverter::convert)
            .collect(Collectors.toList());
    }

    @Override
    public List<RoleDto> findEditableCommunityCareTeamMemberRoles() {
        //todo when linked employees are implemented in phase 2, this method should also accept organization id because
        //different linked account can have different roles in requested organization and therefore matrix will be different
        var loggedEmployee = loggedUserService.getCurrentEmployee();

        return careTeamRoleService.findCommunityCareTeamMemberEditableRoles(loggedEmployee.getCareTeamRole())
                .stream()
                .map(careTeamRoleDtoConverter::convert)
                .collect(Collectors.toList());
    }
}
