package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.EmployeeRequestDao;
import com.scnsoft.eldermark.dao.specification.EmployeeRequestSpecificationGenerator;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeRequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeRequestServiceImpl implements EmployeeRequestService {

    @Autowired
    private EmployeeRequestDao employeeRequestDao;

    @Autowired
    private EmployeeRequestSpecificationGenerator employeeRequestSpecificationGenerator;

    @Override
    @Transactional
    public EmployeeRequest save(Employee employee, EmployeeRequestType type) {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setCreatedDateTime(Instant.now());
        employeeRequest.setTargetEmployee(employee);
        employeeRequest.setCreatedEmployee(employee);
        employeeRequest.setTokenType(type);
        employeeRequest.setToken(UUID.randomUUID().toString());
        return employeeRequestDao.save(employeeRequest);
    }

    @Override
    @Transactional
    public void delete(EmployeeRequest employeeRequest) {
        employeeRequestDao.delete(employeeRequest);
    }

    @Override
    @Transactional
    public void deleteById(Long employeeRequestId) {
        employeeRequestDao.deleteById(employeeRequestId);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeRequest findByToken(String token, EmployeeRequestType type) {
        if (type == null)
            return employeeRequestDao.findByToken(token);
        else
            return employeeRequestDao.findByTokenAndTokenType(token, type);
    }

    @Override
    @Transactional
    public <T> List<T> findRequestsCreatedBefore(Instant when, EmployeeRequestType type, Class<T> projection) {
        var createdBefore = employeeRequestSpecificationGenerator.createdBefore(when);
        var byTokenType = employeeRequestSpecificationGenerator.byTokenType(type);
        return employeeRequestDao.findAll(createdBefore.and(byTokenType), projection);
    }
}
