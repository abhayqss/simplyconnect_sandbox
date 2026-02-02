package com.scnsoft.eldermark.services.palatiumcare.employee;

import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.palatiumcare.NotifyEmployeeDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyEmployeeMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.shared.palatiumcare.NotifyEmployeeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class NotifyEmployeeService extends BasicService<Employee, NotifyEmployeeDto> {

    private NotifyEmployeeDao employeeDao;

    private NotifyEmployeeMapper notifyEmployeeMapper = new NotifyEmployeeMapper();

    @Autowired
    public void setEmployeeDao(NotifyEmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }


    @Override
    protected GenericMapper<Employee, NotifyEmployeeDto> getMapper() {
        return notifyEmployeeMapper;
    }

    @Override
    protected CrudRepository<Employee, Long> getCrudRepository() {
        return employeeDao;
    }


}
