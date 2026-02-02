package com.scnsoft.eldermark.service.password;

import com.scnsoft.eldermark.dao.password.EmployeePasswordSecurityDao;
import com.scnsoft.eldermark.dao.password.OrganizationPasswordSettingsDao;
import com.scnsoft.eldermark.dao.specification.OrganizationPasswordSettingSpecification;
import com.scnsoft.eldermark.dto.PasswordComplexityRules;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.password.EmployeePasswordSecurity;
import com.scnsoft.eldermark.entity.password.OrganizationPasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordHistory;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.EmployeeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
@Service
@Transactional
public class EmployeePasswordSecurityServiceImpl implements EmployeePasswordSecurityService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeePasswordSecurityServiceImpl.class);
    private static final Long MINUTE_IN_MILLIS = 60L * 1000L;
    private static final Long DAY_IN_MILLIS = MINUTE_IN_MILLIS * 60 * 24;

    private static final Set<EmployeeStatus> UPDATE_PASSWORD_AVAILABLE_EMPLOYEE_STATUSES =
        EnumSet.of(EmployeeStatus.ACTIVE, EmployeeStatus.PENDING, EmployeeStatus.CONFIRMED);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganizationPasswordSettingsDao organizationPasswordSettingsDao;

    @Autowired
    private EmployeePasswordSecurityDao employeePasswordSecurityDao;

    @Autowired
    private OrganizationPasswordSettingSpecification organizationPasswordSettingspecification;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordHistoryService passwordHistoryService;

    @Override
    public boolean isPasswordExpired(Employee employee) {
        OrganizationPasswordSettings maxPasswordAgeSetting = organizationPasswordSettingsDao
                .getFirstByOrganizationIdAndPasswordSettings_PasswordSettingsType(employee.getOrganizationId(),
                        PasswordSettingsType.PASSWORD_MAXIMUM_AGE_IN_DAYS);
        if (maxPasswordAgeSetting.getEnabled() && maxPasswordAgeSetting.getValue() > 0) {
            EmployeePasswordSecurity employeePasswordSecurity = employee.getEmployeePasswordSecurity();
            if (employeePasswordSecurity != null) {
                Instant changePasswordTime = employeePasswordSecurity.getChangePasswordTime();
                if (changePasswordTime != null) {
                    Long expiredTimeInMillis = changePasswordTime.toEpochMilli()
                            + (maxPasswordAgeSetting.getValue() * DAY_IN_MILLIS);
                    Long currentTime = System.currentTimeMillis();
                    return currentTime > expiredTimeInMillis;
                }
            }
        }
        return false;
    }

    @Override
    public void updatePasswordChangedTimeIfEnabled(Employee employee) {
        var byOrganizationIdAndType = organizationPasswordSettingspecification.byOrganizationIdAndType(
                employee.getOrganizationId(), PasswordSettingsType.PASSWORD_MAXIMUM_AGE_IN_DAYS);

        organizationPasswordSettingsDao.findOne(byOrganizationIdAndType)
                .filter(pwdMaxAge -> pwdMaxAge.getEnabled() && pwdMaxAge.getValue() > 0)
                .ifPresent(pwdMaxAge -> {
                    var employeePasswordSecurity = employee.getEmployeePasswordSecurity();
                    if (employeePasswordSecurity == null)
                        employeePasswordSecurity = new EmployeePasswordSecurity();
                    employeePasswordSecurity.setChangePasswordTime(Instant.now());
                    employeePasswordSecurity.setEmployee(employee);
                    employeePasswordSecurity.setFailedLogonsCount(0);
                    employeePasswordSecurity.setLocked(false);
                    employeePasswordSecurity.setLockedTime(null);
                    employeePasswordSecurityDao.save(employeePasswordSecurity);
                });
    }

    @Override
    public Long unlockEmployeeAccount(Employee employee) {
        EmployeePasswordSecurity employeePasswordSecurity = employee.getEmployeePasswordSecurity();
        if (employeePasswordSecurity == null) {
            employeePasswordSecurity = new EmployeePasswordSecurity();
            employeePasswordSecurity.setEmployee(employee);
            employee.setEmployeePasswordSecurity(employeePasswordSecurity);
        }
        employeePasswordSecurity.setLockedTime(null);
        employeePasswordSecurity.setFailedLogonsCount(0);
        employeePasswordSecurity.setLocked(false);
        return employeePasswordSecurityDao.save(employeePasswordSecurity).getId();
    }

    @Override
    public void validatePasswordRequirementsMet(Employee employee, String password) {
        if (!UPDATE_PASSWORD_AVAILABLE_EMPLOYEE_STATUSES.contains(employee.getStatus())) {
            throw new InternalServerException(InternalServerExceptionType.PASSWORD_UPDATE_INCORRECT_CONTACT_STATUS);
        }
        var companyId = employee.getOrganization().getSystemSetup().getLoginCompanyId();
        if (StringUtils.isAnyEmpty(companyId, employee.getLoginName(), password)) {
            throw new InternalServerException(InternalServerExceptionType.MISSING_REQUIRED_FIELDS);
        }
        if (!isComplexityValid(password, employee.getOrganizationId())) {
            throw new InternalServerException(InternalServerExceptionType.PASSWORD_COMPLEXITY_VALIDATION_FAILURE);
        }
        if (!isHistoryValid(password, employee)) {
            throw new InternalServerException(InternalServerExceptionType.PASSWORD_HISTORY_VALIDATION_FAILURE);
        }
    }

    private boolean isHistoryValid(CharSequence password, Employee employee) {
        boolean historyValid = true;
        OrganizationPasswordSettings pwdHistorySetting = organizationPasswordSettingsDao
                .getFirstByOrganizationIdAndPasswordSettings_PasswordSettingsType(employee.getOrganizationId(),
                        PasswordSettingsType.COMPLEXITY_PASSWORD_HISTORY_COUNT);
        if (pwdHistorySetting != null && pwdHistorySetting.getEnabled() && pwdHistorySetting.getValue() > 0) {
            List<PasswordHistory> passwordHistories = passwordHistoryService.findLatestByEmployeeId(employee.getId(), pwdHistorySetting.getValue().intValue());
            if (CollectionUtils.isNotEmpty(passwordHistories)) {
                for (PasswordHistory passwordHistory : passwordHistories) {
                    if (passwordEncoder.matches(password, passwordHistory.getPassword())) {
                        historyValid = false;
                        break;
                    }
                }
            }
        }
        return historyValid;
    }

    private boolean isComplexityValid(CharSequence password, Long databaseId) {
        var settings = organizationPasswordSettingsDao.findAllByOrganizationIdAndEnabled(databaseId, true);

        for (var setting : settings) {
            var type = setting.getPasswordSettings().getPasswordSettingsType();
            if (!type.hasRegexp()) {
                continue;
            }
            var regexp = type.getRegexp(setting.getValue()).orElseThrow();
            Pattern pattern = Pattern.compile(regexp);
            var isRuleMet = pattern.matcher(password).matches();
            if (!isRuleMet) {
                return false;
            }
        }
        return true;
    }

    @Override
    public PasswordComplexityRules getPasswordComplexityRules(Long databaseId) {
        PasswordComplexityRules passwordComplexityRules = new PasswordComplexityRules();
        List<OrganizationPasswordSettings> OrganizationPasswordSettings = organizationPasswordSettingsDao
                .findAllByOrganizationId(databaseId);
        for (OrganizationPasswordSettings databasePasswordSetting : OrganizationPasswordSettings) {
            switch (databasePasswordSetting.getPasswordSettings().getPasswordSettingsType()) {
                case COMPLEXITY_ARABIC_NUMERALS_COUNT: {
                    passwordComplexityRules.setArabicNumeralCount(
                            databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_LOWERCASE_COUNT: {
                    passwordComplexityRules.setLowerCaseCount(
                            databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_UPPERCASE_COUNT: {
                    passwordComplexityRules.setUpperCaseCount(
                            databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_NON_ALPHANUMERIC_COUNT: {
                    passwordComplexityRules.setNonAlphaNumeralCount(
                            databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_ALPHABETIC_COUNT: {
                    passwordComplexityRules.setAlphabeticCount(
                            databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_PASSWORD_LENGTH: {
                    passwordComplexityRules
                            .setLength(databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_LESS_SPACES_THAN: {
                    passwordComplexityRules.setSpacesLessThan(databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : null);
                }
                default:
                    break;
            }

        }
        return passwordComplexityRules;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateFailedCount(String username, String companyId) {
        try {
            Employee employee = employeeService.getEmployeeThatCanLogin(username, companyId);
            OrganizationPasswordSettings logonFaildCount = organizationPasswordSettingsDao
                    .getFirstByOrganizationIdAndPasswordSettings_PasswordSettingsType(employee.getOrganizationId(),
                            PasswordSettingsType.ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT);
            if (logonFaildCount.getEnabled() && logonFaildCount.getValue() > 0) {
                if (employee.getEmployeePasswordSecurity() == null) {
                    employee.setEmployeePasswordSecurity(new EmployeePasswordSecurity());
                    employee.getEmployeePasswordSecurity().setChangePasswordTime(Instant.now());
                }
                employee.getEmployeePasswordSecurity().setEmployee(employee);
                employee.getEmployeePasswordSecurity()
                        .setFailedLogonsCount(employee.getEmployeePasswordSecurity().getFailedLogonsCount() + 1);
                if (employee.getEmployeePasswordSecurity().getFailedLogonsCount() >= logonFaildCount.getValue()) {
                    employee.getEmployeePasswordSecurity().setLocked(true);
                    employee.getEmployeePasswordSecurity().setLockedTime(Instant.now());
                }
            }
        } catch (UsernameNotFoundException e) {
            logger.error("User was not found", e);
        }
    }

    @Override
    public Long getEmployeeUnlockTime(Employee employee) {
        Long unlockTime = null;
        OrganizationPasswordSettings accountLockedMaxAge = organizationPasswordSettingsDao
                .getFirstByOrganizationIdAndPasswordSettings_PasswordSettingsType(employee.getOrganizationId(),
                        PasswordSettingsType.ACCOUNT_LOCK_IN_MINUTES);
        if (accountLockedMaxAge.getEnabled() && accountLockedMaxAge.getValue() > 0) {
            EmployeePasswordSecurity employeePasswordSecurity = employee.getEmployeePasswordSecurity();
            if (employeePasswordSecurity != null) {
                Instant lockedTime = employeePasswordSecurity.getLockedTime();
                if (lockedTime != null) {
                    unlockTime = lockedTime.toEpochMilli()
                            + (accountLockedMaxAge.getValue() * MINUTE_IN_MILLIS);
                }
            }
        }
        return unlockTime;
    }

    @Override
    public long lockedMinutesLeft(String username, String companyId) {
        Employee employee = employeeService.getEmployeeThatCanLogin(username, companyId);
        OrganizationPasswordSettings accountLockedMaxAge = organizationPasswordSettingsDao
                .getFirstByOrganizationIdAndPasswordSettings_PasswordSettingsType(employee.getOrganizationId(),
                        PasswordSettingsType.ACCOUNT_LOCK_IN_MINUTES);

        return Optional.ofNullable(employee.getEmployeePasswordSecurity())
                .map(EmployeePasswordSecurity::getLockedTime)
                .map(lockedTime -> lockedTime.plus(accountLockedMaxAge.getValue(), ChronoUnit.MINUTES))
                .map(unlockTime -> Duration.between(Instant.now(), unlockTime).toMinutes())
                .orElse(0L);

    }

}
