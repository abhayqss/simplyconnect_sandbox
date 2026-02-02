package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.*;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.services.carecoordination.EmployeeRequestService;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import com.scnsoft.eldermark.services.phr.UserService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationType;
import com.scnsoft.eldermark.services.sms.SmsService;
import com.scnsoft.eldermark.shared.carecoordination.service.ConfirmationEmailDto;
import com.scnsoft.eldermark.shared.carecoordination.service.InvitationDto;
import com.scnsoft.eldermark.shared.phr.PushNotificationVO;
import com.scnsoft.eldermark.shared.phr.SectionUpdateRequestVO;
import com.scnsoft.eldermark.web.entity.ContactStatus;
import com.scnsoft.eldermark.web.entity.Section;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * @author phomal
 * Created on 5/31/2017.
 */
@Service
public class NotificationsFacade extends BasePhrService {

    private Logger logger = Logger.getLogger(NotificationsFacade.class.getName());

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private EmployeeRequestService employeeRequestService;

    @Autowired
    private UserService userService;

    @Value("${portal.url}")
    private String portalUrl;

    public ContactStatus notifyFriendFamilyAboutInvitationToCareTeam(User userPatient, User userProvider, Employee userProviderEmployee, ResidentCareTeamMember rctm) {
        User currentUser = userDao.getOne(PhrSecurityUtils.getCurrentUserId());
        ContactStatus status = ContactStatus.EXISTING_ACTIVE;

        if (userProviderEmployee.getStatus().equals(EmployeeStatus.PENDING)) {
            // limit the number of emails sent to the same user (only one invitation email per 72 hours)
            if (!employeeRequestService.hasInvitationTokens(userProviderEmployee)) {
                // New employee (inactive) was created for the invited user
                // Email invitation to Web SimplyConnect
                employeeRequestService.createInvitationToken(userPatient, userProviderEmployee, currentUser);
                status = ContactStatus.CREATED;
            } else {
                status = ContactStatus.EXISTING_PENDING;
            }
        } else if (userProvider == null) {
            // New mobile user (inactive) was created for the invited user that already has an active web account
            // Email invitation to install mobile application
            final InvitationDto invitation = new InvitationDto();
            invitation.setTarget(userProviderEmployee.getFullName());
            invitation.setCareReceiver(userPatient.getResidentFullNameLegacy());
            final String providerEmail = PersonService.getPersonTelecomValue(userProviderEmployee.getPerson(), PersonTelecomCode.EMAIL);
            invitation.setToEmail(providerEmail);
            exchangeMailService.sendInvitationToMobileApp(invitation);
        } else if (userService.isActiveMobileUser(userProvider.getId())) {
            // Existing mobile user found
            // Push notification
            final PushNotificationVO invitation = createPushNotificationInvitation(userProvider, rctm, PushNotificationType.INVITATION_TO_CT_FRIEND_FAMILY);
            pushNotificationService.send(invitation);
        }

        InvitationActivity.Status invitationStatus;
        if (userProviderEmployee.getStatus().equals(EmployeeStatus.PENDING)) {
            invitationStatus = InvitationActivity.Status.SENT;
        } else {
            invitationStatus = InvitationActivity.Status.ACCEPTED;
        }
        // In-app activity
        activityService.logInvitationActivity(userPatient.getId(), userProviderEmployee, invitationStatus);

        return status;
    }

    public void notifyMedicalStaffAboutInvitationToCareTeam(User userPatient, User userProvider, ResidentCareTeamMember rctm) {
        User currentUser = userDao.getOne(PhrSecurityUtils.getCurrentUserId());

        // Email invitation to Care team
        exchangeMailService.sendInvitationToCareTeam(createCareTeamInvitationVO(userPatient, userProvider, currentUser));
        // Push notification
        final PushNotificationVO invitation = createPushNotificationInvitation(userProvider, rctm, PushNotificationType.INVITATION_TO_CT_MEDICAL_STAFF);
        pushNotificationService.send(invitation);

        // In-app activity. Invitation status is always SENT until the request is accepted / declined
        activityService.logInvitationActivity(userPatient.getId(), userProvider.getEmployee(), InvitationActivity.Status.SENT);
    }

    InvitationDto createCareTeamInvitationVO(User userPatient, User userProvider, User currentUser) {
        InvitationDto result = new InvitationDto();
        result.setCreator(currentUser.getEmployeeFullName());
        String careReceiverName;
        if (userPatient == currentUser) {
            careReceiverName = "his/her";
        } else {
            careReceiverName = userPatient.getResidentFullNameLegacy() + "'s";
        }
        result.setCareReceiver(careReceiverName);
        result.setTarget(userProvider.getEmployeeFullName());
        result.setToEmail(userProvider.getEmployeeEmail());
        // TODO accept / decline invitation url
        result.setUrl("https://google.com");
        return result;
    }

    private PushNotificationVO createPushNotificationInvitation(User invitee, ResidentCareTeamMember rctm, PushNotificationType pushNotificationType) {
        final Collection<String> tokens = pushNotificationService.getTokens(invitee.getId(), PushNotificationRegistration.ServiceProvider.FCM);

        final Map<String, Object> payload = new HashMap<>();
        payload.put("id", pushNotificationType.getNotificationId());
        payload.put("userId", invitee.getId());
        payload.put("contactId", rctm.getId());
        payload.put("careTeamRole", rctm.getCareTeamRole().getName());

        final PushNotificationVO result = new PushNotificationVO();
        result.setTokens(new ArrayList<>(tokens));
        if (pushNotificationType.equals(PushNotificationType.INVITATION_TO_CT_FRIEND_FAMILY)) {
            result.setTitle("Invitation to a care team");
        } else if (pushNotificationType.equals(PushNotificationType.INVITATION_TO_CT_MEDICAL_STAFF)) {
            result.setTitle("New invitation");
        } else {
            logger.severe(pushNotificationType + " not supported!");
        }
        result.setText("You've been invited to be a part of the care team.");
        result.setPayload(payload);
        result.setServiceProvider(PushNotificationRegistration.ServiceProvider.FCM);

        return result;
    }

    public Future<Boolean> confirmUserRegistration(RegistrationApplication application) {
        // Send code via SMS
        String text = "Your PHR app verification code: " + application.getPhoneConfirmationCode();
        return smsService.sendSmsNotification(application.getPhone(), text);
    }

    public Future<Boolean> sendSectionUpdateRequest(String email, String creatorName, String residentFullName, Section section, String comment,
                                                    SectionUpdateRequest.Type requestType, List<MultipartFile> files) {
        final SectionUpdateRequestVO request = new SectionUpdateRequestVO();
        request.setCreator(creatorName);
        request.setTarget(residentFullName);
        request.setComment(comment);
        request.setSection(getReadableNameOf(section));
        request.setType(getReadableNameOf(requestType));
        request.setToEmail(email);
        request.setAttachments(files);
        // TODO Use community SECURE email, this request contains patient's sensitive data!
        return exchangeMailService.sendUpdateSectionDataRequest(request);
    }

    private String getReadableNameOf(SectionUpdateRequest.Type requestType) {
        switch (requestType) {
            case ADD_NEW:
                return "Add New Information";
            case UPDATE:
                return "Update Existing Information";
            case DELETE:
                return "Delete Information";
            default:
                logger.warning("Section Update Request type not implemented : " + requestType.toString());
                return requestType.toString();
        }
    }

    private String getReadableNameOf(Section section) {
        switch (section) {
            case VITAL_SIGNS:
                return "Vital Signs";
            default:
                return StringUtils.capitalize(section.toString());
        }
    }

    public Future<Boolean> sendPasswordUpdateNotification(Employee employee) {
        final ConfirmationEmailDto passwordUpdateDto = employeeRequestService.createConfirmationEmailDto(employee);
        return exchangeMailService.sendPasswordUpdateNotification(passwordUpdateDto);
    }

    public Future<Boolean> sendRegistrationConfirmation(Employee employee) {
        final ConfirmationEmailDto confirmationDto = employeeRequestService.createConfirmationEmailDto(employee);
        return exchangeMailService.sendConfirmation(confirmationDto);
    }

}
