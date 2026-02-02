package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeRequestType;

import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 24-Sep-15.
 */
public interface EmployeeRequestDao extends BaseDao<EmployeeRequest> {
    EmployeeRequest getByToken(String token, EmployeeRequestType type);

    EmployeeRequest getByTargetEmployee(Employee employee, EmployeeRequestType type);

//    EmployeeRequest getByTargetEmployee(Long employeeId, EmployeeRequestType type);

    boolean existsByTargetEmployee(Employee employee, EmployeeRequestType type);

    List<EmployeeRequest> getExpiredRequests(Date expirationDate, final EmployeeRequestType type);

    int deleteByEmployee(Employee employee);

}
