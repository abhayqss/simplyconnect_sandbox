package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDao extends JpaRepository<Employee, Long> {

    Employee getFirstByLoginAndDatabaseId(String login, Long databaseId);

    Employee getFirstByLoginAndDatabase_AlternativeId(String login, String alternativeId);

}
