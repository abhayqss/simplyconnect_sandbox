package com.scnsoft.eldermark.service.password;

import com.scnsoft.eldermark.dto.PasswordComplexityRules;
import com.scnsoft.eldermark.entity.Employee;

public interface EmployeePasswordSecurityService {

    boolean isPasswordExpired(Employee employee);

    void updatePasswordChangedTimeIfEnabled(Employee employee);

    Long unlockEmployeeAccount(Employee employee);

    void validatePasswordRequirementsMet(Employee employee, String password);

    void updateFailedCount(String username, String companyId);

    PasswordComplexityRules getPasswordComplexityRules(Long organizationId);

    Long getEmployeeUnlockTime(Employee employee);

    long lockedMinutesLeft(String username, String companyId);
}
