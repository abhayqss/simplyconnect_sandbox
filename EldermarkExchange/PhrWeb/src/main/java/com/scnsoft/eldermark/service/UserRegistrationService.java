package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserAccountTypeDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.*;
import com.scnsoft.eldermark.service.internal.EmployeeSupplier;
import com.scnsoft.eldermark.service.internal.EmployeeSupplierFactory;
import com.scnsoft.eldermark.service.passwords.PasswordValidationService;
import com.scnsoft.eldermark.service.validation.UserValidator;
import com.scnsoft.eldermark.services.exceptions.TimedLockedException;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.exception.ParameterizedPhrException;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.utils.MathUtils;
import com.scnsoft.eldermark.shared.web.entity.AccountTypeDto;
import com.scnsoft.eldermark.shared.web.entity.Token;
import com.scnsoft.eldermark.web.entity.*;
import com.scnsoft.eldermark.web.security.PhrAuthTokenService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 12/27/2016.
 */
@Service
public class UserRegistrationService extends BasePhrService {

    @Autowired
    HealthProviderService healthProviderService;

    @Autowired
    UserAccountTypeDao userAccountTypeDao;

    @Autowired
    ProfileService profileService;

    @Autowired
    NotificationPreferencesService notificationPreferencesService;

    @Autowired
    private PhrResidentService phrResidentService;

    @Autowired
    PhysiciansService physiciansService;

    @Autowired
    ContactService contactService;

    @Autowired
    UserRegistrationApplicationService userRegistrationApplicationService;

    @Autowired
    PasswordValidationService passwordValidationService;

    @Autowired
    private NotificationsFacade notificationsFacade;

    @Autowired
    private PhrAuthTokenService authTokenService;

    private final StandardPasswordEncoder userPasswordEncoder = new StandardPasswordEncoder();

    @Autowired
    private EmployeeSupplierFactory employeeSupplierFactory;

    Logger logger = Logger.getLogger(UserRegistrationService.class.getName());

    // intentionally non-transactional in order to persist failed login attempt
    public void validatePasswordOrThrow(Long userId, char[] password) {
        if (password != null) {
            User user = userDao.findOne(userId);

            if (StringUtils.isBlank(user.getPasswordEncoded())) {
                // graceful degradation for users signed up before this registration flow update
                // can be removed when all active users have their password (passcode) saved on server-side
                throw new PhrException(PhrExceptionType.UNABLE_TO_VALIDATE_PASSWORD);
            }

            if (userPasswordEncoder.matches(CharBuffer.wrap(password), user.getPasswordEncoded())) {
                passwordValidationService.processSuccessfulLogin(user.getId());
            } else {
                passwordValidationService.processFailedLogin(user.getId());
                throw new PhrException(PhrExceptionType.INVALID_PASSWORD);
            }
        }
    }

    public void updateTimeZone(Long userId, String timeZoneOffset) {
        if (userId != null && timeZoneOffset != null) {
            userDao.updateTimezone(userId, Long.valueOf(timeZoneOffset));
        }
    }

    @Transactional
    public RegistrationAnswerDto signupNewUser(String ssn, String phone, String email, String timeZoneOffset, String firstName, String lastName, boolean signupWithoutResident) {
        try {
            Employee employee = findAssociatedEmployee(email, phone, firstName, lastName);
            RegistrationApplication application = userRegistrationApplicationService.getOrCreateRegistrationApplicationForConsumer(
                    employee, ssn, phone, email, firstName, lastName);
            application.setTimeZoneOffset(Integer.valueOf(timeZoneOffset));

            Resident resident = application.getResident();
            if (resident == null) {
                resident = phrResidentService.findAssociatedResident(ssn, phone, email, firstName, lastName);
            }
            if (resident == null) {
                if (!signupWithoutResident) {
                    logger.warning("There are no patients with that data in PHR system");
                    throw new PhrException(PhrExceptionType.NO_ASSOCIATED_PATIENT_FOUND);
                }
            } else {
                application.setResident(resident);
                application.setResidentId(resident.getId());
            }

            if (application.getEmployeeId() == null && employee != null) {
                application.setEmployeeId(employee.getId());
                application.setEmployee(employee);
                application.setPerson(employee.getPerson());
            }

            // Create user registration application in DB
            userRegistrationApplicationService.save(application);

            // Send code
            notificationsFacade.confirmUserRegistration(application);

            return transform(application);
        } catch (PhrException e) {
            throw e;
        } catch (Exception e) {
            throw new PhrException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    @Transactional
    public RegistrationAnswerDto signupNewUser(ProviderRegistrationForm form) {
        try {
            final Employee employee = findAssociatedEmployee(form.getEmail(), form.getPhone(), form.getFirstName(), form.getLastName());
            RegistrationApplication application = userRegistrationApplicationService.getOrCreateRegistrationApplicationForProvider(
                    employee, form.getPhone(), form.getEmail(), form.getFirstName(), form.getLastName());
            application.setTimeZoneOffset(Integer.valueOf(form.getTimeZoneOffset()));

            User user = application.getUser();
            if (user != null) {
                final Physician physician = physiciansService.getPhysicianByUserId(user.getId());
                UserValidator.validatePhysicianNotExists(physician);
            }

            if (application.getEmployeeId() == null && employee != null) {
                application.setEmployeeId(employee.getId());
                application.setEmployee(employee);
                application.setPerson(employee.getPerson());
            }

            if (application.getEmployee() != null && employee.getStatus().equals(EmployeeStatus.ACTIVE)) {
                throw new PhrException(PhrExceptionType.EMPLOYEE_ALREADY_EXISTS);
            }

            if (application.getPerson() == null) {
                final Person person = contactService.createPersonInUnaffiliated(form.getEmail(), form.getPhone(), form.getFirstName(), form.getLastName(), form.getAddress());
                application.setPerson(person);
            }

            // Create Physician with "pending verification" status
            Physician physician = new Physician();
            physician.setUserMobile(user);
            physician.setDiscoverable(Boolean.FALSE);
            physician.setVerified(Boolean.FALSE);
            physician.setFax(form.getFax());
            physician.setProfessionalMembership(form.getProfessional().getProfessionalMembership());
            physician.setProfessionalStatement(form.getProfessional().getProfessionalStatement());
            physician.setBoardOfCertifications(form.getProfessional().getBoardOfCertifications());
            physician.setEducation(form.getProfessional().getEducation());
            physician.setNpi(form.getProfessional().getNpi());
            physician.setHospitalName(form.getProfessional().getHospitalName());
            Set<InNetworkInsurance> insurances = physiciansService.getInsurancesById(form.getProfessional().getInNetworkInsurancesIds());
            physician.setInNetworkInsurances(insurances);
            Set<PhysicianCategory> categories = physiciansService.getSpecialitiesById(form.getProfessional().getSpecialitiesIds());
            physician.setCategories(categories);

            Set<PhysicianAttachment> attachments = new HashSet<>();
            for (MultipartFile file : form.getFiles()) {
                PhysicianAttachment attachment = new PhysicianAttachment();
                attachment.setFile(file.getBytes());
                attachment.setContentType(file.getContentType());
                attachment.setOriginalName(file.getOriginalFilename());
                attachment.setPhysician(physician);
                attachments.add(attachment);
            }
            physician.setAttachments(attachments);
            application.setPhysician(physician);

            // Create user in DB
            physiciansService.create(physician);
            userRegistrationApplicationService.save(application);

            // Send code
            notificationsFacade.confirmUserRegistration(application);

            return transform(application);
        } catch (PhrException e) {
            throw e;
        } catch (Exception e) {
            throw new PhrException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * Find associated employee in Unaffiliated database. Throw exception if no exact match found but login (email) is already taken.
     */
    private Employee findAssociatedEmployee(String email, String phone, String firstName, String lastName) {
        // the commented out code is here for easy switching between three different employee lookup strategies
        // 1:
                /*EmployeeSupplier employeeSupplier;
                if (CollectionUtils.isEmpty(residentList)) {
                    employeeSupplier = employeeSupplierFactory.getUnaffiliatedEmployeeSupplier(email, phone, firstName, lastName);
                } else {
                    List<Long> organizationIds = new ArrayList<>();
                    CollectionUtils.collect(organizationIds, new BeanToPropertyValueTransformer("databaseId"), residentList);
                    employeeSupplier = employeeSupplierFactory.getEmployeeSupplier(organizationIds, email, phone, firstName, lastName);
                }*/
        // 2:
        //final EmployeeSupplier employeeSupplier = employeeSupplierFactory.getEmployeeSupplier(email, phone, firstName, lastName);
        // 3:
        final EmployeeSupplier employeeSupplier = employeeSupplierFactory.getUnaffiliatedEmployeeSupplier(email, phone, firstName, lastName);

        final Employee employee = employeeSupplier.getEmployee();
        if (employee == null) {
            final EmployeeSupplier employeeSupplier2 = employeeSupplierFactory.getUnaffiliatedEmployeeSupplier(email);
            if (employeeSupplier2.getEmployee() != null) {
                UserValidator.validatePhoneAndEmailNotEmpty(employeeSupplier2.getEmployee().getPerson());
                throw new PhrException(PhrExceptionType.USER_EMAIL_CONFLICT);
            }
            logger.info("There is no employees with that data in PHR system");
        }

        return employee;
    }

    static User createUser(String email, String phone, String ssn, String firstName, String lastName, boolean isPatient) {
        User user = new User();
        user.setEmail(email);
        user.setPhone(phone);
        user.setSsn(ssn);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhrPatient(isPatient);
        user.setAutocreated(Boolean.FALSE);

        return user;
    }

    @Transactional
    public void reGenerateCode(String ssn, String phone, String email) {
        RegistrationApplication application = userRegistrationApplicationService.getRegistrationApplication(ssn, phone, email,
                RegistrationApplication.Step.CONFIRMATION);
        userRegistrationApplicationService.reGenerateConfirmationCode(application);
        userRegistrationApplicationService.save(application);

        // Send code
        notificationsFacade.confirmUserRegistration(application);
    }

    @Transactional
    public void confirmRegistration(String ssn, String phone, String email, String code) {
        RegistrationApplication application = userRegistrationApplicationService.getRegistrationApplication(ssn, phone, email,
                RegistrationApplication.Step.CONFIRMATION);
        userRegistrationApplicationService.confirmPhone(application, code);
        userRegistrationApplicationService.save(application);
    }

    @Transactional
    public Token savePassword(String ssn, String phone, String email, char[] password) {
        try {
            RegistrationApplication application = userRegistrationApplicationService.getRegistrationApplication(ssn, phone, email,
                    RegistrationApplication.Step.COMPLETION);
            if (application == null) {
                // legacy (web access configuration is new screen and the first released phr app doesn't know about this screen)
                application = userRegistrationApplicationService.getRegistrationApplication(ssn, phone, email,
                        RegistrationApplication.Step.WEB_ACCESS);
                if (application == null) {
                    throw new PhrException(PhrExceptionType.NO_USER_FOUND);
                }
            }

            User user = application.getUser();
            boolean isNewMobileUser = (user == null);
            if (isNewMobileUser) {
                boolean isPatient = RegistrationApplication.Type.SIGNUP_AS_CONSUMER.equals(application.getRegistrationType());
                user = createUser(email, phone, ssn, application.getFirstName(), application.getLastName(), isPatient);
                user = userDao.save(user);
            }
            user.setAutocreated(Boolean.FALSE);
            user.setTimeZoneOffset(application.getTimeZoneOffset());

            if (password != null) {
                //passwordValidationService.validateComplexityOrThrow(CharBuffer.wrap(password));
                String passwordEncoded = userPasswordEncoder.encode(CharBuffer.wrap(password));
                user.setPasswordEncoded(passwordEncoded);
                passwordValidationService.unlockAccount(user);
            }

            boolean isNotificationRequired = false;
            boolean signUpAsConsumer = false;
            switch (application.getRegistrationType()) {
                case SIGNUP_AS_CONSUMER: {
                    signUpAsConsumer = true;
                    // bind main resident
                    if (application.getResidentId() == null) {
                        final Resident associatedResident = phrResidentService.createAssociatedResident(
                                email, phone, ssn, application.getFirstName(), application.getLastName(), String.valueOf(user.getId()), null);
                        application.setResident(associatedResident);
                        application.setResidentId(associatedResident.getId());
                    }
                    user.setResident(application.getResident());
                    user.setResidentId(application.getResidentId());
                    // bind main employee
                    if (application.getEmployeeId() == null && application.getEmployeePassword() != null) {
                        final Employee associatedEmployee = contactService.createEmployeeForConsumer(
                                email, phone, application.getFirstName(), application.getLastName(), application.getEmployeePassword());
                        application.setEmployee(associatedEmployee);
                        application.setEmployeeId(associatedEmployee.getId());
                        isNotificationRequired = true; // send notification in the end
                    } else if (application.getEmployeeId() != null && application.getEmployee().getStatus().equals(EmployeeStatus.PENDING) && application.getEmployeePassword() != null) {
                        contactService.setPassword(application.getEmployee(), application.getEmployeePassword());
                        isNotificationRequired = true; // send notification in the end
                    }
                    user.setEmployee(application.getEmployee());
                    user.setEmployeeId(application.getEmployeeId());
                    // add SSN if missing
                    if (StringUtils.isEmpty(user.getSsn()) && StringUtils.isNotEmpty(application.getSsn())) {
                        user.setSsn(application.getSsn());
                    }
                    if (isNewMobileUser) {
                        // check if any associated residents were found during registration
                        healthProviderService.updateUserResidentRecords(application, user);
                    }
                    break;
                }
                case SIGNUP_AS_PROVIDER: {
                    if (application.getEmployeeId() == null) {
                        final Employee associatedEmployee = contactService.createEmployeeForPhysician(
                                email, phone, application.getFirstName(), application.getLastName(), null, application.getPerson());
                        application.setEmployee(associatedEmployee);
                        application.setEmployeeId(associatedEmployee.getId());
                        application.getPhysician().setUserMobile(user);
                        application.getPhysician().setEmployee(associatedEmployee);
                        isNotificationRequired = true; // send notification in the end
                    }
                    user.setEmployee(application.getEmployee());
                    user.setEmployeeId(application.getEmployeeId());
                    break;
                }
                case I_HAVE_ACCOUNT: {
                    user.setEmployee(application.getEmployee());
                    user.setEmployeeId(application.getEmployeeId());
                    break;
                }
            }

            if (user.getDatabase() == null && user.getEmployeeId() != null) {
                final Database database = user.getEmployee().getDatabase();
                validateEmailNotExistsOrThrow(database, email, PhrExceptionType.USER_EMAIL_CONFLICT);
                user.setDatabase(database);
            }

            Token token = authTokenService.generateFor(user);
            //user.setTokenEncoded(Token.encode(token, passwordEncoder));

            application.setUser(userDao.save(user));
            userRegistrationApplicationService.completeRegistration(application);
            userRegistrationApplicationService.save(application);

            profileService.initAccounts(user, signUpAsConsumer);
            if (isNewMobileUser) {
                notificationPreferencesService.setDefaultNotificationSettings(user.getId(), false);
            } else {
                profileService.setActiveAccountType(user, signUpAsConsumer ? AccountType.Type.CONSUMER : AccountType.Type.PROVIDER);
                setActiveUserAccount(user, signUpAsConsumer ? AccountType.Type.CONSUMER : AccountType.Type.PROVIDER);
            }

            if (isNotificationRequired) {
                notificationsFacade.sendRegistrationConfirmation(user.getEmployee());
            }

            // something went wrong if user id is null at this point -> rollback transaction
            Validate.notNull(token.getUserId());

            return token;
        } catch (PhrException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PhrException(e.getMessage());
        }

    }

    @Transactional(readOnly = true)
    public UserDTO getUserDataBrief(Long id) {
        UserDTO userDTO = new UserDTO();
        User user = userDao.findOne(id);
        UserAccountType currentAccountType = userAccountTypeDao.findByUserAndCurrentIsTrue(user);
        Resident resident = user.getResident();
        if (userDTO.getProfile() == null) {
            userDTO.setProfile(new Profile());
        }
        if (resident != null) {
            if (resident.getBirthDate() != null) {
                userDTO.getProfile().setBirthDate(resident.getBirthDate().getTime());
            }
            userDTO.getProfile().setGender(resident.getGender() != null ? resident.getGender().getDisplayName() : null);
        }

        String firstName = null, lastName = null;
        switch (currentAccountType.getAccountType().getType()) {
            case CONSUMER:
                firstName = user.getResidentFirstNameLegacy();
                lastName = user.getResidentLastNameLegacy();
                break;
            case PROVIDER:
                firstName = user.getEmployeeFirstName();
                lastName = user.getEmployeeLastName();
                break;
        }
        userDTO.getProfile().setFirstName(firstName);
        userDTO.getProfile().setLastName(lastName);

        userDTO.setType(currentAccountType.getAccountType().getType());
        userDTO.setUserId(id);

        return userDTO;
    }

    private void setActiveUserAccount(User user, AccountType.Type type) {
        // In order to prevent loading stale data from cache (the reason of bug SCPAPP-369) we need to update UserAccountType entities
        // in session in addition to calling DAO methods. That's why this method is needed though it performs the same thing
        // as ProfileService#setActiveAccountType() and results in duplicated UPDATE queries sent to DB.
        for (UserAccountType accountType : user.getAccountTypes()) {
            final boolean isCurrent = type.equals(accountType.getAccountType().getType());
            accountType.setCurrent(isCurrent);
        }
    }

    @Transactional(readOnly = true)
    public List<AccountTypeDto> getUserAccounts(Long userId) {
        List<UserAccountType> accountTypes = userAccountTypeDao.findByUserId(userId);
        return ProfileService.transform(accountTypes);
    }

    // intentionally non-transactional
    public TelecomsDto signupUserAsWebEmployee(String companyId, String login, char[] password, String timeZoneOffset) {
        final EmployeeSupplier employeeSupplier = employeeSupplierFactory.getAuthenticationEmployeeSupplier(companyId, login, password);
        final Employee employee;

        try {
            employee = employeeSupplier.getEmployee();
        } catch (TimedLockedException exc) {
            exc.printStackTrace();
            final long durationMin = MathUtils.ceil(exc.getDurationMs() / (60.0 * 1000.0));
            throw new ParameterizedPhrException(PhrExceptionType.ACCOUNT_IS_LOCKED_OUT_DURING_REGISTRATION, durationMin);
        }

        final Pair<String, String> phoneAndEmail = contactService.getPhoneAndEmail(employee.getId());
        final RegistrationApplication application = userRegistrationApplicationService.getOrCreateRegistrationApplicationForWebEmployee(
                employee, phoneAndEmail.getFirst(), phoneAndEmail.getSecond());
        application.setTimeZoneOffset(Integer.valueOf(timeZoneOffset));

        // Create user registration application in DB
        userRegistrationApplicationService.save(application);

        // Send code
        notificationsFacade.confirmUserRegistration(application);

        return transformToTelecomsDto(application);
    }

    @Transactional
    public void registerWebEmployeeProvider(String login, char[] password) {
        RegistrationApplication application = userRegistrationApplicationService.getRegistrationApplication(
                login, RegistrationApplication.Step.WEB_ACCESS, RegistrationApplication.Type.SIGNUP_AS_PROVIDER);

        if (application == null) {
            throw new PhrException(PhrExceptionType.NO_USER_FOUND);
        }
        // validate this method is called only once (during the first signup)
        final Employee employee = application.getEmployee();
        if (employee != null && employee.getStatus().equals(EmployeeStatus.ACTIVE)) {
            throw new PhrException(PhrExceptionType.EMPLOYEE_ALREADY_EXISTS);
        }

        passwordValidationService.validateComplexityOrThrow(CharBuffer.wrap(password));
        userRegistrationApplicationService.setEmployeePassword(application, password);
        userRegistrationApplicationService.save(application);
    }

    @Transactional
    public void registerWebEmployeeConsumer(String login, char[] password) {
        RegistrationApplication application = userRegistrationApplicationService.getRegistrationApplication(
                login, RegistrationApplication.Step.WEB_ACCESS, RegistrationApplication.Type.SIGNUP_AS_CONSUMER);

        if (application == null) {
            throw new PhrException(PhrExceptionType.NO_USER_FOUND);
        }
        // validate this method is called only once (during the first signup)
        final Employee employee = application.getEmployee();
        if (employee != null && employee.getStatus().equals(EmployeeStatus.ACTIVE)) {
            throw new PhrException(PhrExceptionType.EMPLOYEE_ALREADY_EXISTS);
        }

        passwordValidationService.validateComplexityOrThrow(CharBuffer.wrap(password));
        userRegistrationApplicationService.setEmployeePassword(application, password);
        userRegistrationApplicationService.save(application);
    }

    private static RegistrationAnswerDto transform(RegistrationApplication application) {
        final Employee employee = application.getEmployee();
        RegistrationAnswerDto dto = new RegistrationAnswerDto();
        dto.setHasActiveWebAccount(employee != null && employee.getStatus().equals(EmployeeStatus.ACTIVE));
        dto.setFlowId(application.getFlowId());
        return dto;
    }

    private static TelecomsDto transformToTelecomsDto(RegistrationApplication application) {
        TelecomsDto dto = new TelecomsDto();
        dto.setEmail(application.getEmail());
        dto.setPhone(application.getPhone());
        dto.setFlowId(application.getFlowId());
        return dto;
    }

}
