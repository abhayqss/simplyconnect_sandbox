package com.scnsoft.eldermark.services.password;

import com.scnsoft.eldermark.dao.password.DatabasePasswordSettingsDao;
import com.scnsoft.eldermark.dao.password.EmployeePasswordSecurityDao;
import com.scnsoft.eldermark.dao.password.PasswordHistoryDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.entity.password.EmployeePasswordSecurity;
import com.scnsoft.eldermark.entity.password.PasswordHistory;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.exceptions.TimedLockedException;
import com.scnsoft.eldermark.shared.password.PasswordComplexityVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Provider;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class EmployeePasswordSecurityServiceImpl implements  EmployeePasswordSecurityService {

    private static final StandardPasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    private static final Long MINUTE_IN_MILLIS = 60L * 1000L;

    private static final Long DAY_IN_MILLIS = MINUTE_IN_MILLIS * 60 * 24;

    private static final String PASSWORD_COMPLEXITY_PATTERN_TEMPLATE = "^(?=(.*\\d){%s,})(?=(.*[a-z]){%s,})(?=(.*[A-Z]){%s,})(?=(.*[!\"#$%%&'()*+,\\-.\\/:;<=>?@\\[\\\\\\]^_`{|}~]){%s,})(?=(.*[a-zA-Z]){%s,})[0-9a-zA-Z!\"#$%%&'()*+,\\-.\\/:;<=>?@\\[\\\\\\]^_`{|}~]{%s,}";

    @Autowired
    EmployeePasswordSecurityDao employeePasswordSecurityDao;

    @Autowired
    DatabasePasswordSettingsDao databasePasswordSettingsDao;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordHistoryDao passwordHistoryDao;

    @Resource
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private Provider<ResetLoginCounterRunnable> resetLoginCounterRunnableProvider;

    @Autowired
    private Provider<UnlockAccountRunnable> unlockAccountRunnableProvider;

    @Override
    public void processSuccessfulLogin(Employee employee) {
        EmployeePasswordSecurity employeePasswordSecurity = employeePasswordSecurityDao.findEmployeePasswordSecurityByEmployee_Id(employee.getId());
        if (employeePasswordSecurity == null) {
            employeePasswordSecurity = createEmployeePasswordSecurity(employee);
            employeePasswordSecurityDao.save(employeePasswordSecurity);
            DatabasePasswordSettings maxPasswordAgeSetting = databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.PASSWORD_MAXIMUM_AGE_IN_DAYS);
            if (maxPasswordAgeSetting.getEnabled() && maxPasswordAgeSetting.getValue() > 0) {
                employeePasswordSecurityDao.updatePasswordChangedDate(employee.getId(), new Date());
            }
        }

        employeePasswordSecurityDao.flush();
        if (employeePasswordSecurity.getFailedLogonsCount() > 0) {
            employeePasswordSecurityDao.resetFailAttempts(employee.getId());
        }
    }

    @Override
    public void processFailedLogin(Employee employee) {
        DatabasePasswordSettings failedLogonCountSetting = databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT);
        EmployeePasswordSecurity employeePasswordSecurity = employeePasswordSecurityDao.findEmployeePasswordSecurityByEmployee_Id(employee.getId());
        if (employeePasswordSecurity == null) {
            employeePasswordSecurity = createEmployeePasswordSecurity(employee);
            employeePasswordSecurity = employeePasswordSecurityDao.save(employeePasswordSecurity);
            DatabasePasswordSettings maxPasswordAgeSetting = databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.PASSWORD_MAXIMUM_AGE_IN_DAYS);
            if (maxPasswordAgeSetting.getEnabled() && maxPasswordAgeSetting.getValue() > 0) {
                employeePasswordSecurityDao.updatePasswordChangedDate(employee.getId(), new Date());
            }
        }
        Integer failedLogonsCountAfterAttempt = employeePasswordSecurity.getFailedLogonsCount();
        if (failedLogonCountSetting.getEnabled() && failedLogonCountSetting.getValue() > 0) {
            employeePasswordSecurityDao.increaseFailAttemptsCounter(employee.getId());
            failedLogonsCountAfterAttempt++;
        }
        if (failedLogonsCountAfterAttempt == 1) {
            DatabasePasswordSettings resetFailedLogonSetting = databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.ACCOUNT_RESET_FAILED_LOGON_COUNT_IN_MINUTES);
            //Default reset time - 1 day
            Long runTime = System.currentTimeMillis() + DAY_IN_MILLIS;
            if (resetFailedLogonSetting.getEnabled() && resetFailedLogonSetting.getValue() > 0) {
                runTime = System.currentTimeMillis() + (resetFailedLogonSetting.getValue() * MINUTE_IN_MILLIS);
            }
            final EmployeeAwareRunnable runnable = resetLoginCounterRunnableProvider.get();
            runnable.setEmployeeId(employee.getId());
            threadPoolTaskScheduler.schedule(runnable, new Date(runTime));
        }
        if (failedLogonCountSetting.getEnabled() && failedLogonCountSetting.getValue() > 0 && failedLogonsCountAfterAttempt >= failedLogonCountSetting.getValue()) {
            DatabasePasswordSettings lockAccountDurationSetting = databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.ACCOUNT_LOCK_IN_MINUTES);
            //Default lock time - 60 min
            Long lockTime = 60l;
            if (lockAccountDurationSetting.getEnabled() && lockAccountDurationSetting.getValue() > 0) {
                lockTime = lockAccountDurationSetting.getValue();
            }
            if (!employeePasswordSecurity.getLocked()) {
                Date lockDate = new Date();
                employeePasswordSecurityDao.lockEmployeeAccount(employee.getId(), lockDate);
                Long unlockTime = lockDate.getTime() + (lockTime * MINUTE_IN_MILLIS);
                final EmployeeAwareRunnable runnable = unlockAccountRunnableProvider.get();
                runnable.setEmployeeId(employee.getId());
                threadPoolTaskScheduler.schedule(runnable, new Date(unlockTime));
            }
        }
        employeePasswordSecurityDao.flush();
    }

    @Override
    public void checkAccountIsNotLockedOrThrow(Employee employee) {
        EmployeePasswordSecurity employeePasswordSecurity = employeePasswordSecurityDao.findEmployeePasswordSecurityByEmployee_Id(employee.getId());
        if (employeePasswordSecurity != null && employeePasswordSecurity.getLocked()) {
            DatabasePasswordSettings lockAccountDurationSetting = databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.ACCOUNT_LOCK_IN_MINUTES);
            Date lockedDate = employeePasswordSecurity.getLockedTime();
            final long now = System.currentTimeMillis();
            // 60min - default lock duration
            Long lockDuration = (lockAccountDurationSetting.getEnabled() && lockAccountDurationSetting.getValue() > 0) ? lockAccountDurationSetting.getValue() : 60l;
            Long minutesLeft = lockDuration - ((now - lockedDate.getTime())/MINUTE_IN_MILLIS);
            Long millisecondsLeft = lockDuration * MINUTE_IN_MILLIS - ((now - lockedDate.getTime()));
            if (millisecondsLeft > 0) {
                // this account is locked out -> throw exception to make brute force attack pointless
                throw new TimedLockedException("Your account has been locked out because you have reached the maximum number of invalid login attempts. Please try again in " + minutesLeft + " minutes.", millisecondsLeft);
            } else {
                // locking time has gone, but account remains locked
                // (it may happen, for example, if UnlockAccountRunnable failed to execute or locking time has been decreased via Password Settings control panel while this account was locked out)
                // -> unlock account
                employeePasswordSecurityDao.unlockEmployeeAccount(employee.getId());
                employeePasswordSecurityDao.flush();
            }
        }
    }

    @Override
    public Boolean isPasswordExpired(Employee employee) {
        Boolean expired = Boolean.FALSE;
        DatabasePasswordSettings maxPasswordAgeSetting = databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.PASSWORD_MAXIMUM_AGE_IN_DAYS);
        if (maxPasswordAgeSetting.getEnabled() && maxPasswordAgeSetting.getValue() > 0) {
            EmployeePasswordSecurity employeePasswordSecurity = employeePasswordSecurityDao.findEmployeePasswordSecurityByEmployee_Id(employee.getId());
            if (employeePasswordSecurity != null) {
                Date changePasswordTime = employeePasswordSecurity.getChangePasswordTime();
                if (changePasswordTime != null) {
                    Long expiredTimeInMillis = changePasswordTime.getTime() + (maxPasswordAgeSetting.getValue() * DAY_IN_MILLIS);
                    Long currentTime = System.currentTimeMillis();
                    expired = currentTime > expiredTimeInMillis;
                }
            }
        }
        return expired;
    }

    @Override
    public void resetPasswordChangedTime(Long databaseId) {
        employeePasswordSecurityDao.resetPasswordChangedTime(databaseId);
    }

    @Override
    public void setPasswordChangedTime(Long databaseId, Date changePasswordTime) {
        employeePasswordSecurityDao.setPasswordChangedTime(databaseId, changePasswordTime);
    }

    @Override
    public Boolean isPasswordValid(String companyId, String username, String password) {
        if (StringUtils.isAnyEmpty(companyId, username, password)) {
            return Boolean.FALSE;
        }
        Boolean result = Boolean.FALSE;
        Employee employee = employeeService.getActiveEmployee(username, companyId);
        if (employee != null) {
            boolean historyValid = isHistoryValid(password, employee);
            boolean complexityValid = isComplexityValid(password, employee.getDatabaseId());
            result = historyValid && complexityValid;
        }
        return result;
    }

    @Override
    public void updatePasswordChangedTimeIfEnabled(Employee employee) {
        DatabasePasswordSettings pwdMaxAge =  databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.PASSWORD_MAXIMUM_AGE_IN_DAYS);
        if(pwdMaxAge != null && pwdMaxAge.getEnabled() && pwdMaxAge.getValue() > 0) {
            employeePasswordSecurityDao.updatePasswordChangedDate(employee.getId(), new Date());
        }
    }

    @Override
    public Boolean isComplexityValid(CharSequence password, Long databaseId) {
        boolean complexityValid;
        PasswordComplexityVO passwordComplexityVO = getPasswordComplexity(databaseId);
        String passwordPattern = convertToPattern(passwordComplexityVO);
        Pattern pattern = Pattern.compile(passwordPattern);
        complexityValid = pattern.matcher(password).matches();
        return complexityValid;
    }

    @Override
    public String convertToPattern(PasswordComplexityVO passwordComplexity) {
        return String.format(PASSWORD_COMPLEXITY_PATTERN_TEMPLATE,
                passwordComplexity.getArabicNumeralsCount(),
                passwordComplexity.getLowercaseCount(),
                passwordComplexity.getUppercaseCount(),
                passwordComplexity.getSpecialCharsCount(),
                passwordComplexity.getAlphabeticCount(),
                passwordComplexity.getPasswordLength());
    }

    @Override
    public PasswordComplexityVO getPasswordComplexity(Long databaseId) {
        PasswordComplexityVO passwordComplexityVO = new PasswordComplexityVO();
        List<DatabasePasswordSettings> databasePasswordSettings = databasePasswordSettingsDao.getOrganizationPasswordSettings(databaseId);
        for (DatabasePasswordSettings databasePasswordSetting : databasePasswordSettings) {
            switch (databasePasswordSetting.getPasswordSettings().getPasswordSettingsType()) {
                case COMPLEXITY_ARABIC_NUMERALS_COUNT: {
                    passwordComplexityVO.setArabicNumeralsCount(databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_LOWERCASE_COUNT: {
                    passwordComplexityVO.setLowercaseCount(databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_UPPERCASE_COUNT: {
                    passwordComplexityVO.setUppercaseCount(databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_NON_ALPHANUMERIC_COUNT: {
                    passwordComplexityVO.setSpecialCharsCount(databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_ALPHABETIC_COUNT: {
                    passwordComplexityVO.setAlphabeticCount(databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
                case COMPLEXITY_PASSWORD_LENGTH: {
                    passwordComplexityVO.setPasswordLength(databasePasswordSetting.getEnabled() ? databasePasswordSetting.getValue() : 0L);
                    break;
                }
            }

        }
        return passwordComplexityVO;
    }

    @Override
    public Boolean isHistoryValid(CharSequence password, Employee employee) {
        boolean historyValid = true;
        DatabasePasswordSettings pwdHistorySetting = databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.COMPLEXITY_PASSWORD_HISTORY_COUNT);
        if(pwdHistorySetting != null && pwdHistorySetting.getEnabled() && pwdHistorySetting.getValue() > 0) {
            List<PasswordHistory> passwordHistories = passwordHistoryDao.findAllByEmployeeIdOrderByIdDesc(employee.getId(), new PageRequest(0,pwdHistorySetting.getValue().intValue()));
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

    @Override
    public void unlockEmployeeAccount(Long employeeId) {
        employeePasswordSecurityDao.unlockEmployeeAccount(employeeId);
    }

    private static EmployeePasswordSecurity createEmployeePasswordSecurity(Employee employee) {
        EmployeePasswordSecurity employeePasswordSecurity;
        employeePasswordSecurity = new EmployeePasswordSecurity();
        employeePasswordSecurity.setEmployee(employee);
        employeePasswordSecurity.setLocked(Boolean.FALSE);
        employeePasswordSecurity.setFailedLogonsCount(0);
        return employeePasswordSecurity;
    }

}
