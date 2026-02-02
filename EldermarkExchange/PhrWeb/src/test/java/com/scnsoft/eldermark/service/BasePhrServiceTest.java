package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/14/2017.
 */
public class BasePhrServiceTest extends BaseServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @InjectMocks
    private BasePhrService basePhrService = new PhrService();

    // Shared test data
    private final String ssn = TestDataGenerator.randomValidSsn();
    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();
    private final String phoneNormalized = Normalizer.normalizePhone(phone);
    private final String emailNormalized = Normalizer.normalizeEmail(email);

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\nSSN: %s\nPhone: %s\nEmail: %s\n\n",
                userId, residentId, ssn, phone, email);
    }

    @Test
    public void testGetResidentIdOrThrow() {
        // Expected objects
        final User user = createConsumer(userId);

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);

        // Execute the method being tested
        Long resultId = basePhrService.getResidentIdOrThrow(userId);

        // Validation
        assertEquals(residentId, resultId);
        verify(userDao).findOne(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetResidentIdOrThrowThrowsNotFoundPatientInfo() {
        // Expected object
        final User user = User.Builder.anUser()
                .withId(userId)
                .withResident(null)
                .build();

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);

        // Execute the method being tested
        basePhrService.getResidentIdOrThrow(userId);
    }

    @Test
    public void testGetEmployeeIdOrThrow() {
        // Expected objects
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(employee)
                .build();

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);

        // Execute the method being tested
        Long resultId = basePhrService.getEmployeeIdOrThrow(userId);

        // Validation
        assertEquals(employeeId, resultId);
        verify(userDao).findOne(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetEmployeeIdOrThrowThrowsNotFoundEmployeeInfo() {
        // Expected object
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmployee(null)
                .build();

        // Mockito expectations
        when(userDao.findOne(userId)).thenReturn(user);

        // Execute the method being tested
        basePhrService.getEmployeeIdOrThrow(userId);
    }

    @Test
    public void testGetResidentIdsAsConsumer() {
        // Expected object
        final List<Long> expectedIds = Collections.singletonList(residentId);

        // Mockito expectations
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(expectedIds);

        // Execute the method being tested
        Collection<Long> resultIds = basePhrService.getResidentIds(userId);

        // Validation
        assertThat(resultIds, containsInAnyOrder(expectedIds.toArray()));
        verify(userResidentRecordsDao).getActiveResidentIdsByUserId(userId);
        verifyNoMoreInteractions(userResidentRecordsDao);
    }

    @Test
    public void testGetResidentIdsAsProvider() {
        // Expected objects
        final Long consumerUserId = TestDataGenerator.randomIdExceptOf(userId);
        final List<Long> expectedIds = Arrays.asList(residentId - 1, residentId, residentId + 1);

        // Mockito expectations
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(expectedIds);

        // Execute the method being tested
        Collection<Long> resultIds = basePhrService.getResidentIds(consumerUserId);

        // Validation
        assertThat(resultIds, containsInAnyOrder(expectedIds.toArray()));
        verify(userResidentRecordsDao).getAllResidentIdsByUserId(consumerUserId);
        verifyNoMoreInteractions(userResidentRecordsDao);
    }

    @Test
    public void testGetResidentIdsOrThrowAsConsumer() {
        // Expected object
        final List<Long> expectedIds = Collections.singletonList(residentId);

        // Mockito expectations
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(userId)).thenReturn(expectedIds);

        // Execute the method being tested
        Collection<Long> resultIds = basePhrService.getResidentIdsOrThrow(userId);

        // Validation
        assertThat(resultIds, containsInAnyOrder(expectedIds.toArray()));
        verify(userResidentRecordsDao).getActiveResidentIdsByUserId(userId);
        verifyNoMoreInteractions(userResidentRecordsDao);
    }

    @Test(expected = PhrException.class)
    public void testGetResidentIdsOrThrowThrowsNotFoundPatientInfo() {
        // Mockito expectations
        when(userResidentRecordsDao.getAllResidentIdsByUserId(any(Long.class))).thenReturn(Collections.<Long>emptyList());
        when(userResidentRecordsDao.getActiveResidentIdsByUserId(any(Long.class))).thenReturn(Collections.<Long>emptyList());

        // Execute the method being tested
        basePhrService.getResidentIdsOrThrow(userId);
    }

    @Test
    public void testValidateEmailNotExistsOrThrow() {
        // FIXME
        Database database = new Database();

        // Mockito expectations
        when(userDao.findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(database, emailNormalized)).thenReturn(null);

        // Execute the method being tested
        basePhrService.validateEmailNotExistsOrThrow(database, email, PhrExceptionType.USER_EMAIL_CONFLICT);

        // Validation
        verify(userDao).findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(database, emailNormalized);
    }

    @Test(expected = PhrException.class)
    public void testValidateEmailNotExistsOrThrowThrowsException() {
        // FIXME
        Database database = new Database();

        // Expected object
        final User user = User.Builder.anUser()
                .withId(userId)
                .withEmail(email)
                .build();

        // Mockito expectations
        when(userDao.findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(database, emailNormalized)).thenReturn(user);

        // Execute the method being tested
        basePhrService.validateEmailNotExistsOrThrow(database, email, PhrExceptionType.USER_EMAIL_CONFLICT);
    }

    @Test
    public void testGetExistingUserConsumer() {
        // Expected object
        final User expectedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .build();

        // Mockito expectations
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(expectedUser));

        // Execute the method being tested
        User result = basePhrService.getExistingUser(ssn, phone, email);

        // Validation
        assertThat(result, sameBeanAs(expectedUser));
        verify(userDao).findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized);
    }

    @Test
    public void testGetExistingUserProvider() {
        // Expected object
        final User expectedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .build();

        // Mockito expectations
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(expectedUser));

        // Execute the method being tested
        User result = basePhrService.getExistingUser((String) null, phone, email);

        // Validation
        assertThat(result, sameBeanAs(expectedUser));
        verify(userDao).findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized);
    }

    @Test
    public void testGetExistingUserWithoutSsn() {
        // Expected objects
        final User expectedUser = User.Builder.anUser()
                .withId(userId)
                .withSsn(null)
                .withPhone(phone)
                .withEmail(email)
                .build();

        // Mockito expectations
        when(userDao.findUsersByEmailAndPhoneNormalizedAndSsnIsNull(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(expectedUser));

        // Execute the method being tested
        User result = basePhrService.getExistingUserWithoutSsn(phone, email);

        // Validate
        assertThat(result, sameBeanAs(expectedUser));
        verify(userDao).findUsersByEmailAndPhoneNormalizedAndSsnIsNull(emailNormalized, phoneNormalized);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme