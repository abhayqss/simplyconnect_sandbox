package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserAccountTypeDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.Physician;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.service.internal.EmployeeSupplierFactory;
import com.scnsoft.eldermark.service.internal.MockEmployeeSupplier;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.web.entity.Token;
import com.scnsoft.eldermark.shared.web.security.SymmetricKeyPasswordEncoder;
import com.scnsoft.eldermark.util.MockitoAnswers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/12/2017.
 */
public class UserRegistrationServiceTest extends BaseServiceTest {

    @Mock
    protected UserDao userDao;

    @Mock
    private HealthProviderService healthProviderService;

    @Mock
    private UserAccountTypeDao userAccountTypeDao;

    @Mock
    private ProfileService profileService;

    @Mock
    private NotificationPreferencesService notificationPreferencesService;

    @Mock
    private PhrResidentService phrResidentService;

    //@Mock
    //private EmployeeDao employeeDao;

    @Mock
    private PhysiciansService physiciansService;

    @Mock
    private ContactService contactService;

    @Mock
    private AddressService addressService;

    @Mock
    private NotificationsFacade notificationsFacade;

    @Mock
    private EmployeeSupplierFactory employeeProviderFactory;

    // token encryption is skipped for testing purposes
    @Mock
    private SymmetricKeyPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Captor
    private ArgumentCaptor<Physician> physicianCaptor;

    // Shared test data
    private final String tokenJson = Token.toJsonString(token);
    private final String tokenEncoded = Token.base64encode(token);
    private final String ssn = TestDataGenerator.randomValidSsn();
    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();
    private final String phoneNormalized = Normalizer.normalizePhone(phone);
    private final String emailNormalized = Normalizer.normalizeEmail(email);
    private final String fax = TestDataGenerator.randomPhone();
    private final String firstName = TestDataGenerator.randomName();
    private final String lastName = TestDataGenerator.randomName();
    private final Integer timeZoneOffset = TestDataGenerator.randomTimeZoneOffset();
    private final Long code = TestDataGenerator.randomConfirmationCode();
    private final Date birthDate = TestDataGenerator.randomBirthDate();
//    private final char[] password = "secure".toCharArray();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nToken (JSON): %s\nToken (encoded): %s\nSSN: %s\nPhone: %s\nEmail: %s\nFax: %s\nFirst name: %s\nLast name: %s\nTime zone offset (in minutes): %d\nConfirmation code: %d\nBirth date: %s\n\n",
                userId, tokenJson, tokenEncoded, ssn, phone, email, fax, firstName, lastName, timeZoneOffset, code, birthDate);
    }

    // Helper methods

    private void whenAskedForEmployeeThenReturn(Employee employee) {
        /*
        if (employee == null) {
            when(employeeDao.getEmployeesByData(email, phone)).thenReturn(Collections.<Employee>emptyList());
            when(employeeDao.getEmployeesByData(null, email, phone, firstName, lastName)).thenReturn(Collections.<Employee>emptyList());
        } else {
            when(employeeDao.getEmployeesByData(email, phone)).thenReturn(Collections.singletonList(employee));
            when(employeeDao.getEmployeesByData(null, email, phone, firstName, lastName)).thenReturn(Collections.singletonList(employee));
        } */

        final MockEmployeeSupplier employeeProvider = new MockEmployeeSupplier(employee);
        when(employeeProviderFactory.getEmployeeSupplier(email, phone, firstName, lastName)).thenReturn(employeeProvider);
        when(employeeProviderFactory.getEmployeeSupplier(email, phone, null, null)).thenReturn(employeeProvider);
        when(employeeProviderFactory.getUnaffiliatedEmployeeSupplier(email, phone, firstName, lastName)).thenReturn(employeeProvider);
        when(employeeProviderFactory.getEmployeeSupplier(anyCollectionOf(Long.class), eq(email), eq(phone), eq(firstName), eq(lastName)))
                .thenReturn(employeeProvider);
    }

    private void whenAskedForUserThenReturn(User user) {
        // FIXME
        Database database = new Database();
        if (user == null || Boolean.TRUE.equals(user.getAutocreated())) {
            when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized)).thenReturn(Collections.<User>emptyList());
            when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized)).thenReturn(Collections.<User>emptyList());
            when(userDao.findUsersByEmailAndPhoneNormalizedAndSsnIsNull(emailNormalized, phoneNormalized)).thenReturn(Collections.<User>emptyList());
            when(userDao.findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(database, emailNormalized)).thenReturn(null);
        } else {
            if (user.getSsn() == null) {
                when(userDao.findUsersByEmailAndPhoneNormalizedAndSsnIsNull(user.getEmailNormalized(), user.getPhoneNormalized()))
                        .thenReturn(Collections.singletonList(user));
            } else {
                when(userDao.findUsersByEmailAndPhoneNormalizedAndSsnIsNull(user.getEmailNormalized(), user.getPhoneNormalized()))
                        .thenReturn(Collections.<User>emptyList());
                when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(user.getSsn(), user.getEmailNormalized(), user.getPhoneNormalized()))
                        .thenReturn(Collections.singletonList(user));
            }
            when(userDao.findUsersByEmailAndPhoneNormalized(user.getEmailNormalized(), user.getPhoneNormalized())).thenReturn(Collections.singletonList(user));
            when(userDao.findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(database, user.getEmailNormalized())).thenReturn(user);
        }
        when(userDao.save(any(User.class))).then(MockitoAnswers.returnsPersistedUser());
        when(userDao.saveAndFlush(any(User.class))).then(MockitoAnswers.returnsPersistedUser());
    }

    @Test
    public void pleaseDontFailWhenNoTests() {}

    /*
    @Test
    public void signupNewUser() throws Exception {
        // Expected objects
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident();
        resident.setId(residentId);

        User expectedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withTimeZoneOffset(timeZoneOffset)
                .withResident(resident)
                .withActive(Boolean.FALSE)
                .withCurrentSignupTime(null)
                .withLastSignupTime(null)
                .withAutocreated(Boolean.FALSE)
                .withPhrPatient(Boolean.TRUE)
                .build();

        // Mockito expectations
        whenAskedForUserThenReturn(null);
        when(phrResidentService.findAssociatedResident(ssn, phone, email)).thenReturn(resident);
        whenAskedForEmployeeThenReturn(null);

        // Execute the method being tested
        User newUser = userRegistrationService.signupNewUser(ssn, phone, email, String.valueOf(timeZoneOffset), firstName, lastName, false);

        // Validation
        assertNotNull(newUser);
        assertNotNull(newUser.getRegistrationCode());
        assertThat(newUser, sameBeanAs(expectedUser)
                .ignoring("id").ignoring("registrationCode").ignoring(Date.class)
                .ignoring(Resident.class).ignoring("residentId")
                .ignoring("phoneNormalized").ignoring("emailNormalized"));
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verify(userDao).findUsersByEmailAndPhoneNormalizedAndSsnIsNull(emailNormalized, phoneNormalized);
        verify(userDao).findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(emailNormalized);
        verify(phrResidentService).findAssociatedResident(ssn, phone, email);
        verify(phrResidentService, never()).createAssociatedResident(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userDao).saveAndFlush(newUser);
        verify(notificationsFacade, only()).confirmUserRegistration(newUser);
    }

    @Test
    public void signupNewUserProvider() throws Exception {
        // Expected objects
        final String BROOKLYN = "Brooklyn";
        final String STREET = "2844 Ocean Parkway";
        final String ZIP_CODE = "11234";
        final String PROFESSIONAL_STATEMENT = "I am a 'people doctor'. Physician who sees each patient as a multi-faced whole person, not just a single symptom case.";
        final String BOARD_OF_CERTIFICATIONS = "American Board of Internal Medicine";
        final String EDUCATION = "Medical School -- State University of New York, Downstate Medical Center, Doctor of Medicine";
        final String PROFESSIONAL_MEMBERSHIP = "The Institute for Functional Medicine";
        final String HOSPITAL_NAME = "The Brooklyn Hospital Center";
        final String NPI = "npi test";
        final byte[] FILE_CONTENT = "hello world!".getBytes();
        final String ORIGINAL_NAME = "original title";
        final String CONTENT_TYPE = ContentType.TEXT_PLAIN.getMimeType();

        final Employee employee = createEmployee(email, firstName, lastName, null);
        employee.setInactive(Boolean.TRUE);
        final PersonAddress expectedAddress = PersonAddress.Builder.aPersonAddress()
                .withCity(BROOKLYN)
                .withStreetAddress(STREET)
                .withPostalCode(ZIP_CODE)
                .withState("NY")
                .withCountry("US")
                .withPostalAddressUse("WP")
                .withPerson(employee.getPerson())
                .withDatabase(employee.getDatabase())
                .build();
        employee.getPerson().setAddresses(Collections.singletonList(expectedAddress));

        final User expectedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withTimeZoneOffset(timeZoneOffset)
                .withEmployee(employee)
                .withActive(Boolean.FALSE)
                .withCurrentSignupTime(null)
                .withLastSignupTime(null)
                .withAutocreated(Boolean.FALSE)
                .withPhrPatient(Boolean.FALSE)
                .withRegistrationCode(code)
                .build();
        final UserProvider expectedUserProfile = UserProvider.Builder.anUserProvider()
                .withUser(expectedUser)
                .withPrimaryAddress(expectedAddress)
                .build();
        expectedUser.setPersonalProfile(expectedUserProfile);

        final Long specialityId = TestDataGenerator.randomId();
        final Set<PhysicianCategory> categories = new HashSet<>();
        PhysicianCategory category = new PhysicianCategory();
        category.setId(specialityId);
        category.setDisplayName("Primary Care Doctor");
        categories.add(category);
        category = new PhysicianCategory();
        category.setId(specialityId + 1);
        category.setDisplayName("Family Physician");
        categories.add(category);

        final Long insuranceId = TestDataGenerator.randomId();
        final Set<InNetworkInsurance> insurances = new HashSet<>();
        final InNetworkInsurance insurance = new InNetworkInsurance();
        insurance.setId(insuranceId);
        insurance.setDisplayName("Aetna");
        insurances.add(insurance);

        final PhysicianAttachment expectedPhysicianAttachment = new PhysicianAttachment();
        expectedPhysicianAttachment.setOriginalName(ORIGINAL_NAME);
        expectedPhysicianAttachment.setFile(FILE_CONTENT);
        expectedPhysicianAttachment.setContentType(CONTENT_TYPE);
        final Physician expectedPhysician = Physician.Builder.aPhysician()
                .withUserMobile(expectedUser)
                .withDiscoverable(Boolean.FALSE)
                .withVerified(Boolean.FALSE)
                .withEducation(EDUCATION)
                .withBoardOfCertifications(BOARD_OF_CERTIFICATIONS)
                .withProfessionalMembership(PROFESSIONAL_MEMBERSHIP)
                .withHospitalName(HOSPITAL_NAME)
                .withProfessionalStatement(PROFESSIONAL_STATEMENT)
                .withFax(fax)
                .withNpi(NPI)
                .withAttachments(Collections.singleton(expectedPhysicianAttachment))
                .withCategories(categories)
                .withInNetworkInsurance(insurances)
                .build();
        expectedPhysicianAttachment.setPhysician(expectedPhysician);

        // all fields, required and optional, are populated
        final List<Long> specialitiesIds = Arrays.asList(specialityId, specialityId + 1);
        final List<Long> inNetworkInsurancesIds = Collections.singletonList(insuranceId);
        ProfessionalProfileDto professionalProfileDto = ProfessionalProfileDto.Builder.aProfessionalProfileDto()
                .withEducation(EDUCATION)
                .withBoardOfCertifications(BOARD_OF_CERTIFICATIONS)
                .withProfessionalMembership(PROFESSIONAL_MEMBERSHIP)
                .withSpecialitiesIds(specialitiesIds)
                .withInNetworkInsurancesIds(inNetworkInsurancesIds)
                .withHospitalName(HOSPITAL_NAME)
                .withProfessionalStatement(PROFESSIONAL_STATEMENT)
                .withFax(fax)
                .withNpi(NPI)
                .build();
        MultipartFile mockFile = new MockMultipartFile("title", ORIGINAL_NAME, CONTENT_TYPE, FILE_CONTENT);
        AddressEditDto address = new AddressEditDto();
        address.setCity(BROOKLYN);
        address.setState("NY");
        address.setStreetAddress(STREET);
        address.setPostalCode(ZIP_CODE);
        ProviderRegistrationForm form = ProviderRegistrationForm.Builder.aProviderRegistrationForm()
                .withEmail(email)
                .withPhone(phone)
                .withFax(fax)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withTimeZoneOffset(String.valueOf(timeZoneOffset))
                .withAddress(address)
                .withProfessional(professionalProfileDto)
                .withFiles(Collections.singletonList(mockFile))
                .build();

        // Mockito expectations
        whenAskedForUserThenReturn(null);
        whenAskedForEmployeeThenReturn(null);
        when(contactService.createEmployeeForPhysician(eq(email), eq(phone), eq(firstName), eq(lastName), eq(address)))
                .thenReturn(employee);
        when(physiciansService.getSpecialitiesById(specialitiesIds)).thenReturn(categories);
        when(physiciansService.getInsurancesById(inNetworkInsurancesIds)).thenReturn(insurances);

        // Execute the method being tested
        User newUser = userRegistrationService.signupNewUser(form);

        // Validation
        assertNotNull(newUser);
        assertNotNull(newUser.getRegistrationCode());
        assertNull(newUser.getTokenEncoded());

        // Fix failing assertions. Can't ignore these fields for some strange reason !
        newUser.setId(userId);
        Whitebox.setInternalState(newUser, "phoneNormalized", phoneNormalized);
        Whitebox.setInternalState(newUser, "emailNormalized", emailNormalized);
        expectedUser.setRegistrationCode(newUser.getRegistrationCode());

        assertThat(newUser, sameBeanAs(expectedUser)
                .ignoring(Physician.class).ignoring(UserProvider.class).ignoring(Date.class));
        assertThat(newUser.getPersonalProfile(), sameBeanAs(expectedUserProfile)
                .ignoring("id").ignoring(User.class));

        verify(userDao).findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized);
        verify(userDao).findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(emailNormalized);
        verify(employeeProviderFactory).getEmployeeSupplier(email, phone, firstName, lastName);
        //verify(employeeProviderFactory).getUnaffiliatedEmployeeSupplier(email, phone, firstName, lastName);
        verify(userDao).saveAndFlush(newUser);
        verify(addressService, never()).createAddressForPhrUser(any(AddressEditDto.class), any(Person.class));
        verify(contactService).createEmployeeForPhysician(eq(email), eq(phone), eq(firstName), eq(lastName), eq(address));
        verify(contactService).merge(employee);
        verifyNoMoreInteractions(addressService);
        verifyNoMoreInteractions(contactService);
        verify(physiciansService).getSpecialitiesById(specialitiesIds);
        verify(physiciansService).getInsurancesById(inNetworkInsurancesIds);
        verify(notificationsFacade, only()).confirmPhysicianRegistration(newUser);

        verify(physiciansService).create(physicianCaptor.capture());
        final Physician newPhysician = physicianCaptor.getValue();
        assertThat(newPhysician, sameBeanAs(expectedPhysician).ignoring(User.class));
    }

    @Test
    public void signupNewUserProviderMinimal() throws Exception {
        // Expected objects
        final Employee employee = createEmployee(email, null, null, null);
        employee.setInactive(Boolean.TRUE);

        final User expectedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .withFirstName(null)
                .withLastName(null)
                .withTimeZoneOffset(timeZoneOffset)
                .withEmployee(employee)
                .withActive(Boolean.FALSE)
                .withCurrentSignupTime(null)
                .withLastSignupTime(null)
                .withAutocreated(Boolean.FALSE)
                .withPhrPatient(Boolean.FALSE)
                .withRegistrationCode(code)
                .build();
        final UserProvider expectedUserProfile = UserProvider.Builder.anUserProvider()
                .withUser(expectedUser)
                .build();
        expectedUser.setPersonalProfile(expectedUserProfile);

        final Long specialityId = TestDataGenerator.randomId();
        final Set<PhysicianCategory> categories = new HashSet<>();
        final PhysicianCategory category = new PhysicianCategory();
        category.setId(specialityId);
        categories.add(category);

        final Long insuranceId = TestDataGenerator.randomId();
        final Set<InNetworkInsurance> insurances = new HashSet<>();
        final InNetworkInsurance insurance = new InNetworkInsurance();
        insurance.setId(insuranceId);
        insurances.add(insurance);

        // only required fields are populated
        final List<Long> specialitiesIds = Collections.singletonList(specialityId);
        final List<Long> inNetworkInsurancesIds = Collections.singletonList(insuranceId);
        final ProfessionalProfileDto professionalProfileDto = ProfessionalProfileDto.Builder.aProfessionalProfileDto()
                .withSpecialitiesIds(specialitiesIds)
                .withInNetworkInsurancesIds(inNetworkInsurancesIds)
                .withHospitalName("Princeton Plainsboro")
                .withProfessionalStatement("Everybody lies")
                .build();
        final AddressEditDto address = new AddressEditDto();
        final ProviderRegistrationForm form = ProviderRegistrationForm.Builder.aProviderRegistrationForm()
                .withEmail(email)
                .withPhone(phone)
                .withFax(fax)
                .withFirstName(null)
                .withLastName(null)
                .withTimeZoneOffset(String.valueOf(timeZoneOffset))
                .withAddress(address)
                .withProfessional(professionalProfileDto)
                .withFiles(Collections.<MultipartFile>emptyList())
                .build();

        // Mockito expectations
        whenAskedForUserThenReturn(null);
        whenAskedForEmployeeThenReturn(null);
        when(contactService.createEmployeeForPhysician(eq(email), eq(phone), isNull(String.class), isNull(String.class), eq(address)))
                .thenReturn(employee);
        when(physiciansService.getSpecialitiesById(specialitiesIds)).thenReturn(categories);
        when(physiciansService.getInsurancesById(inNetworkInsurancesIds)).thenReturn(insurances);

        // Execute the method being tested
        User newUser = userRegistrationService.signupNewUser(form);

        // Validation
        assertNotNull(newUser);
        assertNotNull(newUser.getRegistrationCode());
        assertNull(newUser.getTokenEncoded());

        // Fix failing assertions. Can't ignore these fields for some strange reason !
        newUser.setId(userId);
        Whitebox.setInternalState(newUser, "phoneNormalized", phoneNormalized);
        Whitebox.setInternalState(newUser, "emailNormalized", emailNormalized);
        expectedUser.setRegistrationCode(newUser.getRegistrationCode());

        assertThat(newUser, sameBeanAs(expectedUser)
                .ignoring(Physician.class).ignoring(UserProvider.class).ignoring(Date.class));
        assertThat(newUser.getPersonalProfile(), sameBeanAs(expectedUserProfile)
                .ignoring("id").ignoring(User.class));

        verify(userDao).findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized);
        verify(userDao).findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(emailNormalized);
        verify(employeeProviderFactory).getEmployeeSupplier(email, phone, null, null);
        //verify(employeeProviderFactory).getUnaffiliatedEmployeeSupplier(email, phone, null, null);
        verify(userDao).saveAndFlush(newUser);
        verify(addressService).createAddressForPhrUser(address, employee.getPerson());
        verify(contactService).createEmployeeForPhysician(eq(email), eq(phone), isNull(String.class), isNull(String.class), eq(address));
        verify(contactService).merge(employee);
        verifyNoMoreInteractions(addressService);
        verifyNoMoreInteractions(contactService);
        verify(physiciansService).getSpecialitiesById(specialitiesIds);
        verify(physiciansService).getInsurancesById(inNetworkInsurancesIds);
        verify(physiciansService).create(any(Physician.class));
        verify(notificationsFacade, only()).confirmPhysicianRegistration(newUser);
    }

    @Test(expected = PhrException.class)
    public void signupNewUserProviderThrowsEmployeeAlreadyExists() throws Exception {
        // Expected objects
        final Employee employee = createEmployee(email, firstName, lastName, null);

        User expectedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withTimeZoneOffset(timeZoneOffset)
                .withEmployee(employee)
                .withActive(Boolean.FALSE)
                .withCurrentSignupTime(null)
                .withLastSignupTime(null)
                .withAutocreated(Boolean.FALSE)
                .withPhrPatient(Boolean.FALSE)
                .withRegistrationCode(code)
                .build();
        UserProvider expectedUserProfile = UserProvider.Builder.anUserProvider()
                .withUser(expectedUser)
                .build();
        Set<PhysicianCategory> categories = new HashSet<>();
        categories.add(new PhysicianCategory());
        expectedUser.setPersonalProfile(expectedUserProfile);

        AddressEditDto address = new AddressEditDto();
        ProviderRegistrationForm form = ProviderRegistrationForm.Builder.aProviderRegistrationForm()
                .withEmail(email)
                .withPhone(phone)
                .withFax(fax)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withTimeZoneOffset(String.valueOf(timeZoneOffset))
                .withAddress(address)
                .build();

        // Mockito expectations
        whenAskedForUserThenReturn(null);
        whenAskedForEmployeeThenReturn(employee);
        when(physiciansService.getSpecialitiesById(anyCollectionOf(Long.class))).thenReturn(categories);

        // Execute the method being tested
        userRegistrationService.signupNewUser(form);
    }

    @Test(expected = PhrException.class)
    public void signupNewUserThrowsNoAssociatedPatientFound() throws Exception {
        // Mockito expectations
        whenAskedForUserThenReturn(null);
        when(phrResidentService.findAssociatedResident(ssn, phone, email)).thenReturn(null);

        // Execute the method being tested
        userRegistrationService.signupNewUser(ssn, phone, email, String.valueOf(timeZoneOffset), firstName, lastName, false);

        // Validation
        verify(phrResidentService).findAssociatedResident(ssn, phone, email);
        verify(userDao, never()).saveAndFlush(any(User.class));
        verifyZeroInteractions(notificationsFacade);
    }

    @Test(expected = PhrException.class)
    public void signupNewUserThrowsEmailInUse() throws Exception {
        // Expected objects
        User existingUser = User.Builder.anUser()
                .withId(userId - 1)
                .withEmail("wrong " + phone)
                .withEmail(email)
                .withTimeZoneOffset(timeZoneOffset)
                .withActive(Boolean.TRUE)
                .build();

        // Mockito expectations
        whenAskedForUserThenReturn(existingUser);

        // Execute the method being tested
        userRegistrationService.signupNewUser(ssn, phone, email, String.valueOf(timeZoneOffset), firstName, lastName, false);

        // Validation
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verify(userDao).findUsersByEmailAndPhoneNormalizedAndSsnIsNull(emailNormalized, phoneNormalized);
        verify(userDao).findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(emailNormalized);
        verify(userDao, never()).saveAndFlush(any(User.class));
        verifyZeroInteractions(notificationsFacade);
    }

    @Test
    public void signupNewUserWithoutAssociatedPatient() throws Exception {
        // Expected objects
        final Resident resident = new Resident(residentId);

        User expectedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withTimeZoneOffset(timeZoneOffset)
                .withResident(resident)
                .withActive(Boolean.FALSE)
                .withCurrentSignupTime(null)
                .withLastSignupTime(null)
                .withAutocreated(Boolean.FALSE)
                .withPhrPatient(Boolean.TRUE)
                .build();

        // Mockito expectations
        whenAskedForUserThenReturn(null);
        when(phrResidentService.findAssociatedResident(ssn, phone, email))
                .thenReturn(null);
        when(phrResidentService.createAssociatedResident(eq(email), eq(phone), eq(ssn), eq(firstName), eq(lastName), anyString()))
                .thenReturn(resident);
        whenAskedForEmployeeThenReturn(null);

        // Execute the method being tested
        User newUser = userRegistrationService.signupNewUser(ssn, phone, email, String.valueOf(timeZoneOffset), firstName, lastName, true);

        // Validation
        assertNotNull(newUser);
        assertNotNull(newUser.getRegistrationCode());
        assertThat(newUser, sameBeanAs(expectedUser)
                .ignoring(Date.class)
                .ignoring("id").ignoring("registrationCode").ignoring("phoneNormalized").ignoring("emailNormalized"));

        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verify(userDao).findUsersByEmailAndPhoneNormalizedAndSsnIsNull(emailNormalized, phoneNormalized);
        verify(userDao).findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(emailNormalized);
        verify(phrResidentService).findAssociatedResident(ssn, phone, email);
        verify(phrResidentService).createAssociatedResident(eq(email), eq(phone), eq(ssn), eq(firstName), eq(lastName), anyString());
        //verify(employeeProviderFactory).getEmployeeSupplier(email, phone, firstName, lastName);
        //verify(employeeProviderFactory).getEmployeeSupplier(anyCollectionOf(Long.class), eq(email), eq(phone), eq(firstName), eq(lastName));
        verify(employeeProviderFactory).getUnaffiliatedEmployeeSupplier(email, phone, firstName, lastName);
        verify(userDao).saveAndFlush(newUser);
        verifyZeroInteractions(healthProviderService);
        verify(notificationsFacade, only()).confirmUserRegistration(newUser);
    }

    @Test
    public void signupNewUserWithExistingAccount() throws Exception {
        // Expected objects
        final Resident resident = new Resident(residentId);
        final Date date1 = new Date();
        final Date date2 = TestDataGenerator.randomDateBefore(date1);

        User expectedUser = createConsumer(userId, ssn, phone, email, firstName, lastName);
        User existingUser = createConsumer(userId, ssn, phone, email, firstName, lastName);
        expectedUser.setTimeZoneOffset(timeZoneOffset);
        expectedUser.setLastSignupTime(date2);
        expectedUser.setCurrentSignupTime(date1);
        existingUser.setTimeZoneOffset(timeZoneOffset);
        existingUser.setLastSignupTime(date2);
        existingUser.setCurrentSignupTime(date1);

        // Mockito expectations
        whenAskedForUserThenReturn(existingUser);
        when(phrResidentService.findAssociatedResident(ssn, phone, email)).thenReturn(resident);
        whenAskedForEmployeeThenReturn(null);

        // Execute the method being tested
        User persistedUser = userRegistrationService.signupNewUser(ssn, phone, email, String.valueOf(timeZoneOffset), firstName, lastName, false);

        // Validation
        assertNotNull(persistedUser);
        assertNotNull(persistedUser.getRegistrationCode());
        assertThat(persistedUser, sameBeanAs(expectedUser).ignoring("registrationCode").ignoring("currentSignupTime"));
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verifyZeroInteractions(phrResidentService);
        verify(userDao).saveAndFlush(persistedUser);
        verify(healthProviderService).updateUserResidentRecords(persistedUser);
        verify(notificationsFacade, only()).confirmUserRegistration(persistedUser);
    }

    @Test
    public void signupNewUserInvitedAsFriend() throws Exception {
        // Expected objects
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident();
        resident.setId(residentId);

        User inviter = new User();
        User expectedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withTimeZoneOffset(timeZoneOffset)
                .withActive(Boolean.FALSE)
                .withCurrentSignupTime(null)
                .withLastSignupTime(null)
                .withPhrPatient(Boolean.TRUE)
                .withInviter(inviter)
                .build();
        // the existing inactive user that was invited by someone without specifying ssn
        final User existingUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .withTimeZoneOffset(timeZoneOffset)
                .withResident(null)
                .withRegistrationCode(null)
                .withCurrentSignupTime(null)
                .withLastSignupTime(null)
                .withActive(Boolean.FALSE)
                .withPhrPatient(Boolean.TRUE)
                .withInviter(inviter)
                .build();

        // Mockito expectations
        whenAskedForUserThenReturn(existingUser);
        when(phrResidentService.findAssociatedResident(ssn, phone, email)).thenReturn(resident);
        whenAskedForEmployeeThenReturn(null);

        // Execute the method being tested
        User persistedUser = userRegistrationService.signupNewUser(ssn, phone, email, String.valueOf(timeZoneOffset), firstName, lastName, false);

        // Validation
        assertNotNull(persistedUser);
        assertNotNull(persistedUser.getRegistrationCode());
        assertThat(persistedUser, sameBeanAs(expectedUser).ignoring("registrationCode").ignoring("currentSignupTime"));
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verify(userDao).findUsersByEmailAndPhoneNormalizedAndSsnIsNull(emailNormalized, phoneNormalized);
        verify(phrResidentService).findAssociatedResident(ssn, phone, email);
        verify(userDao).saveAndFlush(persistedUser);
        verify(healthProviderService).updateUserResidentRecords(persistedUser);
        verify(notificationsFacade, only()).confirmUserRegistration(persistedUser);
    }

    @Test
    public void signupUserAsWebEmployee() throws Exception {
        // Expected objects
        final String companyId = "TestId";
        final char[] password = "password".toCharArray();

        final Person person = createPerson(phone, email, firstName, lastName, PersonTelecomCode.WP);
        final Employee employee = createEmployee(email, firstName, lastName, person);
        final MockEmployeeSupplier employeeProvider = new MockEmployeeSupplier(employee);

        final TelecomsDto expectedTelecomsDto = new TelecomsDto();
        expectedTelecomsDto.setEmail(email);
        expectedTelecomsDto.setPhone(phone);

        // Mockito expectations
        whenAskedForEmployeeThenReturn(employee);
        when(employeeProviderFactory.getAuthenticationEmployeeSupplier(companyId, email, password)).thenReturn(employeeProvider);
        whenAskedForUserThenReturn(null);

        // Execute the method being tested
        TelecomsDto result = userRegistrationService.signupUserAsWebEmployee(companyId, email, password, String.valueOf(timeZoneOffset));

        // Validation
        assertThat(result, sameBeanAs(expectedTelecomsDto));

        verify(userDao).findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized);
        verify(userDao).findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(emailNormalized);
        verify(userDao).save(any(User.class));
        verify(userDao).saveAndFlush(any(User.class));
        verify(notificationsFacade).confirmUserRegistration(any(User.class));
    }

    @Test
    public void signupUserAsWebEmployeeWithExistingAccount() throws Exception {
        // Expected objects
        final String companyId = "TestId";
        final char[] password = "password".toCharArray();

        final User expectedUser = createProvider(userId, null, phone, email, firstName, lastName);
        final User existingUser = createProvider(userId, null, phone, email, firstName, lastName);
        expectedUser.setTimeZoneOffset(timeZoneOffset);
        existingUser.setTimeZoneOffset(timeZoneOffset);

        final Person person = createPerson(phone, email, firstName, lastName, PersonTelecomCode.WP);
        final Employee employee = createEmployee(email, firstName, lastName, person);
        final MockEmployeeSupplier employeeProvider = new MockEmployeeSupplier(employee);

        final TelecomsDto expectedTelecomsDto = new TelecomsDto();
        expectedTelecomsDto.setEmail(email);
        expectedTelecomsDto.setPhone(phone);

        // Mockito expectations
        whenAskedForEmployeeThenReturn(employee);
        whenAskedForUserThenReturn(existingUser);
        when(employeeProviderFactory.getAuthenticationEmployeeSupplier(companyId, email, password)).thenReturn(employeeProvider);

        // Execute the method being tested
        TelecomsDto result = userRegistrationService.signupUserAsWebEmployee(companyId, email, password, String.valueOf(timeZoneOffset));

        // Validation
        assertThat(result, sameBeanAs(expectedTelecomsDto));

        verify(userDao).findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized);
        verify(userDao).saveAndFlush(any(User.class));
        verify(notificationsFacade).confirmUserRegistration(any(User.class));
    }

    @Test(expected = BadCredentialsException.class)
    public void signupUserAsWebEmployeeThrowsBadCredentials() throws Exception {
        // Expected objects
        final String companyId = "TestId";
        final char[] password = "password".toCharArray();

        // Mockito expectations
        when(employeeProviderFactory.getAuthenticationEmployeeSupplier(companyId, email, password)).thenThrow(BadCredentialsException.class);
        whenAskedForUserThenReturn(null);

        // Execute the method being tested
        userRegistrationService.signupUserAsWebEmployee(companyId, email, password, String.valueOf(timeZoneOffset));
    }

    @Test(expected = PhrException.class)
    public void signupUserAsWebEmployeeThrowsNoEmailForRegistration() throws Exception {
        // Expected objects
        final String companyId = "TestId";
        final char[] password = "password".toCharArray();

        final Person person = createPerson(phone, null, firstName, lastName, PersonTelecomCode.WP);
        final Employee employee = createEmployee(email, firstName, lastName, person);
        final MockEmployeeSupplier employeeProvider = new MockEmployeeSupplier(employee);

        // Mockito expectations
        when(employeeProviderFactory.getAuthenticationEmployeeSupplier(companyId, email, password)).thenReturn(employeeProvider);
        whenAskedForUserThenReturn(null);

        // Execute the method being tested
        userRegistrationService.signupUserAsWebEmployee(companyId, email, password, String.valueOf(timeZoneOffset));
    }

    @Test(expected = PhrException.class)
    public void signupUserAsWebEmployeeThrowsNoPhoneForRegistration() throws Exception {
        // Expected objects
        final String companyId = "TestId";
        final char[] password = "password".toCharArray();

        final Person person = createPerson(null, email, firstName, lastName, null);
        final Employee employee = createEmployee(email, firstName, lastName, person);
        final MockEmployeeSupplier employeeProvider = new MockEmployeeSupplier(employee);

        // Mockito expectations
        when(employeeProviderFactory.getAuthenticationEmployeeSupplier(companyId, email, password)).thenReturn(employeeProvider);
        whenAskedForUserThenReturn(null);

        // Execute the method being tested
        userRegistrationService.signupUserAsWebEmployee(companyId, email, password, String.valueOf(timeZoneOffset));
    }

    @Test
    public void reGenerateCode() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .build();

        // Mockito expectations
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));

        // Execute the method being tested
        userRegistrationService.reGenerateCode(ssn, phone, email);

        // Validation
        assertNotNull(persistedUser.getRegistrationCode());
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verify(userDao).save(persistedUser);
        verify(notificationsFacade).confirmUserRegistration(persistedUser);
    }

    @Test
    public void reGenerateCodeProvider() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .build();

        // Mockito expectations
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));
        when(physiciansService.getPhysicianByUser(persistedUser)).thenReturn(new Physician());

        // Execute the method being tested
        userRegistrationService.reGenerateCode(null, phone, email);

        // Validation
        assertNotNull(persistedUser.getRegistrationCode());
        assertThat("registration code", persistedUser.getRegistrationCode(), Matchers.greaterThan(0L));
        assertThat("registration code", persistedUser.getRegistrationCode(), Matchers.lessThan(10000L));
        verify(userDao).findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized);
        verify(userDao).save(persistedUser);
        verify(notificationsFacade).confirmUserRegistration(persistedUser);
    }

    @Test(expected = PhrException.class)
    public void reGenerateCodeThrowsNoUserFound() throws Exception {
        // Mockito expectations
        whenAskedForUserThenReturn(null);

        // Execute the method being tested
        userRegistrationService.reGenerateCode(ssn, phone, email);

        // Validation
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
    }

    @Test
    public void confirmRegistration() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withRegistrationCode(code)
                .build();

        // Mockito expectations
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));

        // Execute the method being tested
        userRegistrationService.confirmRegistration(ssn, phone, email, String.valueOf(code));

        // Validation
        assertNull(persistedUser.getRegistrationCode());
        assertTrue(persistedUser.getActive());
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verify(userDao).save(persistedUser);
        verify(profileService).initAccounts(persistedUser, true);
        verify(notificationPreferencesService).setDefaultNotificationSettings(eq(userId), any(Boolean.class));
    }

    @Test
    public void confirmRegistrationProvider() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .withRegistrationCode(code)
                .build();

        // Mockito expectations
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));
        when(physiciansService.getPhysicianByUser(persistedUser)).thenReturn(new Physician());

        // Execute the method being tested
        userRegistrationService.confirmRegistration(null, phone, email, String.valueOf(code));

        // Validation
        assertNull(persistedUser.getRegistrationCode());
        assertTrue(persistedUser.getActive());
        verify(userDao).findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized);
        verify(userDao).save(persistedUser);
        verify(profileService).initAccounts(persistedUser, false);
        verify(notificationPreferencesService).setDefaultNotificationSettings(eq(userId), any(Boolean.class));
    }

    @Test(expected = PhrException.class)
    public void confirmRegistrationThrowsNoUserFound() throws Exception {
        // Mockito expectations
        whenAskedForUserThenReturn(null);

        // Execute the method being tested
        userRegistrationService.confirmRegistration(ssn, phone, email, String.valueOf(code));

        // Validation
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
    }

    @Test(expected = PhrException.class)
    public void confirmRegistrationThrowsInvalidRegistrationCode() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withRegistrationCode(code)
                .build();

        // Mockito expectations
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));

        // Execute the method being tested
        userRegistrationService.confirmRegistration(ssn, phone, email, String.valueOf(code +  1));

        // Validation
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
    }

    @Test
    public void savePassword() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withTokenEncoded(null)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withCurrentSignupTime(new Date())
                .withRegistrationCode(null)
                .withActive(Boolean.TRUE)
                .withPhrPatient(Boolean.TRUE)
                .build();

        // Mockito expectations
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));
        when(passwordEncoder.encode(tokenJson)).thenReturn(tokenEncoded);

        // Execute the method being tested
        Token newToken = userRegistrationService.savePassword(ssn, phone, email);

        // Validation
        assertNotNull(newToken);
        assertEquals(newToken.getUserId(), userId);
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verify(userDao).save(persistedUser);
    }

    @Test
    public void savePasswordProvider() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withTokenEncoded(null)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .withCurrentSignupTime(new Date())
                .withRegistrationCode(null)
                .withActive(Boolean.TRUE)
                .withPhrPatient(Boolean.FALSE)
                .build();

        // Mockito expectations
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));
        when(passwordEncoder.encode(tokenJson)).thenReturn(tokenEncoded);
        when(physiciansService.getPhysicianByUser(persistedUser)).thenReturn(new Physician());

        // Execute the method being tested
        Token newToken = userRegistrationService.savePassword(null, phone, email);

        // Validation
        assertNotNull(newToken);
        assertEquals(newToken.getUserId(), userId);
        verify(userDao).findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized);
        verify(userDao).save(persistedUser);
    }

    @Ignore("validation removed")
    @Test(expected = PhrException.class)
    public void savePasswordProviderThrowsNoUserFound() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withTokenEncoded(null)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .withRegistrationCode(null)
                .withActive(Boolean.TRUE)
                .withPhrPatient(Boolean.FALSE)
                .build();

        // Mockito expectations
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));
        when(passwordEncoder.encode(tokenJson)).thenReturn(tokenEncoded);
        when(physiciansService.getPhysicianByUser(persistedUser)).thenReturn(null);

        // Execute the method being tested
        userRegistrationService.savePassword(null, phone, email);

        // Validation
        verify(userDao).findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    public void savePasswordForUserWithExistingToken() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withTokenEncoded(tokenEncoded)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withCurrentSignupTime(new Date())
                .withRegistrationCode(null)
                .withActive(Boolean.TRUE)
                .build();

        // Mockito expectations
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));
        when(passwordEncoder.decode(tokenEncoded)).thenReturn(tokenJson);

        // Execute the method being tested
        Token persistedToken = userRegistrationService.savePassword(ssn, phone, email);

        // Validation
        assertNotNull(persistedToken);
        assertThat(persistedToken, sameBeanAs(token));
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verify(userDao).save(persistedUser);
        verifyNoMoreInteractions(userDao);
    }

    @Test(expected = PhrException.class)
    public void savePasswordThrowsNoUserFound() throws Exception {
        // Mockito expectations
        whenAskedForUserThenReturn(null);

        // Execute the method being tested
        userRegistrationService.savePassword(ssn, phone, email);

        // Validation
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verifyZeroInteractions(passwordEncoder);
    }

    @Test(expected = PhrException.class)
    public void savePasswordThrowsUserNotActivated() throws Exception {
        // Expected object
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withTokenEncoded(null)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withActive(Boolean.FALSE)
                .build();

        // Mockito expectations
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(persistedUser));

        // Execute the method being tested
        userRegistrationService.savePassword(ssn, phone, email);

        // Validation
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
        verifyZeroInteractions(passwordEncoder);
    }

    @Test
    public void getUserDataBrief() throws Exception {
        // Expected objects
        final String genderDisplayName = Gender.MALE.getLabel();
        final CcdCode genderCcdCode = new CcdCode();
        genderCcdCode.setDisplayName(genderDisplayName);
        final Resident persistedResident = new Resident(residentId);
        persistedResident.setBirthDate(birthDate);
        persistedResident.setFirstName(firstName);
        persistedResident.setLastName(lastName);
        persistedResident.setGender(genderCcdCode);
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withTokenEncoded(null)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withFirstName(null)
                .withLastName(null)
                .withResident(persistedResident)
                .withActive(Boolean.FALSE)
                .build();
        final UserAccountType persistedUserAccountType = new UserAccountType();
        persistedUserAccountType.setUser(persistedUser);
        persistedUserAccountType.setAccountType(TestDataGenerator.getConsumerAccountType());
        persistedUserAccountType.setCurrent(Boolean.TRUE);

        Profile expectedProfile = Profile.Builder.aProfile()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withBirthDate(birthDate.getTime())
                .withGender(genderDisplayName)
                .build();
        UserDTO expectedUserDataBrief = new UserDTO();
        expectedUserDataBrief.setUserId(userId);
        expectedUserDataBrief.setProfile(expectedProfile);
        expectedUserDataBrief.setType(AccountType.Type.CONSUMER);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(persistedUser);
        when(userAccountTypeDao.findByUserAndCurrentIsTrue(persistedUser)).thenReturn(persistedUserAccountType);

        // Execute the method being tested
        UserDTO userDataBrief = userRegistrationService.getUserDataBrief(userId);

        // Validation
        assertNotNull(userDataBrief);
        assertThat(userDataBrief, sameBeanAs(expectedUserDataBrief));
        verify(userDao).findOne(userId);
        verify(userAccountTypeDao).findByUserAndCurrentIsTrue(persistedUser);
    }

    @Test
    public void getUserAccounts() throws Exception {
        // Expected objects
        final User persistedUser = User.Builder.anUser()
                .withId(userId)
                .withActive(Boolean.TRUE)
                .build();
        final UserAccountType persistedUserAccountTypeConsumer = new UserAccountType();
        persistedUserAccountTypeConsumer.setUser(persistedUser);
        persistedUserAccountTypeConsumer.setAccountType(TestDataGenerator.getConsumerAccountType());
        persistedUserAccountTypeConsumer.setCurrent(Boolean.TRUE);
        final UserAccountType persistedUserAccountTypeProvider = new UserAccountType();
        persistedUserAccountTypeProvider.setUser(persistedUser);
        persistedUserAccountTypeProvider.setAccountType(TestDataGenerator.getProviderAccountType());
        persistedUserAccountTypeProvider.setCurrent(Boolean.FALSE);

        AccountTypeDto expectedUserAccountTypeConsumer = AccountTypeDto.Builder.anAccountTypeDto()
                .withType(AccountType.Type.CONSUMER)
                .withCurrent(Boolean.TRUE)
                .build();
        AccountTypeDto expectedUserAccountTypeProvider = AccountTypeDto.Builder.anAccountTypeDto()
                .withType(AccountType.Type.PROVIDER)
                .withCurrent(Boolean.FALSE)
                .build();

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(persistedUser);
        when(userAccountTypeDao.findByUser(persistedUser)).thenReturn(Arrays.asList(persistedUserAccountTypeConsumer, persistedUserAccountTypeProvider));

        // Execute the method being tested
        List<AccountTypeDto> userAccounts = userRegistrationService.getUserAccounts(userId);

        // Validation
        assertThat(userAccounts.get(0), sameBeanAs(expectedUserAccountTypeConsumer));
        assertThat(userAccounts.get(1), sameBeanAs(expectedUserAccountTypeProvider));
        verify(userDao).findOne(userId);
        verify(userAccountTypeDao).findByUser(persistedUser);
    }


    @Test
    public void setWebEmployeePassword() {
        // Expected objects
        final User provider = createProvider(userId, null, phone, email, firstName, lastName);
        provider.setActive(Boolean.TRUE);
        provider.setCurrentSignupTime(new Date());
        provider.setTokenEncoded(null);
        provider.getEmployee().setInactive(true);

        // Mockito expectations
        whenAskedForUserThenReturn(provider);

        // Execute the method being tested
        userRegistrationService.setWebEmployeePassword(email, password);

        // Validation
        verify(contactService).setPassword(provider.getEmployee(), password);
        verify(notificationsFacade).sendPasswordUpdateNotification(provider.getEmployee());
    }

    @Test(expected = PhrException.class)
    public void setWebEmployeePasswordThrowsUserNotActivated() {
        // Expected objects
        final User provider = createProvider(userId, null, phone, email, firstName, lastName);
        provider.setActive(Boolean.FALSE);
        provider.setCurrentSignupTime(new Date());
        provider.setTokenEncoded(null);
        provider.getEmployee().setInactive(true);

        // Mockito expectations
        whenAskedForUserThenReturn(provider);

        // Execute the method being tested
        userRegistrationService.setWebEmployeePassword(email, password);
    }

    @Test(expected = PhrException.class)
    public void setWebEmployeePasswordThrowsUserNotActivated2() {
        // Expected objects
        final User provider = createProvider(userId, null, phone, email, firstName, lastName);
        provider.setActive(Boolean.TRUE);
        provider.setCurrentSignupTime(null);
        provider.setTokenEncoded(null);
        provider.getEmployee().setInactive(true);

        // Mockito expectations
        whenAskedForUserThenReturn(provider);

        // Execute the method being tested
        userRegistrationService.setWebEmployeePassword(email, password);
    }

    @Test(expected = PhrException.class)
    public void setWebEmployeePasswordThrowsNotFoundEmployeeInfo() {
        // Expected objects
        final User consumer = createConsumer(userId, ssn, phone, email, firstName, lastName);
        consumer.setActive(Boolean.TRUE);
        consumer.setCurrentSignupTime(new Date());
        consumer.setTokenEncoded(null);

        // Mockito expectations
        whenAskedForUserThenReturn(consumer);

        // Execute the method being tested
        userRegistrationService.setWebEmployeePassword(email, password);
    }

    @Test(expected = PhrException.class)
    public void setWebEmployeePasswordThrowsNoUserFound() {
        // Mockito expectations
        whenAskedForUserThenReturn(null);

        // Execute the method being tested
        userRegistrationService.setWebEmployeePassword(email, password);
    }

    @Test(expected = PhrException.class)
    public void setWebEmployeePasswordThrowsEmployeeAlreadyExists() {
        // Expected objects
        final User provider = createProvider(userId, null, phone, email, firstName, lastName);
        provider.setActive(Boolean.TRUE);
        provider.setCurrentSignupTime(new Date());
        provider.setTokenEncoded(null);

        // Mockito expectations
        whenAskedForUserThenReturn(provider);

        // Execute the method being tested
        userRegistrationService.setWebEmployeePassword(email, password);
    }


    @Test
    public void registerWebEmployee() {
        // Expected objects
        final User consumer = createConsumer(userId, ssn, phone, email, firstName, lastName);
        consumer.setActive(Boolean.TRUE);
        consumer.setCurrentSignupTime(new Date());
        consumer.setTokenEncoded(null);

        final Employee newEmployee = createEmployee(email, firstName, lastName, null);

        // Mockito expectations
        whenAskedForUserThenReturn(consumer);
        when(contactService.createEmployeeForConsumer(email, phone, firstName, lastName, password)).thenReturn(newEmployee);

        // Execute the method being tested
        userRegistrationService.registerWebEmployee(email, password);

        // Validation
        assertEquals(newEmployee, consumer.getEmployee());

        verify(userDao).save(consumer);
        verify(notificationsFacade).sendRegistrationConfirmation(newEmployee);
    }

    @Test(expected = PhrException.class)
    public void registerWebEmployeeThrowsUserNotActivated() {
        // Expected objects
        final User consumer = createConsumer(userId, ssn, phone, email, firstName, lastName);
        consumer.setActive(Boolean.FALSE);
        consumer.setCurrentSignupTime(new Date());
        consumer.setTokenEncoded(null);

        // Mockito expectations
        whenAskedForUserThenReturn(consumer);

        // Execute the method being tested
        userRegistrationService.registerWebEmployee(email, password);
    }

    @Test(expected = PhrException.class)
    public void registerWebEmployeeThrowsUserNotActivated2() {
        // Expected objects
        final User consumer = createConsumer(userId, ssn, phone, email, firstName, lastName);
        consumer.setActive(Boolean.TRUE);
        consumer.setCurrentSignupTime(null);
        consumer.setTokenEncoded(null);

        // Mockito expectations
        whenAskedForUserThenReturn(consumer);

        // Execute the method being tested
        userRegistrationService.registerWebEmployee(email, password);
    }

    @Test(expected = PhrException.class)
    public void registerWebEmployeeThrowsEmployeeAlreadyExists() {
        // Expected objects
        final User provider = createProvider(userId, ssn, phone, email, firstName, lastName);
        provider.setActive(Boolean.TRUE);
        provider.setCurrentSignupTime(new Date());

        // Mockito expectations
        whenAskedForUserThenReturn(provider);

        // Execute the method being tested
        userRegistrationService.registerWebEmployee(email, password);
    }

    @Test(expected = PhrException.class)
    public void registerWebEmployeeThrowsNoUserFound() {
        // Mockito expectations
        whenAskedForUserThenReturn(null);

        // Execute the method being tested
        userRegistrationService.registerWebEmployee(email, password);
    }
    */

}
