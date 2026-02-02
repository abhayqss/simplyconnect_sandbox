package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Employee;

public interface EmployeeService {

    Employee getEmployeeByLoginAndDatabaseAlternativeId(String login, String alternativeId);

    Employee getConsanaAuthorEmployee();
}
