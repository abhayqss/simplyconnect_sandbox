package com.scnsoft.eldermark.mapper.palatiumcare;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.shared.palatiumcare.NotifyEmployeeDto;
import org.modelmapper.convention.MatchingStrategies;

public class NotifyEmployeeMapper  extends GenericMapper<Employee, NotifyEmployeeDto> {

    {
        getModelMapper().getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    protected Class<Employee> getEntityClass() {
        return Employee.class;
    }

    @Override
    protected Class<NotifyEmployeeDto> getDtoClass() {
        return NotifyEmployeeDto.class;
    }
}