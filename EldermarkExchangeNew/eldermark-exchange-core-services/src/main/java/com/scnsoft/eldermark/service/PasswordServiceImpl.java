package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dto.ResetPasswordMailDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeRequestType;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.exception.AuthException;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.service.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.service.password.PasswordHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.scnsoft.eldermark.service.CareCoordinationConstants.EXTERNAL_COMPANY_ID;

@SuppressWarnings("deprecation")
@Service
@Transactional
public class PasswordServiceImpl implements PasswordService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);

    @Value("${reset.password.url}")
    private String resetPasswordUrl;

    @Value("${reset.password.url.external}")
    private String resetPasswordUrlExternal;

    @Value("${reset.password.request.url}")
    private String resetPasswordRequestUrl;

    @Value("${portal.url}")
    private String portalUrl;

    private long passwordRequestExpiresIn;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private EmployeeRequestService employeeRequestService;

    @Autowired
    private PasswordHistoryService passwordHistoryService;

    @Autowired
    private EmployeePasswordSecurityService employeePasswordSecurityService;

    @Autowired
    private ExchangeMailService exchangeMailService;

    // private static final PasswordEncoder passwordEncoder = new
    // SCryptPasswordEncoder();

    // TODO according to changes in Spring Security 5, passwords should be reencoded
    private static final PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    @Value("${reset.password.expiration.time.ms:0}")
    public void setMsInvitationExpiration(long msInvitationExpiration) {
        if (msInvitationExpiration < 0) {
            throw new IllegalArgumentException("Invalid \"reset.password.expiration.time.ms\" property: expected a positive number or 0, but got " +
                    msInvitationExpiration + ".");
        }
        this.passwordRequestExpiresIn = msInvitationExpiration;
    }

    @Override
    @Transactional
    public void requestPasswordReset(String loginName, String loginCompanyId) {
        List<Employee> employees = employeeDao.findByLoginNameAndOrganization_SystemSetup_LoginCompanyIdAndStatusIn(
                loginName, loginCompanyId, EmployeeStatus.allCanLogin());
        if (employees.isEmpty()) {
            logger.info("Received reset password request, but no active employees were found, login={}, companyId={}",
                    loginName, loginCompanyId);
        }
        employees.forEach(employee -> {
            logger.info("Received reset password request for employee id={}", employee.getId());
            var employeeRequest = employeeRequestService.save(employee, EmployeeRequestType.RESET_PASSWORD);
            ResetPasswordMailDto resetPasswordMailDto = createResetPasswordMailDto(employeeRequest);
            exchangeMailService.sendResetPassword(resetPasswordMailDto);
        });
    }

    @Override
    public void createPassword(Employee employee, String password) {
        updateEmployeePassword(employee, password);
    }

    private ResetPasswordMailDto createResetPasswordMailDto(EmployeeRequest employeeRequest) {
        final ResetPasswordMailDto result = new ResetPasswordMailDto();
        //todo - consider using email person telecom instead
        result.setToEmail(employeeRequest.getTargetEmployee().getLoginName());
        result.setUrl(generateUrl(
                employeeRequest.getTargetEmployee(),
                employeeRequest.getToken()
        ));

        return result;
    }

    private String generateUrl(Employee employee, String token) {
        var loginCompanyId = employee.getOrganization().getSystemSetup().getLoginCompanyId();
        var baseUrl = loginCompanyId.equals(EXTERNAL_COMPANY_ID) ?
                resetPasswordUrlExternal :
                resetPasswordUrl;

        return baseUrl + token +
                "&organizationId=" + employee.getOrganizationId() +
                "&email=" + employee.getLoginName() +
                "&companyId=" + loginCompanyId;
    }

    @Override
    public void resetPassword(String token, String password) {
        validateResetPasswordToken(token);

        var employeeRequest = employeeRequestService.findByToken(token, EmployeeRequestType.RESET_PASSWORD);
        var targetEmployee = employeeRequest.getTargetEmployee();

        updateEmployeePassword(targetEmployee, password);
        employeeRequestService.delete(employeeRequest);
    }

    @Override
    @Transactional
    public void changePassword(Employee employee, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new AuthException(InternalServerExceptionType.AUTH_BAD_CREDENTIALS, "Old password is incorrect");
        }

        updateEmployeePassword(employee, newPassword);
    }

    private void updateEmployeePassword(Employee employee, String newPassword) {
        employeePasswordSecurityService.validatePasswordRequirementsMet(employee, newPassword);

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setModifiedTimestamp(Instant.now().toEpochMilli());

        employeePasswordSecurityService.unlockEmployeeAccount(employee);
        passwordHistoryService.addCurrentPasswordToHistoryIfEnabled(employee);
        employeePasswordSecurityService.updatePasswordChangedTimeIfEnabled(employee);
        employeeDao.save(employee);
    }

    @Override
    public void validateResetPasswordToken(String token) {
        final EmployeeRequest employeeRequest = employeeRequestService.findByToken(token, EmployeeRequestType.RESET_PASSWORD);

        if (employeeRequest == null || employeeRequest.getCreatedDateTime().isBefore(getExpiredRequestWasCreatedBeforeDate())) {
            throw new InternalServerException(InternalServerExceptionType.AUTH_RESET_REQUEST_INVALID);
        }
    }

    @Override
    @Transactional
    public void clearResetPasswordRequests() {
        var expiredRequestWasCreatedBeforeDate = getExpiredRequestWasCreatedBeforeDate();
        final List<IdAware> requests = employeeRequestService.findRequestsCreatedBefore(expiredRequestWasCreatedBeforeDate,
                EmployeeRequestType.RESET_PASSWORD, IdAware.class);
        for (IdAware employeeRequest : requests) {
            clearResetPasswordRequest(employeeRequest);
        }
    }

    private void clearResetPasswordRequest(IdAware employeeRequest) {
        employeeRequestService.deleteById(employeeRequest.getId());
    }

    private Instant getExpiredRequestWasCreatedBeforeDate() {
        return Instant.now().minus(passwordRequestExpiresIn, ChronoUnit.MILLIS);
    }
}
