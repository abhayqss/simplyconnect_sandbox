package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.EmployeeDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.scnsoft.eldermark.consana.sync.server.constants.ConsanaSyncApiReceivePatientConstants.EMPLOYEE_LOGIN;
import static com.scnsoft.eldermark.consana.sync.server.constants.ConsanaSyncApiReceivePatientConstants.ORGANIZATION_ALTERNATIVE_ID;

@Component
@Transactional(noRollbackFor = Exception.class)
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public Employee getEmployeeByLoginAndDatabaseAlternativeId(String login, String alternativeId) {
        return employeeDao.getFirstByLoginAndDatabase_AlternativeId(login, alternativeId);
    }

    @Override
    public Employee getConsanaAuthorEmployee() {
        return getEmployeeByLoginAndDatabaseAlternativeId(EMPLOYEE_LOGIN, ORGANIZATION_ALTERNATIVE_ID);
    }
}
