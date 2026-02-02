package com.scnsoft.eldermark.service.passwords;

import com.scnsoft.eldermark.dao.password.UserPasswordSecurityDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import com.scnsoft.eldermark.entity.password.UserPasswordSecurity;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.exceptions.TimedLockedException;
import com.scnsoft.eldermark.services.password.DatabasePasswordSettingsService;
import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.password.PasswordComplexityVO;
import com.scnsoft.eldermark.shared.utils.MathUtils;
import com.scnsoft.eldermark.web.entity.AccountStatusDto;
import com.scnsoft.eldermark.web.entity.PasswordRequirementsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Provider;
import java.nio.CharBuffer;
import java.util.Date;

/**
 * @author phomal
 * Created on 11/9/2017.
 */
@Service
public class PasswordValidationService {

    private static final Long MINUTE_IN_MILLIS = 60L * 1000L;

    @Autowired
    private DatabasesService databasesService;

    @Autowired
    private EmployeePasswordSecurityService employeePasswordSecurityService;

    @Autowired
    private DatabasePasswordSettingsService databasePasswordSettingsService;

    @Autowired
    UserPasswordSecurityDao userPasswordSecurityDao;

    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private Provider<ResetLoginCounterRunnable> resetLoginCounterRunnableProvider;

    @Autowired
    private Provider<UnlockAccountRunnable> unlockAccountRunnableProvider;

    @Autowired
    private UserDao userDao;

    @Transactional(readOnly = true)
    public void validateComplexityOrThrow(CharSequence password) {
        final Long databaseId = databasesService.getUnaffiliatedDatabase().getId();

        final Boolean isComplexityValid = employeePasswordSecurityService.isComplexityValid(CharBuffer.wrap(password), databaseId);
        if (!isComplexityValid) {
            throw new PhrException(PhrExceptionType.INVALID_PASSWORD_COMPLEXITY);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = TimedLockedException.class)
    public void processSuccessfulLogin(Long userId) throws TimedLockedException {
        // load user again in new transaction session for the ability to fetch lazy initialized fields.
        final User user = userDao.findOne(userId);

        UserPasswordSecurity userPasswordSecurity = userPasswordSecurityDao.findByUser(user);
        if (userPasswordSecurity == null) {
            userPasswordSecurityDao.save(createUserPasswordSecurity(user));
            return;
        }

        checkAccountIsNotLockedOrThrow(userPasswordSecurity);
        if (userPasswordSecurity.getFailedLogonsCount() > 0) {
            userPasswordSecurityDao.resetFailAttempts(user.getId());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = TimedLockedException.class)
    public void processFailedLogin(Long userId) throws TimedLockedException {
        // load user again in new transaction session for the ability to fetch lazy initialized fields.
        final User user = userDao.findOne(userId);

        final Long databaseId = user.getEmployeeId() != null ? user.getEmployee().getDatabaseId() : databasesService.getUnaffiliatedDatabase().getId();

        DatabasePasswordSettings failedLogonCountSetting = databasePasswordSettingsService.getOrganizationSpecificSetting(
                databaseId, PasswordSettingsType.ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT);
        UserPasswordSecurity userPasswordSecurity = userPasswordSecurityDao.findByUser(user);
        if (userPasswordSecurity == null) {
            userPasswordSecurity = createUserPasswordSecurity(user);
            if (failedLogonCountSetting.getEnabled() && failedLogonCountSetting.getValue() > 0) {
                userPasswordSecurity.setFailedLogonsCount(1);
            }
            userPasswordSecurityDao.save(userPasswordSecurity);
        } else {
            if (failedLogonCountSetting.getEnabled() && failedLogonCountSetting.getValue() > 0) {
                userPasswordSecurityDao.increaseFailAttemptsCounter(user);
                userPasswordSecurity.setFailedLogonsCount(userPasswordSecurity.getFailedLogonsCount() + 1);
            }
        }

        if (userPasswordSecurity.getFailedLogonsCount() == 1) {
            DatabasePasswordSettings resetFailedLogonSetting = databasePasswordSettingsService.getOrganizationSpecificSetting(
                    databaseId, PasswordSettingsType.ACCOUNT_RESET_FAILED_LOGON_COUNT_IN_MINUTES);
            if (resetFailedLogonSetting.getEnabled() && resetFailedLogonSetting.getValue() > 0) {
                Long runTime = System.currentTimeMillis() + (resetFailedLogonSetting.getValue() * MINUTE_IN_MILLIS);
                final ResetLoginCounterRunnable runnable = resetLoginCounterRunnableProvider.get();
                runnable.setUserId(user.getId());
                threadPoolTaskScheduler.schedule(runnable, new Date(runTime));
            }
        }
        if (failedLogonCountSetting.getEnabled() && failedLogonCountSetting.getValue() > 0 && userPasswordSecurity.getFailedLogonsCount() >= failedLogonCountSetting.getValue()) {
            DatabasePasswordSettings lockAccountDurationSetting = databasePasswordSettingsService.getOrganizationSpecificSetting(
                    databaseId, PasswordSettingsType.ACCOUNT_LOCK_IN_MINUTES);
            if (lockAccountDurationSetting.getEnabled() && lockAccountDurationSetting.getValue() > 0 && !userPasswordSecurity.getLocked()) {
                Date lockDate = new Date();
                userPasswordSecurityDao.lockAccount(user, lockDate);
                userPasswordSecurity.setLocked(Boolean.TRUE);
                userPasswordSecurity.setLockedTime(lockDate);
                Long unlockTime = lockDate.getTime() + (lockAccountDurationSetting.getValue() * MINUTE_IN_MILLIS);
                final UnlockAccountRunnable runnable = unlockAccountRunnableProvider.get();
                runnable.setUserId(user.getId());
                threadPoolTaskScheduler.schedule(runnable, new Date(unlockTime));
            }
        }

        checkAccountIsNotLockedOrThrow(userPasswordSecurity);
    }

    /**
     * Throw {@link TimedLockedException} if account is temporarily locked.
     * Unlock account
     */
    private void checkAccountIsNotLockedOrThrow(UserPasswordSecurity userPasswordSecurity) throws TimedLockedException {
        long millisecondsLeft = getMillisecondsLeft(userPasswordSecurity);
        if (millisecondsLeft > 0L) {
            // this account is locked out -> throw exception to make brute force attack pointless
            throw new TimedLockedException(prepareMessage(millisecondsLeft), millisecondsLeft);
        }
    }

    private long getMillisecondsLeft(UserPasswordSecurity userPasswordSecurity) {
        if (userPasswordSecurity.getLocked()) {
            final User user = userPasswordSecurity.getUser();
            final Long databaseId = user.getEmployeeId() != null ? user.getEmployee().getDatabaseId() : databasesService.getUnaffiliatedDatabase().getId();

            DatabasePasswordSettings lockAccountDurationSetting = databasePasswordSettingsService.getOrganizationSpecificSetting(
                    databaseId, PasswordSettingsType.ACCOUNT_LOCK_IN_MINUTES);
            Date lockedDate = userPasswordSecurity.getLockedTime();
            final long now = System.currentTimeMillis();
            long millisecondsLeft = lockAccountDurationSetting.getValue() * MINUTE_IN_MILLIS - (now - lockedDate.getTime());
            if (millisecondsLeft >= 0L) {
                return millisecondsLeft;
            } else {
                // locking time has gone, but account remains locked
                // (it may happen, for example, if UnlockAccountRunnable failed to execute or locking time has been decreased via Password Settings control panel while this account was locked out)
                // -> unlock account
                userPasswordSecurityDao.unlockAccount(user.getId());
            }
        }

        return 0L;
    }

    private static String prepareMessage(Long millisecondsLeft) {
        long minutesLeft = MathUtils.ceil((double) millisecondsLeft / MINUTE_IN_MILLIS);
        return "Your account has been locked out because you have reached the maximum number of invalid logon attempts. Please try again in " + minutesLeft + " minute(s).";
    }

    private static UserPasswordSecurity createUserPasswordSecurity(User user) {
        UserPasswordSecurity userPasswordSecurity;
        userPasswordSecurity = new UserPasswordSecurity();
        userPasswordSecurity.setUser(user);
        userPasswordSecurity.setLocked(Boolean.FALSE);
        userPasswordSecurity.setFailedLogonsCount(0);
        return userPasswordSecurity;
    }

    @Transactional
    public void unlockAccount(User user) {
        userPasswordSecurityDao.unlockAccount(user.getId());
    }

    @Transactional(readOnly = true)
    public PasswordRequirementsDto getPasswordRequirements() {
        PasswordRequirementsDto dto = new PasswordRequirementsDto();

        final Long databaseId = databasesService.getUnaffiliatedDatabase().getId();
        final PasswordComplexityVO passwordComplexity = employeePasswordSecurityService.getPasswordComplexity(databaseId);
        dto.setRegexp(employeePasswordSecurityService.convertToPattern(passwordComplexity));
        dto.setText(convertToReadableText(passwordComplexity));

        return dto;
    }

    @Transactional(readOnly = true)
    public AccountStatusDto.Lockout getLockoutInfo(User user) {
        UserPasswordSecurity userPasswordSecurity = userPasswordSecurityDao.findByUser(user);

        final long millisecondsLeft = userPasswordSecurity == null ? 0L : getMillisecondsLeft(userPasswordSecurity);
        final AccountStatusDto.Lockout dto = new AccountStatusDto.Lockout();
        if (millisecondsLeft > 0L) {
            dto.setIsActive(Boolean.TRUE);
            dto.setMessage(prepareMessage(millisecondsLeft));
        } else {
            dto.setIsActive(Boolean.FALSE);
            dto.setMessage(null);
        }

        return dto;
    }

    private static final String PASSWORD_COMPLEXITY_TEXT_TEMPLATE_START = "at least ";
    private static final String PASSWORD_COMPLEXITY_TEXT_TEMPLATE_1 = "%s characters, ";
    private static final String PASSWORD_COMPLEXITY_TEXT_TEMPLATE_2 = "%s letter(s), ";
    private static final String PASSWORD_COMPLEXITY_TEXT_TEMPLATE_3 = "%s uppercase letter(s), ";
    private static final String PASSWORD_COMPLEXITY_TEXT_TEMPLATE_4 = "%s lowercase letter(s), ";
    private static final String PASSWORD_COMPLEXITY_TEXT_TEMPLATE_5 = "%s digit(s), ";
    private static final String PASSWORD_COMPLEXITY_TEXT_TEMPLATE_6 = "%s special symbol(s) (e.g. @#$%%!), ";
    private static final String PASSWORD_COMPLEXITY_TEXT_TEMPLATE_END = "the password is case sensitive and can not contain spaces.";

    private static String convertToReadableText(PasswordComplexityVO passwordComplexity) {
        final StringBuilder sb = new StringBuilder();
        if (passwordComplexity.isConfigured()) {
            sb.append(PASSWORD_COMPLEXITY_TEXT_TEMPLATE_START);
            if (passwordComplexity.getPasswordLength() > 0) {
                sb.append(String.format(PASSWORD_COMPLEXITY_TEXT_TEMPLATE_1, passwordComplexity.getPasswordLength()));
            }
            if (passwordComplexity.getAlphabeticCount() > 0) {
                sb.append(String.format(PASSWORD_COMPLEXITY_TEXT_TEMPLATE_2, passwordComplexity.getAlphabeticCount()));
            }
            if (passwordComplexity.getUppercaseCount() > 0) {
                sb.append(String.format(PASSWORD_COMPLEXITY_TEXT_TEMPLATE_3, passwordComplexity.getUppercaseCount()));
            }
            if (passwordComplexity.getLowercaseCount() > 0) {
                sb.append(String.format(PASSWORD_COMPLEXITY_TEXT_TEMPLATE_4, passwordComplexity.getLowercaseCount()));
            }
            if (passwordComplexity.getArabicNumeralsCount() > 0) {
                sb.append(String.format(PASSWORD_COMPLEXITY_TEXT_TEMPLATE_5, passwordComplexity.getArabicNumeralsCount()));
            }
            if (passwordComplexity.getSpecialCharsCount() > 0) {
                sb.append(String.format(PASSWORD_COMPLEXITY_TEXT_TEMPLATE_6, passwordComplexity.getSpecialCharsCount()));
            }
        }
        sb.append(PASSWORD_COMPLEXITY_TEXT_TEMPLATE_END);
        return sb.toString();
    }

}
