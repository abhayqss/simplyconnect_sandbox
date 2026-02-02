package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.phr.AccountTypeDao;
import com.scnsoft.eldermark.dao.phr.UserAccountTypeDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.Physician;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserAccountType;
import com.scnsoft.eldermark.shared.ccd.AddressDto;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.web.entity.AccountTypeDto;
import com.scnsoft.eldermark.web.entity.AddressEditDto;
import com.scnsoft.eldermark.web.entity.PersonalProfileDto;
import com.scnsoft.eldermark.web.entity.PersonalProfileEditDto;
import com.scnsoft.eldermark.web.entity.ProfessionalProfileDto;
import com.scnsoft.eldermark.web.security.PhrAuthTokenService;
import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 7/4/2017.
 */
public class ProfileServiceTest extends BaseServiceTest {

    @Mock
    private UserAccountTypeDao userAccountTypeDao;
    @Mock
    private UserDao userDao;
    @Mock
    private EmployeeDao employeeDao;
    @Mock
    private AccountTypeDao accountTypeDao;
    @Mock
    private PhysiciansService physiciansService;
    //@Mock
    //private PhysicianCategoryDao physicianCategoryDao;
    @Mock
    private AddressService addressService;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @Captor
    private ArgumentCaptor<User> captor;
    @Captor
    private ArgumentCaptor<UserAccountType> accountTypeCaptor;

    @Mock
    private PhrAuthTokenService phrAuthTokenService;

    @InjectMocks
    private ProfileService profileService;

    // Shared test data
    private final String ssn = TestDataGenerator.randomValidSsn();
    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();
    private final String firstName = TestDataGenerator.randomName();
    private final String lastName = TestDataGenerator.randomName();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nSSN: %s\nPhone: %s\nEmail: %s\nFirst name: %s\nLast name: %s\n\n",
                userId, ssn, phone, email, firstName, lastName);
    }

    @Before
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        profileService.setDozer(dozer);
    }

    @Before
    public void initCache() {
        when(accountTypeDao.findByType(AccountType.Type.CONSUMER)).thenReturn(TestDataGenerator.getConsumerAccountType());
        when(accountTypeDao.findByType(AccountType.Type.PROVIDER)).thenReturn(TestDataGenerator.getProviderAccountType());

        profileService.postConstruct();
    }

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User user = super.createConsumer(consumerUserId, ssn, phone, email, firstName, lastName);

        final UserAccountType userAccountType = new UserAccountType();
        userAccountType.setUser(user);
        userAccountType.setAccountType(TestDataGenerator.getConsumerAccountType());
        userAccountType.setCurrent(Boolean.TRUE);
        user.setAccountTypes(Collections.singleton(userAccountType));

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        when(userDao.findOne(consumerUserId)).thenReturn(user);
        when(userDao.getOne(consumerUserId)).thenReturn(user);
        when(userAccountTypeDao.findByUser(user)).thenReturn(Collections.singletonList(userAccountType));
        when(userAccountTypeDao.findByUserId(user.getId())).thenReturn(Collections.singletonList(userAccountType));
        when(userAccountTypeDao.setCurrentAccountType(user, TestDataGenerator.getConsumerAccountType())).thenReturn(1);

        return user;
    }

    private User setUpMockitoExpectationsAsProvider(Long providerUserId) {
        final User user = super.createProvider(providerUserId);

        final UserAccountType userAccountType = new UserAccountType();
        userAccountType.setUser(user);
        userAccountType.setAccountType(TestDataGenerator.getProviderAccountType());
        userAccountType.setCurrent(Boolean.TRUE);
        user.setAccountTypes(new HashSet<>(Arrays.asList(userAccountType)));

        when(userDao.findOne(providerUserId)).thenReturn(user);
        when(userDao.getOne(providerUserId)).thenReturn(user);
        when(userAccountTypeDao.findByUser(user)).thenReturn(Collections.singletonList(userAccountType));
        when(userAccountTypeDao.findByUserId(user.getId())).thenReturn(Collections.singletonList(userAccountType));
        when(userAccountTypeDao.setCurrentAccountType(user, TestDataGenerator.getProviderAccountType())).thenReturn(1);

        return user;
    }

    @Test
    public void testGetAccountTypes() {
        // Expected objects
        final AccountTypeDto expectedAccountTypeDto = new AccountTypeDto();
        expectedAccountTypeDto.setType(AccountType.Type.CONSUMER);
        expectedAccountTypeDto.setName("Consumer");
        expectedAccountTypeDto.setCurrent(Boolean.TRUE);

        setUpMockitoExpectations(userId);

        // Execute the method being tested
        List<AccountTypeDto> result = profileService.getAccountTypes(userId);

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedAccountTypeDto));
    }

    @Test
    public void testGetAccountTypesAsProvider() {
        // Expected objects
        final AccountTypeDto expectedAccountTypeDto = new AccountTypeDto();
        expectedAccountTypeDto.setType(AccountType.Type.PROVIDER);
        expectedAccountTypeDto.setName("Provider");
        expectedAccountTypeDto.setCurrent(Boolean.TRUE);

        setUpMockitoExpectationsAsProvider(userId);

        // Execute the method being tested
        List<AccountTypeDto> result = profileService.getAccountTypes(userId);

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedAccountTypeDto));
    }

    @Test
    public void testSetActiveAccountType() {
        final User user = setUpMockitoExpectations(userId);

        // Execute the method being tested
        List<AccountTypeDto> result = profileService.setActiveAccountType(userId, AccountType.Type.CONSUMER);

        // Validation
        assertThat(result, hasSize(1));
        verify(userAccountTypeDao).resetCurrentAccountType(user);
        verify(userAccountTypeDao).setCurrentAccountType(user, TestDataGenerator.getConsumerAccountType());
    }

    @Test(expected = PhrException.class)
    public void testSetActiveAccountTypeThrowsAccountTypeNotAvailable() {
        final User user = setUpMockitoExpectations(userId);

        // Execute the method being tested
        List<AccountTypeDto> result = profileService.setActiveAccountType(userId, AccountType.Type.PROVIDER);

        // Validation
        assertThat(result, hasSize(1));
        verify(userAccountTypeDao, never()).resetCurrentAccountType(user);
        verify(userAccountTypeDao, never()).setCurrentAccountType(user, TestDataGenerator.getConsumerAccountType());
    }

    @Test
    public void testSetActiveAccountType2() {
        final User user = setUpMockitoExpectations(userId);

        // Execute the method being tested
        profileService.setActiveAccountType(user, AccountType.Type.CONSUMER);

        // Validation
        verify(userAccountTypeDao).resetCurrentAccountType(user);
        verify(userAccountTypeDao).setCurrentAccountType(user, TestDataGenerator.getConsumerAccountType());
    }

    @Ignore("this screen is for provider mode only")
    @Test
    public void testGetPersonalProfileAsConsumer() {
        // Expected objects
        final PersonalProfileDto expectedPersonalProfile = new PersonalProfileDto();
        expectedPersonalProfile.setEmail(email);
        expectedPersonalProfile.setPhone(phone);
        expectedPersonalProfile.setLastFourDigitsOfSsn(StringUtils.right(ssn, 4));
        expectedPersonalProfile.setAddress(null);

        setUpMockitoExpectations(userId);

        // Execute the method being tested
        PersonalProfileDto result = profileService.getPersonalProfile(userId);

        // Validation
        assertThat(result, sameBeanAs(expectedPersonalProfile));
    }

    @Test
    public void testGetPersonalProfileAsProvider() {
        final User user = super.createProvider(userId, null, phone, email, firstName, lastName);

        // Expected objects
        final String email2 = TestDataGenerator.randomEmail();
        final String phone2 = TestDataGenerator.randomPhone();
        final String stateAbbr = "MN";

        final PersonAddress address = PersonAddress.Builder.aPersonAddress()
                .withCity("test city")
                .withStreetAddress("test street")
                .withPostalCode("54637")
                .withState(stateAbbr)
                .withCountry("US")
                .withPostalAddressUse("WP")
                .withPerson(user.getEmployee().getPerson())
                .withDatabase(createDatabase())
                .build();

        user.setSecondaryEmail(email2);
        user.setSecondaryPhone(phone2);
        user.getEmployee().getPerson().getAddresses().add(address);

        final AddressDto expectedAddress = new AddressDto();
        expectedAddress.setCity("test city");
        expectedAddress.setStreetAddress("test street");
        expectedAddress.setPostalCode("54637");
        expectedAddress.setState(stateAbbr);
        expectedAddress.setCountry("US");
        expectedAddress.setPostalAddressUse("WP");

        final PersonalProfileDto expectedPersonalProfile = new PersonalProfileDto();
        expectedPersonalProfile.setEmail(email);
        expectedPersonalProfile.setPhone(phone);
        expectedPersonalProfile.setSecondaryEmail(email2);
        expectedPersonalProfile.setSecondaryPhone(phone2);
        expectedPersonalProfile.setLastFourDigitsOfSsn(null);
        expectedPersonalProfile.setAddress(expectedAddress);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(user);

        // Execute the method being tested
        PersonalProfileDto result = profileService.getPersonalProfile(userId);

        // Validation
        assertThat(result, sameBeanAs(expectedPersonalProfile));
    }

    @Test(expected = PhrException.class)
    public void testGetPersonalProfileThrowsUserNotFound() {
        // Execute the method being tested
        profileService.getPersonalProfile(userId);
    }

    @Test
    public void testEditPersonalProfile() {
        final User provider = super.createProvider(userId, null, phone, email, firstName, lastName);

        // Expected objects
        final String email2 = TestDataGenerator.randomEmail();
        final String phone2 = TestDataGenerator.randomPhone();

        final AddressEditDto address = new AddressEditDto();
        address.setCity("test city");
        final PersonAddress personAddress = new PersonAddress();
        personAddress.setCity("test city");

        final PersonalProfileEditDto body = new PersonalProfileEditDto();
        body.setSecondaryEmail(email2);
        body.setSecondaryPhone(phone2);
        body.setSsn(ssn);
        body.setAddress(address);

        final AddressDto expectedAddress = new AddressDto();
        expectedAddress.setCity("test city");

        final PersonalProfileDto expectedPersonalProfile = new PersonalProfileDto();
        expectedPersonalProfile.setEmail(email);
        expectedPersonalProfile.setPhone(phone);
        expectedPersonalProfile.setSecondaryEmail(email2);
        expectedPersonalProfile.setSecondaryPhone(phone2);
        expectedPersonalProfile.setLastFourDigitsOfSsn(StringUtils.right(ssn, 4));
        expectedPersonalProfile.setAddress(expectedAddress);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(provider);
        when(addressService.createAddressForPhrUser(body.getAddress(), provider.getEmployee().getPerson())).thenReturn(personAddress);

        // Execute the method being tested
        PersonalProfileDto result = profileService.editPersonalProfile(userId, body);

        // Validation
        assertThat(result, sameBeanAs(expectedPersonalProfile));
        verify(userDao).save(provider);
        verify(employeeDao).merge(provider.getEmployee());
    }

    @Test
    public void testEditPersonalProfileSsnChangeNotAllowed() {
        final User user = super.createProvider(userId, ssn, phone, email, firstName, lastName);

        // Expected objects
        final String ssn2 = TestDataGenerator.randomValidSsnExceptOf(ssn);
        final PersonalProfileEditDto body = new PersonalProfileEditDto();
        body.setSsn(ssn2);

        final PersonalProfileDto expectedPersonalProfile = new PersonalProfileDto();
        expectedPersonalProfile.setEmail(email);
        expectedPersonalProfile.setPhone(phone);
        expectedPersonalProfile.setSecondaryEmail(null);
        expectedPersonalProfile.setSecondaryPhone(null);
        expectedPersonalProfile.setLastFourDigitsOfSsn(StringUtils.right(ssn, 4));
        expectedPersonalProfile.setAddress(null);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(user);

        // Execute the method being tested
        PersonalProfileDto result = profileService.editPersonalProfile(userId, body);

        // Validation
        assertThat(result, sameBeanAs(expectedPersonalProfile));
    }

    @Test
    public void testEditPersonalProfileDeleteSecondaryPhoneAndEmail() {
        final User provider = super.createProvider(userId, null, phone, email, firstName, lastName);

        // Expected objects
        final String email2 = TestDataGenerator.randomEmail();
        final String phone2 = TestDataGenerator.randomPhone();
        provider.setSecondaryEmail(email2);
        provider.setSecondaryPhone(phone2);

        final PersonalProfileEditDto body = new PersonalProfileEditDto();
        body.setSecondaryPhone("");
        body.setSecondaryEmail("");

        final PersonalProfileDto expectedPersonalProfile = new PersonalProfileDto();
        expectedPersonalProfile.setEmail(email);
        expectedPersonalProfile.setPhone(phone);
        expectedPersonalProfile.setSecondaryEmail(null);
        expectedPersonalProfile.setSecondaryPhone(null);
        expectedPersonalProfile.setLastFourDigitsOfSsn(null);
        expectedPersonalProfile.setAddress(null);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(provider);

        // Execute the method being tested
        PersonalProfileDto result = profileService.editPersonalProfile(userId, body);

        // Validation
        assertThat(result, sameBeanAs(expectedPersonalProfile));
        verify(userDao).save(provider);
    }

    @Test
    public void testGetProfessionalProfile() {
        // Expected objects
        final Physician physician = new Physician();
        final ProfessionalProfileDto expectedDto = new ProfessionalProfileDto();

        // Mockito expectations
        when(physiciansService.getPhysicianByUserId(userId)).thenReturn(physician);
        when(physiciansService.transformProfessionalInfo(physician)).thenReturn(expectedDto);

        // Execute the method being tested
        ProfessionalProfileDto result = profileService.getProfessionalProfile(userId);

        // Validation
        assertEquals(expectedDto, result);
    }

    @Test(expected = PhrException.class)
    public void testGetProfessionalProfileThrowsPhysicianNotFound() {
        // Execute the method being tested
        profileService.getProfessionalProfile(userId);
    }

    @Test(expected = PhrException.class)
    public void testEditProfessionalProfile() {
        profileService.editProfessionalProfile(userId, new ProfessionalProfileDto());
    }

    @Test
    public void testDeactivateProfile() {
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        Boolean result = profileService.deactivateProfile(userId);

        // Validation
        assertEquals(Boolean.TRUE, result);
        verify(phrAuthTokenService).expireAllTokens(userId);
    }

    @Test
    public void testAddPatientAccountType() {
        final User user = setUpMockitoExpectationsAsProvider(userId);

        // Expected objects
        final UserAccountType expectedUserAccountType = new UserAccountType();
        expectedUserAccountType.setUser(user);
        expectedUserAccountType.setAccountType(TestDataGenerator.getConsumerAccountType());
        expectedUserAccountType.setCurrent(Boolean.FALSE);

        // Execute the method being tested
        profileService.addPatientAccountType(user, Boolean.FALSE);

        // Validation
        verify(userAccountTypeDao).save(accountTypeCaptor.capture());
        final UserAccountType actual = accountTypeCaptor.getValue();
        assertThat(actual, sameBeanAs(expectedUserAccountType));
    }

    @Test
    public void testAddGuardianAccountType() {
        final User user = setUpMockitoExpectations(userId);

        // Expected objects
        final UserAccountType expectedUserAccountType = new UserAccountType();
        expectedUserAccountType.setUser(user);
        expectedUserAccountType.setAccountType(TestDataGenerator.getProviderAccountType());
        expectedUserAccountType.setCurrent(Boolean.FALSE);

        // Execute the method being tested
        profileService.addGuardianAccountType(user, Boolean.FALSE);

        // Validation
        verify(userAccountTypeDao).save(accountTypeCaptor.capture());
        final UserAccountType actual = accountTypeCaptor.getValue();
        assertThat(actual, sameBeanAs(expectedUserAccountType));
    }

    @Test
    public void testIsPatient() {
        final User user = setUpMockitoExpectations(userId);

        // Execute the method being tested
        boolean result = profileService.isPatient(user);

        // Validation
        assertEquals(true, result);
    }

    @Test
    public void testIsGuardian() {
        final User user = setUpMockitoExpectationsAsProvider(userId);

        // Execute the method being tested
        boolean result = profileService.isGuardian(user);

        // Validation
        assertEquals(true, result);
    }

    @Test
    public void testIsPatientFalse() {
        final User user = setUpMockitoExpectationsAsProvider(userId);

        // Execute the method being tested
        boolean result = profileService.isPatient(user);

        // Validation
        assertEquals(false, result);
    }

    @Test
    public void testIsGuardianFalse() {
        final User user = setUpMockitoExpectations(userId);

        // Execute the method being tested
        boolean result = profileService.isGuardian(user);

        // Validation
        assertEquals(false, result);
    }

    @Test
    public void testInitAccountsAsConsumer() {
        final User user = setUpMockitoExpectations(userId);
        user.setAccountTypes(Collections.<UserAccountType>emptySet());

        // Expected objects
        final UserAccountType expectedUserAccountType = new UserAccountType();
        expectedUserAccountType.setUser(user);
        expectedUserAccountType.setAccountType(TestDataGenerator.getConsumerAccountType());
        expectedUserAccountType.setCurrent(Boolean.TRUE);

        // Execute the method being tested
        profileService.initAccounts(user, false);

        // Validation
        verify(userAccountTypeDao).save(accountTypeCaptor.capture());
        final UserAccountType actual = accountTypeCaptor.getValue();
        assertThat(actual, sameBeanAs(expectedUserAccountType));
    }

    @Test
    public void testInitAccountsAsProvider() {
        final User user = setUpMockitoExpectationsAsProvider(userId);
        user.setAccountTypes(Collections.<UserAccountType>emptySet());

        // Expected objects
        final UserAccountType expectedUserAccountType = new UserAccountType();
        expectedUserAccountType.setUser(user);
        expectedUserAccountType.setAccountType(TestDataGenerator.getProviderAccountType());
        expectedUserAccountType.setCurrent(Boolean.TRUE);

        // Execute the method being tested
        profileService.initAccounts(user, false);

        // Validation
        verify(userAccountTypeDao).save(accountTypeCaptor.capture());
        final UserAccountType actual = accountTypeCaptor.getValue();
        assertThat(actual, sameBeanAs(expectedUserAccountType));
    }

    @Ignore("Impossible case?")
    @Test
    public void testInitAccountsAsProviderForceConsumer() {
        final User user = setUpMockitoExpectationsAsProvider(userId);

        // Expected objects
        final UserAccountType expectedUserAccountType = new UserAccountType();
        expectedUserAccountType.setUser(user);
        expectedUserAccountType.setAccountType(TestDataGenerator.getConsumerAccountType());
        expectedUserAccountType.setCurrent(Boolean.FALSE);

        // Execute the method being tested
        profileService.initAccounts(user, true);

        // Validation
        verify(userAccountTypeDao).save(accountTypeCaptor.capture());
        final UserAccountType actual = accountTypeCaptor.getValue();
        assertThat(actual, sameBeanAs(expectedUserAccountType));
    }

    @Test
    public void testInitAccountsAsNobody() {
        // Expected objects
        final User user = User.Builder.anUser()
                .withId(userId)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withResident(null)
                .withEmployee(null)
                .withAccountTypes(Collections.<UserAccountType>emptySet())
                .build();

        final UserAccountType expectedUserAccountType = new UserAccountType();
        expectedUserAccountType.setUser(user);
        expectedUserAccountType.setAccountType(TestDataGenerator.getConsumerAccountType());
        expectedUserAccountType.setCurrent(Boolean.TRUE);

        // Execute the method being tested
        profileService.initAccounts(user, false);

        // Validation
        verify(userAccountTypeDao).save(accountTypeCaptor.capture());
        final UserAccountType actual = accountTypeCaptor.getValue();
        assertThat(actual, sameBeanAs(expectedUserAccountType));
    }

    @Test
    public void testInitAccountsAsProviderConsumer() {
        final User user = setUpMockitoExpectationsAsProvider(userId);

        // Expected objects
        final UserAccountType userAccountType2 = new UserAccountType();
        userAccountType2.setUser(user);
        userAccountType2.setAccountType(TestDataGenerator.getConsumerAccountType());
        userAccountType2.setCurrent(Boolean.FALSE);
        user.getAccountTypes().add(userAccountType2);

        // Execute the method being tested
        profileService.initAccounts(user, false);

        // Validation
        verify(userAccountTypeDao, never()).save(any(UserAccountType.class));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme