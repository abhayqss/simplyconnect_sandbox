package com.scnsoft.eldermark.services.password;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.password.PasswordHistory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PasswordHistoryService {
    void clearPasswordHistory(Long databaseId);
    List<PasswordHistory> findAllByEmployeeId(Long employeeId);
    void enablePasswordHistory(Long databaseId);
    void addCurrentPasswordToHistoryIfEnabled(Employee employee);
}
