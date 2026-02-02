package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeRequestType;

import java.time.Instant;
import java.util.List;

public interface EmployeeRequestService {

    EmployeeRequest save(Employee employee, EmployeeRequestType type);
    
    EmployeeRequest findByToken(String token, EmployeeRequestType type);

    void delete(EmployeeRequest employeeRequest);

    void deleteById(Long employeeRequestId);

    <T> List<T> findRequestsCreatedBefore(Instant when, EmployeeRequestType type, Class<T> projection);
}
