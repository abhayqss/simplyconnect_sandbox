package com.scnsoft.eldermark.services.password;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.shared.password.PasswordComplexityVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
public interface EmployeePasswordSecurityService {
    void processSuccessfulLogin(Employee employee);
    void processFailedLogin(Employee employee);
    Boolean isPasswordExpired(Employee employee);
    void resetPasswordChangedTime(Long databaseId);
    void setPasswordChangedTime(Long databaseId, Date changePasswordTime);
    Boolean isPasswordValid(String companyId, String username, String password);
    void updatePasswordChangedTimeIfEnabled(Employee employee);
    Boolean isComplexityValid(CharSequence password, Long databaseId);
    String convertToPattern(PasswordComplexityVO passwordComplexityVO);
    PasswordComplexityVO getPasswordComplexity(Long databaseId);
    Boolean isHistoryValid(CharSequence password, Employee employee);
    void unlockEmployeeAccount(Long employeeId);
    void checkAccountIsNotLockedOrThrow(Employee employee);
}
