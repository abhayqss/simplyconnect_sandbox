package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeRequestType;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRequestDao extends AppJpaRepository<EmployeeRequest, Long> {

    EmployeeRequest findByTokenAndTokenType(String token, EmployeeRequestType type);

    EmployeeRequest findByToken(String token);

    void deleteAllByTargetEmployeeAndTokenType(Employee targetEmployee, EmployeeRequestType tokenType);
}
