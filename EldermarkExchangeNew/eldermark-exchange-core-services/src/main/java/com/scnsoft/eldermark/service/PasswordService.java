package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Employee;

public interface PasswordService {

    void requestPasswordReset(String login, String companyCode);

    void createPassword(Employee employee, String password);

    void resetPassword(String token, String password);

    void changePassword(Employee employee, String oldPassword, String newPassword);
    
    void validateResetPasswordToken(String token);

    void clearResetPasswordRequests();
}
