package com.scnsoft.eldermark.service.password;

import java.util.List;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.password.PasswordHistory;

public interface PasswordHistoryService {

    //TODO method will be used as soon as password settings management will be implemented
    void enablePasswordHistory(Long databaseId);
    
    void addCurrentPasswordToHistoryIfEnabled(Employee employee);
        
    List<PasswordHistory> findAllByEmployeeId(Long employeeId);

    List<PasswordHistory> findLatestByEmployeeId(Long employeeId, int count);
}
