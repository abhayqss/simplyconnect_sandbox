package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotifyEmployeeDao extends CrudRepository<Employee, Long> {}
