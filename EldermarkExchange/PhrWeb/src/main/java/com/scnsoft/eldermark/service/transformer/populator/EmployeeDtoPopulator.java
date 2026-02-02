package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.shared.carecoordination.EmployeeDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDtoPopulator implements Populator<Employee, EmployeeDto> {

    @Override
    public void populate(Employee src, EmployeeDto target) {
        if (src != null && target != null) {
            target.setFirstName(src.getFirstName());
            target.setMiddleName(src.getMiddleName());
            target.setLastName(src.getLastName());
            target.setRoleId(src.getCareTeamRole().getId());
            target.setRole(src.getCareTeamRole().getName());
        }
    }
}
