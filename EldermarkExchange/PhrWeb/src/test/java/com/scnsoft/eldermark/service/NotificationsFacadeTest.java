package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.*;
import com.scnsoft.eldermark.services.carecoordination.EmployeeRequestService;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationService;
import com.scnsoft.eldermark.services.sms.SmsService;
import com.scnsoft.eldermark.shared.carecoordination.service.ConfirmationEmailDto;
import com.scnsoft.eldermark.shared.carecoordination.service.InvitationDto;
import com.scnsoft.eldermark.shared.phr.PushNotificationVO;
import com.scnsoft.eldermark.shared.phr.SectionUpdateRequestVO;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.Section;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/13/2017.
 */
public class NotificationsFacadeTest extends BaseServiceTest {

    @Mock
    private ActivityService activityService;

    @Mock
    private ExchangeMailService exchangeMailService;

    @Mock
    private SmsService smsService;

    @Mock
    private PushNotificationService pushNotificationService;

    @Mock
    private EmployeeRequestService employeeRequestService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private NotificationsFacade notificationsFacade;

    // Shared test data
    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();
    private final Long code = TestDataGenerator.randomConfirmationCode();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nPhone: %s\nEmail: %s\nConfirmation code: %d\n\n",
                userId, phone, email, code);
    }

    @Test
    public void testNotifyAboutInvitationToCareTeamFromProvider() {
        // Expected objects
        final Long patientUserId = TestDataGenerator.randomIdExceptOf(userId);
        final User userPatient = createConsumer(patientUserId);
        final Employee employeeProvider = new Employee();
        employeeProvider.setStatus(EmployeeStatus.PENDING);
        employeeProvider.setDatabase(createDatabase());
        final Long providerUserId = TestDataGenerator.randomIdExceptOf(patientUserId, userId);
        final User userProvider = User.Builder.anUser()
                .withId(providerUserId)
                .withPhone(phone)
                .withEmail(email)
                .withEmployee(employeeProvider)
                .build();
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employeeCurrent = new Employee();
        employeeCurrent.setId(employeeId);
        final User userCurrent = User.Builder.anUser()
                .withId(userId)
                .withPhone(phone)
                .withEmail(email)
                .withEmployee(employeeCurrent)
                .build();
        final InvitationActivity.Status expectedInvitationActivityStatus = InvitationActivity.Status.SENT;
        final Long rctmId = TestDataGenerator.randomId();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(rctmId);
        rctm.setEmployee(employeeProvider);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(userCurrent);
        when(employeeRequestService.hasInvitationTokens(employeeProvider)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(userPatient, userProvider, employeeProvider, rctm);

        // Validation
        verify(userDao).getOne(userId);
        verify(employeeRequestService).hasInvitationTokens(employeeProvider);
        verify(employeeRequestService, never()).createInvitationToken(any(Resident.class), any(Employee.class));
        verify(employeeRequestService, never()).createInvitationToken(any(Employee.class), any(Employee.class));
        verify(pushNotificationService, never()).send(any(PushNotificationVO.class));
        verify(employeeRequestService).createInvitationToken(userPatient, employeeProvider, userCurrent);
        verify(activityService).logInvitationActivity(patientUserId, employeeProvider, expectedInvitationActivityStatus);
        verifyNoMoreInteractions(employeeRequestService, activityService);
    }

    @Test
    public void testNotifyAboutInvitationToCareTeamFromConsumer() {
        // Expected objects
        final User userPatient = createConsumer(userId);
        final Long providerUserId = TestDataGenerator.randomIdExceptOf(userId);
        final Employee employeeProvider = new Employee();
        employeeProvider.setStatus(EmployeeStatus.PENDING);
        employeeProvider.setDatabase(new Database());
        final User userProvider = User.Builder.anUser()
                .withId(providerUserId)
                .withPhone(phone)
                .withEmail(email)
                .withEmployee(employeeProvider)
                .build();
        final User userCurrent = userPatient;
        final InvitationActivity.Status expectedInvitationActivityStatus = InvitationActivity.Status.SENT;
        final Long rctmId = TestDataGenerator.randomId();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(rctmId);
        rctm.setEmployee(employeeProvider);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(userCurrent);
        when(employeeRequestService.hasInvitationTokens(employeeProvider)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(userPatient, userProvider, employeeProvider, rctm);

        // Validation
        verify(userDao).getOne(userId);
        verify(employeeRequestService).hasInvitationTokens(employeeProvider);
        verify(employeeRequestService, never()).createInvitationToken(any(Resident.class), any(Employee.class));
        verify(employeeRequestService, never()).createInvitationToken(any(Employee.class), any(Employee.class));
        verify(pushNotificationService, never()).send(any(PushNotificationVO.class));
        verify(employeeRequestService).createInvitationToken(userPatient, employeeProvider, userCurrent);
        verify(activityService).logInvitationActivity(userId, employeeProvider, expectedInvitationActivityStatus);
        verifyNoMoreInteractions(employeeRequestService, employeeRequestService);
    }

//    @Test
    public void testNotifyFriendFamilyAboutInvitationToCareTeamSendsInviteToMobileApplication() {
        // Expected objects
        final User userPatient = createConsumer(userId);
        final Employee employeeProvider = new Employee();
        employeeProvider.setStatus(EmployeeStatus.PENDING);
        employeeProvider.setDatabase(createDatabase());
        final User userCurrent = userPatient;
        final InvitationActivity.Status expectedInvitationActivityStatus = InvitationActivity.Status.ACCEPTED;

        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName("Parent / Guardian");

        final Long rctmId = TestDataGenerator.randomId();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(rctmId);
        rctm.setEmployee(employeeProvider);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCareTeamRole(careTeamRole);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(userCurrent);
        when(employeeRequestService.hasInvitationTokens(employeeProvider)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(userPatient, null, employeeProvider, rctm);

        // Validation
        verify(userDao).getOne(userId);
        verify(employeeRequestService, never()).createInvitationToken(any(Resident.class), any(Employee.class));
        verify(employeeRequestService, never()).createInvitationToken(any(Employee.class), any(Employee.class));
        verify(employeeRequestService, never()).createInvitationToken(userPatient, employeeProvider, userCurrent);
        verify(pushNotificationService, never()).send(any(PushNotificationVO.class));
        verify(exchangeMailService).sendInvitationToMobileApp(any(InvitationDto.class));
        verify(activityService).logInvitationActivity(userId, employeeProvider, expectedInvitationActivityStatus);
        verifyNoMoreInteractions(exchangeMailService, employeeRequestService, employeeRequestService);
    }

//    @Test
    public void testNotifyExistingUserAboutInvitationToCareTeamFromConsumer() {
        // Expected objects
        final User userPatient = createConsumer(userId);
        final Long providerUserId = TestDataGenerator.randomIdExceptOf(userId);
        final Employee employeeProvider = new Employee();
        employeeProvider.setStatus(EmployeeStatus.PENDING);
        employeeProvider.setDatabase(createDatabase());
        final User userProvider = User.Builder.anUser()
                .withId(providerUserId)
                .withPhone(phone)
                .withEmail(email)
                .withEmployee(employeeProvider)
                .withTokenEncoded("Q")
                .withPhrPatient(Boolean.FALSE)
                .build();
        final User userCurrent = userPatient;
        final InvitationActivity.Status expectedInvitationActivityStatus = InvitationActivity.Status.ACCEPTED;
        final String fcmToken = TestDataGenerator.randomGcmToken();

        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName("Parent / Guardian");

        final Long rctmId = TestDataGenerator.randomId();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(rctmId);
        rctm.setEmployee(employeeProvider);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCareTeamRole(careTeamRole);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(userCurrent);
        when(employeeRequestService.hasInvitationTokens(employeeProvider)).thenReturn(Boolean.FALSE);
        when(pushNotificationService.getTokens(providerUserId, PushNotificationRegistration.ServiceProvider.FCM))
                .thenReturn(Collections.singletonList(fcmToken));

        // Execute the method being tested
        notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(userPatient, userProvider, employeeProvider, rctm);

        // Validation
        verify(userDao).getOne(userId);
        verify(employeeRequestService, never()).createInvitationToken(any(Resident.class), any(Employee.class));
        verify(employeeRequestService, never()).createInvitationToken(any(Employee.class), any(Employee.class));
        verify(employeeRequestService, never()).createInvitationToken(userPatient, employeeProvider, userCurrent);
        verify(pushNotificationService).send(any(PushNotificationVO.class));
        verifyZeroInteractions(exchangeMailService);
        verify(activityService).logInvitationActivity(userId, employeeProvider, expectedInvitationActivityStatus);
        verifyNoMoreInteractions(employeeRequestService, employeeRequestService);
    }

    @Test
    public void testNotifyAboutInvitationToCareTeamSendsNothing() {
        // Expected objects
        final User userPatient = createConsumer(userId);
        final Long providerUserId = TestDataGenerator.randomIdExceptOf(userId);
        final Employee employeeProvider = new Employee();
        employeeProvider.setStatus(EmployeeStatus.PENDING);
        employeeProvider.setDatabase(createDatabase());
        final User userProvider = User.Builder.anUser()
                .withId(providerUserId)
                .withPhone(phone)
                .withEmail(email)
                .withEmployee(employeeProvider)
                .withTokenEncoded(null)
                .withPhrPatient(Boolean.FALSE)
                .build();
        final User userCurrent = userPatient;
        final InvitationActivity.Status expectedInvitationActivityStatus = InvitationActivity.Status.SENT;

        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName("Parent / Guardian");

        final Long rctmId = TestDataGenerator.randomId();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(rctmId);
        rctm.setEmployee(employeeProvider);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCareTeamRole(careTeamRole);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(userCurrent);
        when(employeeRequestService.hasInvitationTokens(employeeProvider)).thenReturn(Boolean.TRUE);
        when(pushNotificationService.getTokens(providerUserId, PushNotificationRegistration.ServiceProvider.FCM))
                .thenReturn(Collections.<String>emptyList());

        // Execute the method being tested
        notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(userPatient, userProvider, employeeProvider, rctm);

        // Validation
        verify(userDao).getOne(userId);
        verify(employeeRequestService).hasInvitationTokens(employeeProvider);
        verify(employeeRequestService, never()).createInvitationToken(any(Resident.class), any(Employee.class));
        verify(employeeRequestService, never()).createInvitationToken(any(Employee.class), any(Employee.class));
        verify(employeeRequestService, never()).createInvitationToken(userPatient, employeeProvider, userCurrent);
        verify(pushNotificationService, never()).send(any(PushNotificationVO.class));
        verifyZeroInteractions(exchangeMailService);
        verify(activityService).logInvitationActivity(userId, employeeProvider, expectedInvitationActivityStatus);
        verifyNoMoreInteractions(employeeRequestService, employeeRequestService);
    }

    @Test
    public void testNotifyAboutInvitationToCareTeamPhysician() {
        // Expected objects
        final User userPatient = createConsumer(userId);
        final Long providerUserId = TestDataGenerator.randomIdExceptOf(userId);
        final Employee employeeProvider = new Employee();
        employeeProvider.setStatus(EmployeeStatus.PENDING);
        employeeProvider.setDatabase(createDatabase());
        final User userProvider = User.Builder.anUser()
                .withId(providerUserId)
                .withPhone(phone)
                .withEmail(email)
                .withEmployee(employeeProvider)
                .build();
        final User userCurrent = userPatient;
        final InvitationActivity.Status expectedInvitationActivityStatus = InvitationActivity.Status.SENT;

        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_BEHAVIORAL_HEALTH;
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName(BEHAVIORAL_HEALTH);

        final Long rctmId = TestDataGenerator.randomId();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(rctmId);
        rctm.setEmployee(employeeProvider);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCareTeamRole(careTeamRole);

        // Mockito expectations
        when(userDao.getOne(userId)).thenReturn(userCurrent);
        when(employeeRequestService.hasInvitationTokens(employeeProvider)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        notificationsFacade.notifyMedicalStaffAboutInvitationToCareTeam(userPatient, userProvider, rctm);

        // Validation
        verify(userDao).getOne(userId);
        verify(exchangeMailService).sendInvitationToCareTeam(any(InvitationDto.class));
        verify(activityService).logInvitationActivity(userId, employeeProvider, expectedInvitationActivityStatus);
        verifyNoMoreInteractions(employeeRequestService, exchangeMailService, activityService);
    }

    @Test
    public void testConfirmUserRegistration() {
        // Expected object
        final User user = User.Builder.anUser()
                .withId(userId)
                .withPhone(phone)
                .withEmail(email)
                .withEmployee(null)
                .build();
        final RegistrationApplication application = new RegistrationApplication();
        application.setUser(user);
        application.setPhone(phone);
        application.setPhoneConfirmationCode(String.valueOf(code));

        // Execute the method being tested
        notificationsFacade.confirmUserRegistration(application);

        // Validation
        verify(smsService, only()).sendSmsNotification(eq(phone), contains(code.toString()));
    }

    @Test
    public void testSendSectionUpdateRequest() {
        // Expected objects
        final String creatorName = TestDataGenerator.randomFullName();
        final String residentName = TestDataGenerator.randomFullName();

        // Execute the method being tested
        notificationsFacade.sendSectionUpdateRequest(email, creatorName, residentName, Section.IMMUNIZATIONS, "time to change",
                SectionUpdateRequest.Type.UPDATE, Collections.<MultipartFile>emptyList());

        // Validation
        verify(exchangeMailService).sendUpdateSectionDataRequest(any(SectionUpdateRequestVO.class));
        verifyNoMoreInteractions(exchangeMailService);
    }

    @Test
    public void testSendPasswordUpdateNotification() {
        // Expected objects
        final ConfirmationEmailDto passwordUpdateDto = new ConfirmationEmailDto();
        final Employee employee = new Employee();

        // Mockito expectations
        when(employeeRequestService.createConfirmationEmailDto(employee)).thenReturn(passwordUpdateDto);
        when(exchangeMailService.sendPasswordUpdateNotification(passwordUpdateDto)).thenReturn(new AsyncResult<>(true));

        // Execute the method being tested
        notificationsFacade.sendPasswordUpdateNotification(employee);

        // Validation
        verify(exchangeMailService).sendPasswordUpdateNotification(passwordUpdateDto);
    }

    @Test
    public void testSendRegistrationConfirmation() {
        final ConfirmationEmailDto confirmationDto = new ConfirmationEmailDto();
        final Employee employee = new Employee();

        // Mockito expectations
        when(employeeRequestService.createConfirmationEmailDto(employee)).thenReturn(confirmationDto);
        when(exchangeMailService.sendConfirmation(confirmationDto)).thenReturn(new AsyncResult<>(true));

        // Execute the method being tested
        notificationsFacade.sendRegistrationConfirmation(employee);

        // Validation
        verify(exchangeMailService).sendConfirmation(confirmationDto);
    }

}
