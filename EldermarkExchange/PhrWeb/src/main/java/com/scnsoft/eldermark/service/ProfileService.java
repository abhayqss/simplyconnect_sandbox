package com.scnsoft.eldermark.service;

import com.google.maps.model.LatLng;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.phr.AccountTypeDao;
import com.scnsoft.eldermark.dao.phr.UserAccountTypeDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.Physician;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserAccountType;
import com.scnsoft.eldermark.service.passwords.PasswordValidationService;
import com.scnsoft.eldermark.service.validation.UserValidator;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.marketplace.MapsService;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.ccd.AddressDto;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.service.validation.SsnValidator;
import com.scnsoft.eldermark.shared.web.entity.AccountTypeDto;
import com.scnsoft.eldermark.web.entity.*;
import com.scnsoft.eldermark.web.security.PhrAuthTokenService;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author phomal
 * Created on 5/10/2017
 */
@Service
@Transactional
public class ProfileService extends BasePhrService {

    @Autowired
    UserAccountTypeDao userAccountTypeDao;

    @Autowired
    UserDao userDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    AccountTypeDao accountTypeDao;

    @Autowired
    private PhysiciansService physiciansService;

    //@Autowired
    //PhysicianCategoryDao physicianCategoryDao;

    @Autowired
    private PasswordValidationService passwordValidationService;

    private final StandardPasswordEncoder userPasswordEncoder = new StandardPasswordEncoder();

    @Autowired
    private AddressService addressService;

    @Autowired
    private DozerBeanMapper dozer;

    @Autowired
    private StateService stateService;

    @Autowired
    private MapsService mapsService;

    private AccountType patientAccountType;
    private AccountType guardianAccountType;
    private AccountType notifyAccountType;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private PhrAuthTokenService phrAuthTokenService;

    @PostConstruct
    void postConstruct() {
        // cache account types
        patientAccountType = accountTypeDao.findByType(AccountType.Type.CONSUMER);
        guardianAccountType = accountTypeDao.findByType(AccountType.Type.PROVIDER);
        notifyAccountType = accountTypeDao.findByType(AccountType.Type.NOTIFY);
    }

    public List<AccountTypeDto> getAccountTypes(Long userId) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        List<UserAccountType> userAccountTypes = userAccountTypeDao.findByUserId(userId);
        return transform(userAccountTypes);
    }

    public AccountStatusDto getAccountStatus(Long userId) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        User user = userDao.getOne(userId);
        AccountStatusDto dto = new AccountStatusDto();
        dto.setLockout(passwordValidationService.getLockoutInfo(user));

        return dto;
    }

    public Boolean changePassword(Long userId, char[] oldPassword, char[] newPassword) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        User user = userDao.getOne(userId);

        if (StringUtils.isBlank(user.getPasswordEncoded())) {
            // graceful degradation for users signed up before this registration flow update
            // can be removed when all active users have their password (passcode) saved on server-side
            throw new PhrException(PhrExceptionType.UNABLE_TO_VALIDATE_PASSWORD);
        }

        if (userPasswordEncoder.matches(CharBuffer.wrap(oldPassword), user.getPasswordEncoded())) {
            //passwordValidationService.processSuccessfulLogin(user);
            String passwordEncoded = userPasswordEncoder.encode(CharBuffer.wrap(newPassword));
            user.setPasswordEncoded(passwordEncoded);
            passwordValidationService.unlockAccount(user);
        } else {
            //passwordValidationService.processFailedLogin(user);
            throw new PhrException(PhrExceptionType.INVALID_PASSWORD);
        }

        return Boolean.TRUE;
    }

    public List<AccountTypeDto> setActiveAccountType(Long userId, AccountType.Type type) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        User user = userDao.getOne(userId);

        setActiveAccountType(user, type);

        List<UserAccountType> userAccountTypes = userAccountTypeDao.findByUser(user);
        return transform(userAccountTypes);
    }

    void setActiveAccountType(User user, AccountType.Type type) {
        AccountType accountType;
        if (AccountType.Type.CONSUMER.equals(type)) {
            accountType = patientAccountType;
        } else if (AccountType.Type.PROVIDER.equals(type)) {
            accountType = guardianAccountType;
        }
        else if (AccountType.Type.NOTIFY.equals(type)) {
            accountType = notifyAccountType;
        } else {
            throw new PhrException(PhrExceptionType.ACCOUNT_TYPE_NOT_AVAILABLE);
        }


        userAccountTypeDao.resetCurrentAccountType(user);
        if (1 != userAccountTypeDao.setCurrentAccountType(user, accountType)) {
            throw new PhrException(PhrExceptionType.ACCOUNT_TYPE_NOT_AVAILABLE);
        }
        userAccountTypeDao.flush();
    }

    public PersonalProfileDto getPersonalProfile(Long userId) {
        final User user = userDao.getOne(userId);
        if (user == null) {
            throw new PhrException(PhrExceptionType.USER_NOT_FOUND);
        }

        return transform(user);
    }

    public CoordinatesDto getPersonalAddressCoordinates(Long userId) {
        CoordinatesDto result = null;
        Long residentId = getResidentIdOrThrow(userId);
        Resident resident = residentDao.getResident(residentId);
        PersonAddress address = null;
        if (CollectionUtils.isNotEmpty(resident.getPerson().getAddresses())){
            address = resident.getPerson().getAddresses().get(0);
        }
        if (address != null) {
            com.scnsoft.eldermark.shared.carecoordination.AddressDto addressDto = new com.scnsoft.eldermark.shared.carecoordination.AddressDto();
            if (!StringUtils.isEmpty(address.getState())) {
                addressDto.setState(CareCoordinationUtils.createKeyValueDto(stateService.findByAbbr(address.getState())));
            }
            addressDto.setCity(address.getCity());
            addressDto.setStreet(address.getStreetAddress());
            addressDto.setZip(address.getPostalCode());
            LatLng latLng = mapsService.getCoordinatesByAddress(addressDto.getDisplayAddress());
            if (latLng != null) {
                result = new CoordinatesDto();
                result.setLatitude(latLng.lat);
                result.setLongitude(latLng.lng);
            }
        }
        return result;
    }

    public PersonalProfileDto editPersonalProfile(Long userId, PersonalProfileEditDto body) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        final User user = userDao.getOne(userId);

        boolean userWasModified = false;
        if (StringUtils.isNotBlank(body.getSecondaryPhone())) {
            UserValidator.validateSecondaryPhone(user, body.getSecondaryPhone());
            user.setSecondaryPhone(body.getSecondaryPhone());
            userWasModified = true;
        } else if (body.getSecondaryPhone() != null) {
            user.setSecondaryPhone(null);
            userWasModified = true;
        }
        if (StringUtils.isNotBlank(body.getSecondaryEmail())) {
            UserValidator.validateSecondaryEmail(user, body.getSecondaryEmail());
            user.setSecondaryEmail(body.getSecondaryEmail());
            userWasModified = true;
        } else if (body.getSecondaryEmail() != null) {
            user.setSecondaryEmail(null);
            userWasModified = true;
        }
        if (StringUtils.isNotBlank(body.getSsn()) && StringUtils.isBlank(user.getSsn())) {
            SsnValidator.validateSsnOrThrow(body.getSsn());
            user.setSsn(body.getSsn());
            userWasModified = true;
        }
        if (body.getAddress() != null) {
            final Employee employee = getEmployeeOrThrow(user);
            Person person = employee.getPerson();
            PersonAddress address = addressService.createAddressForPhrUser(body.getAddress(), person);
            if (address != null) {
                if (CollectionUtils.isEmpty(person.getAddresses())) {
                    person.getAddresses().add(address);
                } else {
                    person.getAddresses().set(0, address);
                }
                employeeDao.merge(employee);
            }
        }

        if (userWasModified) {
            userDao.save(user);
        }

        return transform(user);
    }

    public ProfessionalProfileDto getProfessionalProfile(Long userId) {
        Physician physician = getPhysicianOrThrow(userId);

        return physiciansService.transformProfessionalInfo(physician);
    }

    private Physician getPhysicianOrThrow(Long userId) {
        final Physician physician = physiciansService.getPhysicianByUserId(userId);
        if (physician == null) {
            throw new PhrException(PhrExceptionType.PHYSICIAN_NOT_FOUND);
        }
        return physician;
    }

    public ProfessionalProfileDto editProfessionalProfile(Long userId, ProfessionalProfileDto body) {
        throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        /*
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        Physician physician = getPhysicianOrThrow(userId);

        if (StringUtils.isNotBlank(body.getFax())) {
            physician.setFax(body.getFax());
        } else if (body.getFax() == null) {
            physician.setFax(null);
        }
        if (StringUtils.isNotBlank(body.getBoardOfCertifications())) {
            physician.setBoardOfCertifications(body.getBoardOfCertifications());
        } else if (body.getBoardOfCertifications() == null) {
            physician.setBoardOfCertifications(null);
        }
        if (StringUtils.isNotBlank(body.getEducation())) {
            physician.setEducation(body.getEducation());
        } else if (body.getEducation() == null) {
            physician.setEducation(null);
        }
        if (StringUtils.isNotBlank(body.getHospitalName())) {
            physician.setHospitalName(body.getHospitalName());
        } else if (body.getHospitalName() == null) {
            physician.setHospitalName(null);
        }
        if (StringUtils.isNotBlank(body.getInNetworkInsurances())) {
            physician.setInNetworkInsurances(body.getInNetworkInsurances());
        } else if (body.getInNetworkInsurances() == null) {
            physician.setInNetworkInsurances(null);
        }
        if (StringUtils.isNotBlank(body.getProfessionalMembership())) {
            physician.setProfessionalMembership(body.getProfessionalMembership());
        } else if (body.getProfessionalMembership() == null) {
            physician.setProfessionalMembership(null);
        }
        if (StringUtils.isNotBlank(body.getProfessionalStatement())) {
            physician.setProfessionalStatement(body.getProfessionalStatement());
        } else if (body.getProfessionalStatement() == null) {
            physician.setProfessionalStatement(null);
        }
        if (CollectionUtils.isEmpty(body.getSpecialities())) {
            physician.setCategories(null);
        } else {
            for (String speciality : body.getSpecialities()) {
                final PhysicianCategory category = physicianCategoryDao.getByDisplayName(speciality);
                physician.getCategories().add(category);
            }
        }

        return physiciansService.transformProfessionalInfo(physicianDao.save(physician));
        */
    }

    public Boolean deactivateProfile(Long userId) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        phrAuthTokenService.expireAllTokens(userId);

        return Boolean.TRUE;
    }

    void addPatientAccountType(User user, Boolean current) {
        final UserAccountType accountType = createAccountType(user, current, patientAccountType);
        userAccountTypeDao.save(accountType);
    }

    void addGuardianAccountType(User user, Boolean current) {
        final UserAccountType accountType = createAccountType(user, current, guardianAccountType);
        userAccountTypeDao.save(accountType);
    }

    private static UserAccountType createAccountType(User user, Boolean current, AccountType accountType) {
        UserAccountType userAccountType = new UserAccountType();
        userAccountType.setCurrent(current);
        userAccountType.setUser(user);
        userAccountType.setAccountType(accountType);
        return userAccountType;
    }

    boolean isPatient(User user) {
        for (UserAccountType userAccountType : user.getAccountTypes()) {
            if (AccountType.Type.CONSUMER.equals(userAccountType.getAccountType().getType())) {
                return true;
            }
        }
        return false;
    }

    boolean isGuardian(User user) {
        for (UserAccountType userAccountType : user.getAccountTypes()) {
            if (AccountType.Type.PROVIDER.equals(userAccountType.getAccountType().getType())) {
                return true;
            }
        }
        return false;
    }

    void initAccounts(User user, boolean forceConsumer) {
        boolean isEmployee = user.getEmployeeId() != null;
        boolean isResident = user.getResidentId() != null;
        boolean hasAccount = CollectionUtils.isNotEmpty(user.getAccountTypes());
        if (!isGuardian(user) && isEmployee) {
            addGuardianAccountType(user, !forceConsumer);
            hasAccount = true;
        }
        if (!isPatient(user) && (isResident || !hasAccount || forceConsumer)) {
            addPatientAccountType(user, forceConsumer || !hasAccount);
        }
    }

    private PersonalProfileDto transform(User user) {
        PersonalProfileDto dto = dozer.map(user, PersonalProfileDto.class);
        if (user.getPrimaryAddress() != null) {
            dto.setAddress(dozer.map(user.getPrimaryAddress(), AddressDto.class));
        }
        dto.setEmail(user.getEmployeeEmail());
        dto.setPhone(user.getEmployeePhone());
        dto.setLastFourDigitsOfSsn(user.getSsnLastFourDigits());
        return dto;
    }

    public static List<AccountTypeDto> transform(Iterable<UserAccountType> accountTypes) {
        List<AccountTypeDto> dtos = new ArrayList<>();
        for (UserAccountType accountType : accountTypes) {
            AccountTypeDto dto = transform(accountType);
            dtos.add(dto);
        }
        return dtos;
    }

    private static AccountTypeDto transform(UserAccountType accountType) {
        AccountTypeDto dto = new AccountTypeDto();
        dto.setName(accountType.getAccountType().getName());
        dto.setType(accountType.getAccountType().getType());
        dto.setCurrent(accountType.getCurrent());
        return dto;
    }

    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }

}
