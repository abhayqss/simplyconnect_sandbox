package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.shared.EmployeeDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeFacadeImpl implements EmployeeFacade {

    @Override
    public EmployeeDto getLoggedInEmployee() {
        ExchangeUserDetails token = SecurityUtils.getAuthenticatedUser();

        EmployeeDto employee = new EmployeeDto();

        employee.setLogin(token.getEmployeeLogin());
        employee.setAlternativeDatabaseId(token.getAlternativeDatabaseId());

        employee.setId(token.getEmployeeId());

        employee.setFirstName(token.getEmployeeFirstName());
        employee.setLastName(token.getEmployeeLastName());

        employee.setEldermarkUser(SecurityUtils.isEldermarkUser());
        employee.setManager(SecurityUtils.isManager());
        employee.setDirectManager(SecurityUtils.isDirectManager());

        employee.setDatabaseId(token.getCurrentDatabaseId());

        employee.setLogoUrl(token.getLogoPaths().getFirst());
        employee.setAlternativeLogoUrl(token.getLogoPaths().getSecond());

        if(token.getEmployee().getCareTeamRole() != null) {
            employee.setRoleDisplayName(token.getEmployee().getCareTeamRole().getDisplayName());
        }

        return employee;
    }
}
