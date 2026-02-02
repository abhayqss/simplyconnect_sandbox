package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.password.PasswordHistoryDao;
import com.scnsoft.eldermark.dao.phr.ActivityDao;
import com.scnsoft.eldermark.dao.carecoordination.CareTeamMemberDao;
import com.scnsoft.eldermark.dao.carecoordination.EmployeeRequestDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserResidentRecord;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.services.password.PasswordHistoryService;
import com.scnsoft.eldermark.services.phr.InvitationActivityService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationType;
import com.scnsoft.eldermark.shared.carecoordination.contacts.NewAccountLinkedDto;
import com.scnsoft.eldermark.shared.carecoordination.service.ConfirmationEmailDto;
import com.scnsoft.eldermark.shared.carecoordination.service.InvitationDto;
import com.scnsoft.eldermark.shared.carecoordination.service.ResetPasswordDto;
import com.scnsoft.eldermark.shared.carecoordination.service.ResetPasswordMailDto;
import com.scnsoft.eldermark.shared.phr.PushNotificationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.*;

/**
 * @author averazub
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 03-Nov-15.
 */
@Service
public class EmployeeRequestServiceImpl implements EmployeeRequestService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRequestServiceImpl.class);
    private static final StandardPasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    public static final String MSG_PASSWORDS_DONT_MATCH = "Passwords don't match.";
    public static final String MSG_TRYING_TO_DELETE_NONEXISTING_INVITE = "Trying to delete non-existing invite request with token ";
    private static final String MSG_EMPLOYEE_ALREADY_ACTIVATED = "Trying to delete invite request for employee that is already activated. Request token is ";

    @Value("${invite.url}")
    private String inviteUrl;
    @Value("${reset.password.url}")
    private String resetPasswordUrl;

    @Value("${portal.url}")
    private String portalUrl;
    @Value("${link.url}")
    private String linkUrl;

    @Autowired
    private EmployeeRequestDao employeeRequestDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private CareTeamMemberDao careTeamMemberDao;

    @Autowired
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    InvitationActivityService invitationActivityService;

    @Autowired
    ActivityDao activityDao;

    @Autowired
    UserResidentRecordsDao userResidentRecordsDao;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    PasswordHistoryService passwordHistoryService;

    @Autowired
    EmployeePasswordSecurityService employeePasswordSecurityService;

    @Override
    public void createInvitationTokenForAutoCreated(final Database database, final Employee target) {
        final EmployeeRequest employeeRequest = prepareInviteEmployeeRequest();
        employeeRequest.setTargetEmployee(target);

        employeeRequestDao.create(employeeRequest);

        exchangeMailService.sendInvitationAuto(createInvitationAutoCreatedDto(database, employeeRequest));
    }

    @Override
    public void createInvitationToken(final Employee creator, final Employee target) {
        final EmployeeRequest employeeRequest = prepareInviteEmployeeRequest();
        employeeRequest.setTargetEmployee(target);
        employeeRequest.setCreatedEmployee(creator);

        employeeRequestDao.create(employeeRequest);

        exchangeMailService.sendInvitation(createInvitationDto(employeeRequest));
    }

    @Override
    public void createInvitationToken(final Resident creator, Employee target) {
        final EmployeeRequest employeeRequest = prepareInviteEmployeeRequest();
        employeeRequest.setTargetEmployee(target);
        employeeRequest.setCreatedResident(creator);

        employeeRequestDao.create(employeeRequest);

        exchangeMailService.sendInvitation(createInvitationDto(employeeRequest));
    }

    @Override
    public void createInvitationToken(final User patient, Employee providerEmployee, User current) {
        final EmployeeRequest employeeRequest = prepareInviteEmployeeRequest();
        employeeRequest.setTargetEmployee(providerEmployee);
        if (current.getEmployee() != null) {
            employeeRequest.setCreatedEmployee(current.getEmployee());
        } else {
            // legacy: after changes in SCPAPP-208 all registered users should have an associated employee
            employeeRequest.setCreatedResident(current.getResident());
        }

        employeeRequestDao.create(employeeRequest);

        exchangeMailService.sendInvitationToCareTeamNewUser(createInvitationDto(employeeRequest, patient));
    }

    @Override
    public boolean hasInvitationTokens(Employee target) {
        return employeeRequestDao.existsByTargetEmployee(target, EmployeeRequestType.INVITE);
    }

    private static EmployeeRequest prepareInviteEmployeeRequest() {
        final EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setCreatedDateTime(new Date());
        employeeRequest.setTokenType(EmployeeRequestType.INVITE);
        employeeRequest.setToken(UUID.randomUUID().toString());
        return employeeRequest;
    }

    @Override
    public void createResetPasswordToken(final Employee employee) {
        final EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setCreatedDateTime(new Date());
        employeeRequest.setTargetEmployee(employee);
        employeeRequest.setCreatedEmployee(employee);
        employeeRequest.setTokenType(EmployeeRequestType.RESET_PASSWORD);
        employeeRequest.setToken(UUID.randomUUID().toString());

        employeeRequestDao.create(employeeRequest);

        exchangeMailService.sendResetPassword(createResetPasswordMailDto(employeeRequest));
    }

    @Override
    public void declineInvitation(EmployeeRequest employeeRequest) {
        if (employeeRequest.getTargetEmployee().getStatus().equals(EmployeeStatus.ACTIVE)) {
            //throw new RuntimeException(MSG_EMPLOYEE_ALREADY_ACTIVATED + employeeRequest.getToken());
            logger.error(MSG_EMPLOYEE_ALREADY_ACTIVATED + employeeRequest.getToken());
        }
        else {
            employeeRequestDao.delete(employeeRequest);
            careTeamMemberDao.deleteCareTeamMembersForEmployee(employeeRequest.getTargetEmployee());
            activityDao.deleteByEmployee(employeeRequest.getTargetEmployee());
            employeeDao.delete(employeeRequest.getTargetEmployee());
            employeeDao.flush();
        }
    }

    @Override
    public void expireInvitation(EmployeeRequest employeeRequest) {
        if (employeeRequest.getTargetEmployee().getStatus().equals(EmployeeStatus.ACTIVE)) {
            //throw new RuntimeException(MSG_EMPLOYEE_ALREADY_ACTIVATED + employeeRequest.getToken());
            logger.error(MSG_EMPLOYEE_ALREADY_ACTIVATED + employeeRequest.getToken());
        }
        else {
//            employeeRequestDao.delete(employeeRequest);
//            careTeamMemberDao.deleteCareTeamMembersForEmployee(employeeRequest.getTargetEmployee());
//            activityDao.deleteByEmployee(employeeRequest.getTargetEmployee());
            Employee employee = employeeRequest.getTargetEmployee();
            employee.setStatus(EmployeeStatus.EXPIRED);
            employeeDao.merge(employee);
//            employeeDao.flush();
        }
    }


    @Override
    public void sendNewInvitation(ResetPasswordDto dto) {
        EmployeeRequest employeeRequest =  getInviteToken(dto.getToken());
//        sendNewInvitation(employeeRequest);
        employeeRequestDao.delete(employeeRequest);
        Employee employee = employeeRequest.getTargetEmployee();
        createInvitationToken(employeeRequest.getCreatedEmployee(), employee);
        employee.setStatus(EmployeeStatus.PENDING);
        employeeDao.merge(employee);
    }

    @Override
    public void sendNewInvitation(Long contactId) {

//        sendNewInvitation(employeeRequest);
        Employee employee = employeeDao.get(contactId);
        EmployeeRequest employeeRequest = null;
        try {
             employeeRequest = employeeRequestDao.getByTargetEmployee(employee,EmployeeRequestType.INVITE);
        } catch (Exception e) {
            logger.warn("No EmployeeRequest found for contactId" + contactId);
        }
        Employee createdEmployee = null;
        if (employeeRequest != null) {
            createdEmployee = employeeRequest.getCreatedEmployee();
            employeeRequestDao.delete(employeeRequest);
        }
        else {
            createdEmployee = SecurityUtils.getAuthenticatedUser().getEmployee();
        }
        createInvitationToken(createdEmployee, employee);
        employee.setStatus(EmployeeStatus.PENDING);
        employeeDao.merge(employee);
    }

//    private void sendNewInvitation(EmployeeRequest employeeRequest) {
//        employeeRequestDao.delete(employeeRequest);
//        Employee employee = employeeRequest.getTargetEmployee();
//        createInvitationToken(employeeRequest.getCreatedEmployee(), employee);
//        employee.setStatus(EmployeeStatus.PENDING);
//        employeeDao.merge(employee);
//    }


    @Override
    public ResetPasswordDto createInviteDto(final String token) {
        final ResetPasswordDto result = new ResetPasswordDto(token);
        final EmployeeRequest employeeRequest = employeeRequestDao.getByToken(token, EmployeeRequestType.INVITE);
        result.setCreatorName(getCreatorFullName(employeeRequest));
        if (employeeRequest.getTargetEmployee().getDatabase().getSystemSetup()!=null) {
            result.setOrganizationCode(employeeRequest.getTargetEmployee().getDatabase().getSystemSetup().getLoginCompanyId());
        }
        return result;
    }

    @Override
    public ResetPasswordDto createResetPasswordDto(final String token) {
        final ResetPasswordDto result = new ResetPasswordDto(token);
        final EmployeeRequest employeeRequest = employeeRequestDao.getByToken(token, EmployeeRequestType.RESET_PASSWORD);
        result.setCreatorName(getCreatorFullName(employeeRequest));
        if (employeeRequest.getTargetEmployee().getDatabase().getSystemSetup()!=null) {
            result.setOrganizationCode(employeeRequest.getTargetEmployee().getDatabase().getSystemSetup().getLoginCompanyId());
        }
        return result;
    }

    @Override
    public NewAccountLinkedDto createNewAccountLinkedDto(String token) {
        final NewAccountLinkedDto result = new NewAccountLinkedDto(token);
        final EmployeeRequest employeeRequest = employeeRequestDao.getByToken(token, EmployeeRequestType.INVITE);
        result.setCreatorName(getCreatorFullName(employeeRequest));
        if (employeeRequest.getTargetEmployee().getDatabase().getSystemSetup()!=null) {
            result.setOrganizationCode(employeeRequest.getTargetEmployee().getDatabase().getSystemSetup().getLoginCompanyId());
        }
        result.setLogin(employeeRequest.getTargetEmployee().getLoginName());
        if (employeeRequest.getTargetEmployee().getCareTeamRole() != null) {
            result.setRole(employeeRequest.getTargetEmployee().getCareTeamRole().getName());
        }
        result.setOrganization(employeeRequest.getTargetEmployee().getDatabase().getName());
        result.setDatabaseId(employeeRequest.getTargetEmployee().getDatabase().getId());
        return result;
    }

    private static String getCreatorFullName(EmployeeRequest employeeRequest) {
        String fullName = null;
        if (employeeRequest.getCreatedEmployee() != null) {
            fullName = employeeRequest.getCreatedEmployee().getFullName();
        } else if (employeeRequest.getCreatedResident() != null) {
            fullName = employeeRequest.getCreatedResident().getFullName();
        }
        return fullName;
    }

    @Override
    public Employee useInviteToken(final ResetPasswordDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException(MSG_PASSWORDS_DONT_MATCH);
        }

        final EmployeeRequest employeeRequest = employeeRequestDao.getByToken(dto.getToken(), EmployeeRequestType.INVITE);
        final Employee targetEmployee = employeeRequest.getTargetEmployee();

        targetEmployee.setStatus(EmployeeStatus.ACTIVE);
        targetEmployee.setPassword(passwordEncoder.encode(dto.getPassword()));

        ConfirmationEmailDto confirmationEmailDto = createConfirmationEmailDto(targetEmployee);
        employeeRequestDao.delete(employeeRequest);
        Employee result = employeeDao.merge(targetEmployee);
        employeeDao.flush();

        passwordHistoryService.addCurrentPasswordToHistoryIfEnabled(result);
        employeePasswordSecurityService.updatePasswordChangedTimeIfEnabled(result);
        invitationActivityService.logInvitationAcceptedActivity(result);
        sendPushNotificationsToCareReceivers(result);
        exchangeMailService.sendConfirmation(confirmationEmailDto);
        return result;
    }

    @Override
    @Transactional
    public void declineInviteRequest(final String token) {
        try {
            logger.info("Deleting request by token " + token);
            final EmployeeRequest employeeRequest = employeeRequestDao.getByToken(token, EmployeeRequestType.INVITE);
            declineInvitation(employeeRequest);
        } catch (final NoResultException e) {
            logger.info(MSG_TRYING_TO_DELETE_NONEXISTING_INVITE + token);
        }

    }

    @Override
    public void useResetPasswordToken(ResetPasswordDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException(MSG_PASSWORDS_DONT_MATCH);
        }

        final EmployeeRequest employeeRequest = employeeRequestDao.getByToken(dto.getToken(), EmployeeRequestType.RESET_PASSWORD);
        final Employee targetEmployee = employeeRequest.getTargetEmployee();

        targetEmployee.setPassword(passwordEncoder.encode(dto.getPassword()));

        employeeRequestDao.delete(employeeRequest);
        Employee result = employeeDao.merge(targetEmployee);
        employeeDao.flush();
        employeePasswordSecurityService.unlockEmployeeAccount(result.getId());
        passwordHistoryService.addCurrentPasswordToHistoryIfEnabled(result);
        employeePasswordSecurityService.updatePasswordChangedTimeIfEnabled(result);
    }

    @Override
    @Transactional
    public void declineResetPasswordToken(String token) {
        try {
            logger.info("Deleting request by token " + token);
            final EmployeeRequest employeeRequest = employeeRequestDao.getByToken(token, EmployeeRequestType.RESET_PASSWORD);
            employeeRequestDao.delete(employeeRequest);

        } catch (final NoResultException e) {
            logger.info(MSG_TRYING_TO_DELETE_NONEXISTING_INVITE + token);
        }

    }

    @Override
    public EmployeeRequest getInviteToken(String token) {
        return employeeRequestDao.getByToken(token, EmployeeRequestType.INVITE);
    }

    @Override
    public void deleteInvitations(Employee employee) {
        employeeRequestDao.deleteByEmployee(employee);
    }

    @Override
    public EmployeeRequest getResetToken(String token) {
        return employeeRequestDao.getByToken(token, EmployeeRequestType.RESET_PASSWORD);
    }

    @Override
    public ConfirmationEmailDto createConfirmationEmailDto(final Employee employee) {
        final ConfirmationEmailDto result = new ConfirmationEmailDto();
        if (employee.getDatabase().getSystemSetup()!=null) {
            result.setCompanyName(employee.getDatabase().getSystemSetup().getLoginCompanyId());
        }
        result.setLogin(employee.getLoginName());
        result.setPortalUrl(portalUrl);
        result.setResetPasswordUrl(portalUrl + "service/resetRequest");
        result.setUser(employee.getFullName());
        result.setToEmail(employee.getLoginName());

        return result;
    }


    protected ResetPasswordMailDto createResetPasswordMailDto(final EmployeeRequest employeeRequest) {
        final ResetPasswordMailDto result = new ResetPasswordMailDto();

        result.setToEmail(employeeRequest.getTargetEmployee().getLoginName());
        result.setUrl(generateResetPasswordUrl(employeeRequest.getToken()));

        return result;

    }

    protected InvitationDto createInvitationDto(final EmployeeRequest employeeRequest) {
        final InvitationDto result = new InvitationDto();

        result.setCreator(getCreatorFullName(employeeRequest));
        result.setToEmail(employeeRequest.getTargetEmployee().getLoginName());

        result.setTarget(employeeRequest.getTargetEmployee().getFullName());
        result.setUrl(generateInviteUrl(employeeRequest.getToken()));
        result.setLinkUrl(generateLinkUrl(employeeRequest.getToken()));
        result.setPortalUrl(portalUrl);

        return result;

    }

    protected InvitationDto createInvitationAutoCreatedDto(final Database database, final EmployeeRequest employeeRequest) {
        final InvitationDto result = new InvitationDto();

        result.setToEmail(employeeRequest.getTargetEmployee().getLoginName());

        result.setUrl(generateInviteUrl(employeeRequest.getToken()));
        result.setPortalUrl(portalUrl);

        return result;

    }

    private InvitationDto createInvitationDto(final EmployeeRequest employeeRequest, final User patient) {
        final InvitationDto result = createInvitationDto(employeeRequest);
        result.setCareReceiver(patient.getResidentFullNameLegacy());
        return result;
    }

    private void sendPushNotificationsToCareReceivers(Employee employee) {
        final List<ResidentCareTeamMember> rctms = residentCareTeamMemberDao.getCareTeamMembersByEmployeeIds(Collections.singleton(employee.getId()), null);
        final Map<Long, ResidentCareTeamMember> residentIdToCtmMap = new HashMap<Long, ResidentCareTeamMember>();
        for (ResidentCareTeamMember rctm : rctms) {
            residentIdToCtmMap.put(rctm.getResidentId(), rctm);
        }
        if (residentIdToCtmMap.size()>0) {
            final List<UserResidentRecord> careReceiverRecords = userResidentRecordsDao.getAllByResidentIdIn(residentIdToCtmMap.keySet());

            for (UserResidentRecord careReceiver : careReceiverRecords) {
                pushNotificationService.send(createPushNotificationVOtoCareReceivers(careReceiver, residentIdToCtmMap.get(careReceiver.getResidentId())));
            }
        }
    }

    private PushNotificationVO createPushNotificationVOtoCareReceivers(UserResidentRecord record, ResidentCareTeamMember rctm) {
        final Collection<String> tokens = pushNotificationService.getTokens(record.getUserId(), PushNotificationRegistration.ServiceProvider.FCM);

        final Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("id", PushNotificationType.RESULT_OF_PROVIDERS_INVITATION.getNotificationId());
        payload.put("userId", record.getUserId());
        payload.put("contactId", rctm.getId());
        payload.put("careTeamRole", rctm.getCareTeamRole().getName());
        payload.put("invitationStatus", InvitationStatus.ACTIVE);

        final PushNotificationVO result = new PushNotificationVO();
        result.setTokens(new ArrayList<String>(tokens));
        result.setTitle("Provider accepted your invitation");
        result.setText("The Provider is a part of your care team. You can share your health data with him/her.");
        result.setPayload(payload);
        result.setServiceProvider(PushNotificationRegistration.ServiceProvider.FCM);

        return result;
    }

    protected String generateResetPasswordUrl(final String token) {
        return resetPasswordUrl + token;
    }

    protected String generateInviteUrl(final String token) {
        return inviteUrl + token;
    }

    protected String generateLinkUrl(final String token) {
        return linkUrl + token;
    }
}
