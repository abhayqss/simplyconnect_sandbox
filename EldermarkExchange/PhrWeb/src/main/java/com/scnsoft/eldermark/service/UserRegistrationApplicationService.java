package com.scnsoft.eldermark.service;

import com.google.common.collect.Lists;
import com.scnsoft.eldermark.dao.phr.RegistrationApplicationDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.phr.RegistrationApplication;
import com.scnsoft.eldermark.entity.phr.RegistrationStep;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import com.scnsoft.eldermark.shared.service.RegistrationStepService;
import com.scnsoft.eldermark.shared.utils.RegistrationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.nio.CharBuffer;
import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * Created on 10/17/2017.
 */
@Service
public class UserRegistrationApplicationService extends BasePhrService {

    @Autowired
    RegistrationApplicationDao registrationApplicationDao;

    @Autowired
    RegistrationStepService registrationStepService;

    @Autowired
    DatabasesService databasesService;

    @Autowired
    AsyncHealthProviderService asyncHealthProviderService;

    private final StandardPasswordEncoder employeePasswordEncoder = new StandardPasswordEncoder();

    @Value("${confirmation.code.expiration.time}")
    private Integer confirmationCodeExpirationTime;

    public RegistrationApplication getOrCreateRegistrationApplicationForConsumer(Employee employee, String ssn, String phone, String email, String firstName, String lastName) {
        RegistrationApplication.Type type = RegistrationApplication.Type.SIGNUP_AS_CONSUMER;

        RegistrationApplication application = getExistingApplication(ssn, phone, email, firstName, lastName, type);
        if (application == null) {
            User user;
            if (employee != null) {
                user = userDao.getFirstByEmployee(employee);
                // check that existing user's SSN is matching with SSN submitted during sign up
                if (user != null && user.getSsn() != null && !user.getSsn().equals(ssn)) {
                    throw new PhrException(PhrExceptionType.USER_EMAIL_CONFLICT);
                }
            } else {
                // legacy : find mobile users without associated employee
                final Database unaffiliated = databasesService.getUnaffiliatedDatabase();
                final List<User> users = userDao.findUsersByData(unaffiliated, ssn, email, phone, firstName, lastName);
                user = CollectionUtils.isEmpty(users) ? null : users.get(0);
            }
            application = createNewApplication(user, ssn, phone, email, firstName, lastName, type);
        }
        initApplication(application);

        return application;
    }

    public RegistrationApplication getOrCreateRegistrationApplicationForProvider(Employee employee, String phone, String email, String firstName, String lastName) {
        RegistrationApplication.Type type = RegistrationApplication.Type.SIGNUP_AS_PROVIDER;

        RegistrationApplication application = getExistingApplication(phone, email, firstName, lastName, type);
        if (application == null) {
            User user = null;
            if (employee != null) {
                user = userDao.getFirstByEmployee(employee);
            }
            application = createNewApplication(user, null, phone, email, firstName, lastName, type);
        }
        initApplication(application);

        return application;
    }

    @Transactional
    public RegistrationApplication getOrCreateRegistrationApplicationForWebEmployee(Employee employee, String phone, String email) {
        RegistrationApplication.Type type = RegistrationApplication.Type.I_HAVE_ACCOUNT;

        // Search for existing with that data
        User user = userDao.getFirstByEmployee(employee);
        // legacy : look for existing mobile users with the same data.
        // this may happen, if mobile user was created without an associated employee, and then after some time, an employee with the same data was created
        if (user == null) {
            user = getExistingUser(employee.getDatabaseId(), phone, employee.getLoginName());
            if (user == null) {
                user = getExistingUser(employee.getDatabaseId(), phone, email);
            }
        }
        RegistrationApplication application = getExistingApplication(employee, phone, email, type);
        if (application == null) {
            application = createNewApplication(user, null, phone, email, employee.getFirstName(), employee.getLastName(), type);
            application.setEmployee(employee);
            application.setEmployeeId(employee.getId());
        } else {
            application.setUser(user);
        }
        initApplication(application);

        return application;
    }

    public RegistrationApplication createRegistrationApplicationForInvitee(User inviter, Employee employee, String ssn, String phone, String email,
                                                                           String firstName, String lastName) {
        RegistrationApplication application = createNewApplication(null, ssn, phone, email, firstName, lastName, null);
        application.setInviter(inviter);
        application.setEmployee(employee);
        application.setEmployeeId(employee.getId());
        application.setPerson(employee.getPerson());
        application.setRegistrationStep(registrationStepService.convert(RegistrationApplication.Step.INVITED));
        application.setCurrentSignupTime(new Date());

        return application;
    }

    private void initApplication(RegistrationApplication application) {
        application.setSignupAttemptCount(application.getSignupAttemptCount() + 1);
        application.setCurrentSignupTime(new Date());
        // the next step after sign up is phone confirmation
        application.setRegistrationStep(registrationStepService.convert(RegistrationApplication.Step.CONFIRMATION));
        Long activationCode = createConfirmationCode(application);
        application.setPhoneConfirmationCode(String.valueOf(activationCode));
        application.setConfirmationCodeIssuedAt(new Date());
    }

    private static RegistrationApplication createNewApplication(User user, String ssn, String phone, String email, String firstName, String lastName,
                                                                RegistrationApplication.Type type) {
        RegistrationApplication application = new RegistrationApplication();
        application.setSsn(ssn);
        application.setPhone(phone);
        application.setEmail(email);
        application.setFirstName(firstName);
        application.setLastName(lastName);
        if (user != null) {
            application.setUser(user);
            application.setResident(user.getResident());
            application.setResidentId(user.getResidentId());
            application.setEmployee(user.getEmployee());
            application.setEmployeeId(user.getEmployeeId());
            application.setTimeZoneOffset(user.getTimeZoneOffset());
        }

        application.setRegistrationType(type);
        application.setSignupAttemptCount(0);
        application.setPhoneConfirmationAttemptCount(0);

        return application;
    }

    private RegistrationApplication getExistingApplication(String ssn, String phone, String email, String firstName, String lastName,
                                                           RegistrationApplication.Type type) {
        final Pageable top1 = new PageRequest(0, 1, Sort.Direction.DESC, "currentSignupTime");
        final List<RegistrationApplication> applications = registrationApplicationDao.findAllBy(
                ssn, Normalizer.normalizeEmail(email), Normalizer.normalizePhone(phone), firstName, lastName, type,
                registrationStepService.excludeCompleted(), top1);
        return CollectionUtils.isEmpty(applications) ? null : applications.get(0);
    }

    private RegistrationApplication getExistingApplication(String phone, String email, String firstName, String lastName,
                                                           RegistrationApplication.Type type) {
        final Pageable top1 = new PageRequest(0, 1, Sort.Direction.DESC, "currentSignupTime");
        final List<RegistrationApplication> applications = registrationApplicationDao.findAllBy(
                Normalizer.normalizeEmail(email), Normalizer.normalizePhone(phone), firstName, lastName, type,
                registrationStepService.excludeCompleted(), top1);
        return CollectionUtils.isEmpty(applications) ? null : applications.get(0);
    }

    private RegistrationApplication getExistingApplication(Employee employee, String phone, String email, RegistrationApplication.Type type) {
        final Pageable top1 = new PageRequest(0, 1, Sort.Direction.DESC, "currentSignupTime");
        final List<RegistrationApplication> applications = registrationApplicationDao.findAllBy(employee, Normalizer.normalizeEmail(email),
                Normalizer.normalizePhone(phone), employee.getFirstName(), employee.getLastName(), type,
                registrationStepService.excludeCompleted(), top1);
        return CollectionUtils.isEmpty(applications) ? null : applications.get(0);
    }

    @Transactional
    public void save(RegistrationApplication application) {
        registrationApplicationDao.save(application);
    }

    public void confirmPhone(RegistrationApplication application, String code) throws PhrException {
        if (application == null) {
            throw new PhrException(PhrExceptionType.NO_CONFIRMATION_NEEDED);
        }
        final Date issuedAt = application.getConfirmationCodeIssuedAt();
        long diff = new Date().getTime() - issuedAt.getTime();
        if (!code.equals(application.getPhoneConfirmationCode()) || diff >= confirmationCodeExpirationTime) {
            application.setPhoneConfirmationAttemptCount(application.getPhoneConfirmationAttemptCount() + 1);
            // TODO persist and throw exception (do not rollback transaction)
            throw new PhrException(PhrExceptionType.INVALID_REGISTRATION_CODE);
        }
        application.setPhoneConfirmationCode(null);
        application.setPhoneConfirmationAttemptCount(0);

        final Employee employee = application.getEmployee();
        if (employee == null || employee.getStatus().equals(EmployeeStatus.PENDING)) {
            application.setRegistrationStep(registrationStepService.convert(RegistrationApplication.Step.WEB_ACCESS));
        } else {
            application.setRegistrationStep(registrationStepService.convert(RegistrationApplication.Step.COMPLETION));
        }

        // launch residents search in background
        if (application.getUser() == null) {
            asyncHealthProviderService.updateUserResidentRecords(application);
        } else {
            asyncHealthProviderService.updateUserResidentRecords(application.getUser().getId());
        }
    }

    private static class HardcodedConfirmationCodeEntry {
        String firstName;
        String lastName;
        String phone;
        String ssn;
        String email;
        Long code;

        HardcodedConfirmationCodeEntry(String firstName, String lastName, String phone, String ssn, String email, Long code) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.ssn = ssn;
            this.email = email;
            this.code = code;
        }
    }

    private static final List<HardcodedConfirmationCodeEntry> hardcodedConfirmationCodeEntries = Lists.newArrayList(
            new HardcodedConfirmationCodeEntry(
                    "Craig",
                    "Patnode",
                    "375297688482",
                    "312256379",
                    "test-craig-patnode1@test.test",
                    4523L),
            new HardcodedConfirmationCodeEntry(
                    "Craig",
                    "Patnode",
                    "375297688482",
                    "500416379",
                    "test-craig-patnode@test.test",
                    4523L),
            new HardcodedConfirmationCodeEntry(
                    "Joe",
                    "Decker",
                    "7632262643",
                    "123121234",
                    "jdecker@eldermark.com",
                    4523L),
            new HardcodedConfirmationCodeEntry(
                    "Dustin",
                    "Pease",
                    "6125782336",
                    "123451234",
                    "dustin@eldermark.com",
                    4523L)
    );


    private Long createConfirmationCode(RegistrationApplication application) {
        for (HardcodedConfirmationCodeEntry h : hardcodedConfirmationCodeEntries) {
            if (h.firstName.equalsIgnoreCase(application.getFirstName())
                    && h.lastName.equalsIgnoreCase(application.getLastName())
                    && h.phone.equals(application.getPhone())
                    && h.ssn.equals(application.getSsn())
                    && h.email.equalsIgnoreCase(application.getEmail())) {
                return h.code;
            }
        }
        return RegistrationUtils.generateConfirmationCode();
    }

    public void reGenerateConfirmationCode(RegistrationApplication application) {
        if (application == null) {
            throw new PhrException(PhrExceptionType.NO_CONFIRMATION_NEEDED);
        }
        application.setPhoneConfirmationAttemptCount(application.getPhoneConfirmationAttemptCount() + 1);
        Long activationCode = createConfirmationCode(application);
        application.setPhoneConfirmationCode(String.valueOf(activationCode));
        application.setConfirmationCodeIssuedAt(new Date());
    }

    public RegistrationApplication getRegistrationApplication(String ssn, String phone, String email, RegistrationApplication.Step step) {
        final RegistrationStep registrationStep = registrationStepService.convert(step);
        if (StringUtils.isBlank(ssn)) {
            return registrationApplicationDao.findFirstBySsnIsNullAndEmailNormalizedAndPhoneNormalizedAndRegistrationStepOrderByCurrentSignupTimeDesc(
                    Normalizer.normalizeEmail(email), Normalizer.normalizePhone(phone), registrationStep);
        } else {
            return registrationApplicationDao.findFirstBySsnAndEmailNormalizedAndPhoneNormalizedAndRegistrationStepOrderByCurrentSignupTimeDesc(
                    ssn, Normalizer.normalizeEmail(email), Normalizer.normalizePhone(phone), registrationStep);
        }
    }

    public RegistrationApplication getRegistrationApplication(String email, RegistrationApplication.Step step, RegistrationApplication.Type type) {
        final RegistrationStep registrationStep = registrationStepService.convert(step);

        return registrationApplicationDao.findFirstByEmailNormalizedAndRegistrationStepAndRegistrationTypeOrderByCurrentSignupTimeDesc(
                Normalizer.normalizeEmail(email), registrationStep, type);
    }

    public void setEmployeePassword(RegistrationApplication application, char[] password) {
        final String encodedPassword = employeePasswordEncoder.encode(CharBuffer.wrap(password));
        application.setEmployeePassword(encodedPassword);
        application.setRegistrationStep(registrationStepService.convert(RegistrationApplication.Step.COMPLETION));
    }

    public void completeRegistration(RegistrationApplication application) {
        application.setLastSignupTime(new Date());
        application.setEmployeePassword(null);
        application.setRegistrationStep(registrationStepService.convert(RegistrationApplication.Step.COMPLETED));
    }

}
