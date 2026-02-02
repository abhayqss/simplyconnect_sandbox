package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.LinkedEmployees;

import java.util.List;

public interface LinkedEmployeesDao extends BaseDao<LinkedEmployees> {
    List<Long> getLinkedEmployeeIds(Long employeeId);
    void deleteLinkedEmployee(Long linkedEmployeeIdToRemove, Long currentEmployeeId);
}
