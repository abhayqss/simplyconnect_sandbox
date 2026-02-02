package com.scnsoft.eldermark.service;

import com.google.common.collect.Lists;
import com.scnsoft.eldermark.dao.CommunityCareTeamMemberDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.ReferralRequestNotificationDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.referral.*;
import com.scnsoft.eldermark.service.notification.sender.ReferralNotificationSender;
import com.scnsoft.eldermark.util.StreamUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReferralNotificationServiceImpl implements ReferralNotificationService {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private CommunityCareTeamMemberDao communityCareTeamMemberDao;

    @Autowired
    private ReferralRequestNotificationDao referralRequestNotificationDao;

    @Autowired
    private ReferralNotificationSender referralNotificationSender;

    @Autowired
    private ExternalEmployeeRequestService externalEmployeeRequestService;

    @Autowired
    private ExternalEmployeeInboundReferralCommunityService externalEmployeeInboundReferralCommunityService;

    @Autowired
    private EmployeeService employeeService;


    @Override
    public void sendSubmitNotifications(Referral referral) {
        send(prepareSubmitNotification(referral));
    }

    @Override
    public void sendPreAdmitNotification(ReferralRequest referralRequest) {
        ReferralRequestNotification notification = createNotification(
                referralRequest.getReferral().getRequestingEmployee(), referralRequest, ReferralRequestNotificationType.PRE_ADMITTED, false);
        notification = referralRequestNotificationDao.save(notification);
        List<ReferralRequestNotification> notifications = prepareNotAvailableNotification(referralRequest.getReferral());
        notifications.add(notification);
        send(notifications);
    }

    @Override
    public void sendAcceptedNotification(ReferralRequest referralRequest) {
        ReferralRequestNotification notification = createNotification(
                referralRequest.getReferral().getRequestingEmployee(), referralRequest, ReferralRequestNotificationType.ACCEPTED, false);
        notification = referralRequestNotificationDao.save(notification);
        send(notification);
    }

    @Override
    public void sendDeclinedNotification(ReferralRequest referralRequest, ReferralStatus referralStatus) {
        ReferralRequestNotification notification = createNotification(
                referralRequest.getReferral().getRequestingEmployee(), referralRequest, ReferralRequestNotificationType.DECLINED, false);
        notification = referralRequestNotificationDao.save(notification);
        List<ReferralRequestNotification> notificationsList = Lists.newArrayList(notification);
        if (referralStatus.equals(ReferralStatus.PRE_ADMIT)) {
            notificationsList.addAll(prepareSubmitNotification(referralRequest.getReferral()));
        }
        send(notificationsList);
    }

    @Override
    public void sendCanceledNotification(Referral referral) {
        List<ReferralRequest> filteredList = referral.getReferralRequests()
                .stream()
                .filter(referralRequest -> (
                        referralRequest.getLastResponse() == null))
                .collect(Collectors.toList());
        List<ReferralRequestNotification> notificationsList = createNotifications(filteredList, ReferralRequestNotificationType.CANCELED);
        List<ReferralRequestNotification> notificationsForSend = referralRequestNotificationDao.saveAll(notificationsList);
        send(notificationsForSend);
    }


    @Override
    public void sendInfoReqNotification(ReferralInfoRequest referralInfoRequest) {
        ReferralRequestNotification notification = createNotification(
                referralInfoRequest.getReferralRequest().getReferral().getRequestingEmployee(), referralInfoRequest
                        .getReferralRequest(), ReferralRequestNotificationType.INFO_REQUESTED, false);
        notification.setReferralInfoRequest(referralInfoRequest);
        notification = referralRequestNotificationDao.save(notification);
        send(notification);
    }

    @Override
    public void sendReplyInfoReqNotification(ReferralInfoRequest referralInfoRequest) {
        ReferralRequestNotification notification = createNotification(
                referralInfoRequest.getRequesterEmployee(), referralInfoRequest.getReferralRequest(), ReferralRequestNotificationType.INFO_REPLIED, false);
        notification.setReferralInfoRequest(referralInfoRequest);
        notification = referralRequestNotificationDao.save(notification);
        send(notification);
    }

    @Override
    public void sendAssignedToYouNotification(ReferralRequest referralRequest) {
        ReferralRequestNotification notification = createNotification(
                referralRequest.getAssignedEmployee(), referralRequest, ReferralRequestNotificationType.ASSIGN, false);
        notification = referralRequestNotificationDao.save(notification);
        send(notification);
    }

    private List<ReferralRequestNotification> prepareNotAvailableNotification(Referral referral) {
        List<ReferralRequest> filteredList = referral.getReferralRequests()
                .stream()
                .filter(referralRequest -> (
                        referralRequest.getLastResponse() == null))
                .collect(Collectors.toList());
        List<ReferralRequestNotification> notificationsList = createNotifications(filteredList, ReferralRequestNotificationType.NOT_AVAILABLE);
        return referralRequestNotificationDao.saveAll(notificationsList);
    }

    private List<ReferralRequestNotification> prepareSubmitNotification(Referral referral) {
        List<ReferralRequest> filteredList = referral.getReferralRequests()
                .stream()
                .filter(referralRequest -> (
                        referralRequest.getLastResponse() == null))
                .collect(Collectors.toList());
        List<ReferralRequestNotification> notificationsList;
        if (referral.isMarketplace()) {
            if (CollectionUtils.isEmpty(filteredList)) {
                return Collections.emptyList();
            }
            if (ReferralRequestSharedChannel.FAX == filteredList.get(0).getSharedChannel()) {
                notificationsList = createFaxNotifications(filteredList, ReferralRequestNotificationType.NEW_REQUEST);
            } else {
                notificationsList = createExternalNotifications(filteredList, ReferralRequestNotificationType.NEW_REQUEST);
            }

        } else {
            notificationsList = createNotifications(filteredList, ReferralRequestNotificationType.NEW_REQUEST);
        }
        return referralRequestNotificationDao.saveAll(notificationsList);
    }

    private List<ReferralRequestNotification> createFaxNotifications(List<ReferralRequest> referralRequests, ReferralRequestNotificationType type) {
        var referralRequest = referralRequests.get(0);
        var notification = createBaseNotification(referralRequest.getReferral().getRequestingEmployee(), referralRequest, type, false);
        notification.setDestination(referralRequest.getSharedFax());
        notification.setSharedChannel(ReferralRequestSharedChannel.FAX);
        return List.of(notification);
    }

    private List<ReferralRequestNotification> createExternalNotifications(List<ReferralRequest> referralRequests, ReferralRequestNotificationType type) {
        var community = referralRequests.get(0).getCommunity();
        var referralEmails = community.getMarketplace().getReferralEmails();
        if (CollectionUtils.isEmpty(referralEmails)) {
            return Collections.emptyList();
        }
        var externalEmployeeByEmails = externalEmployeeInboundReferralCommunityService.findAllByCommunityId(community.getId())
                .stream()
                .collect(StreamUtils.toMapOfUniqueKeys(em -> em.getEmployee().getLoginName()));
        return referralEmails
                .stream()
                .map(email -> {
                    var extEmployee = externalEmployeeByEmails.computeIfAbsent(email, e -> createExternalEmployeeInboundReferralCommunity(e, community)).getEmployee();
                    return referralRequests
                            .stream()
                            .map(referralRequest -> {
                                var notification = createBaseNotification(extEmployee, referralRequest, type, false);
                                notification.setDestination(email);
                                notification.setSharedChannel(ReferralRequestSharedChannel.EMAIL);
                                return notification;
                            });

                })
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    private ExternalEmployeeInboundReferralCommunity createExternalEmployeeInboundReferralCommunity(String email, Community community) {
        var extEmployee = employeeService.findExternalEmployee(email, EmployeeStatus.PENDING, EmployeeStatus.ACTIVE);
        ExternalEmployeeInboundReferralCommunity employeeCommunity;
        if (extEmployee.isPresent()) {
            employeeCommunity = externalEmployeeInboundReferralCommunityService.create(extEmployee.get(), community);
            if (extEmployee.get().getStatus().equals(EmployeeStatus.PENDING)) {
                externalEmployeeRequestService.create(employeeCommunity);
            }
        } else {
            employeeCommunity = externalEmployeeInboundReferralCommunityService.create(email, community);
            externalEmployeeRequestService.create(employeeCommunity);
        }
        return employeeCommunity;
    }

    private List<ReferralRequestNotification> createNotifications(List<ReferralRequest> list, ReferralRequestNotificationType type) {
        List<ReferralRequestNotification> referralRequestNotifications = new ArrayList<>();
        List<ReferralRequestNotification> notificationsForAdmins = new ArrayList<>();
        list.stream().forEach(referralRequest -> {
            List<ReferralRequestNotification> notifications = getRecipients(referralRequest).stream()
                    .filter(StreamUtils.distinctByKey(Employee::getId))
                    .map(employee -> createNotification(employee, referralRequest, type, false))
                    .collect(Collectors.toList());
            if (notifications.isEmpty()) {
                notificationsForAdmins.addAll(getRecipientsAdmins(referralRequest).stream()
                        .filter(StreamUtils.distinctByKey(Employee::getId))
                        .map(employee -> createNotification(employee, referralRequest, type, true))
                        .collect(Collectors.toList()));
            }
            referralRequestNotifications.addAll(notifications);
        });
        referralRequestNotifications.addAll(notificationsForAdmins
                .stream()
                .filter(StreamUtils.distinctByKey(ReferralRequestNotification::getDestination))
                .collect(Collectors.toList()));
        return referralRequestNotifications;
    }

    private ReferralRequestNotification createNotification(Employee employee, ReferralRequest referralRequest, ReferralRequestNotificationType type, boolean isAdminNotification) {
        var requestNotification = createBaseNotification(employee, referralRequest, type, isAdminNotification);
        requestNotification.setDestination(PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.EMAIL).orElse(null));
        requestNotification.setSharedChannel(ReferralRequestSharedChannel.EMAIL);
        return requestNotification;
    }

    private ReferralRequestNotification createBaseNotification(Employee employee, ReferralRequest referralRequest, ReferralRequestNotificationType type, boolean isAdminNotification) {
        ReferralRequestNotification requestNotification = new ReferralRequestNotification();
        requestNotification.setEmployee(employee);
        requestNotification.setCreatedDatetime(Instant.now());
        requestNotification.setReferralRequest(referralRequest);
        requestNotification.setType(type);
        requestNotification.setIsOrgAdmin(isAdminNotification);
        return requestNotification;
    }

    private List<Employee> getRecipients(ReferralRequest referralRequest) {
        List<Employee> listCtm = communityCareTeamMemberDao
                .findAllByCommunityIdAndEmployeeStatus(referralRequest.getCommunity().getId(), EmployeeStatus.ACTIVE)
                .stream()
                .map(CommunityCareTeamMember::getEmployee)
                .collect(Collectors.toList());
        listCtm.addAll(employeeDao.findByCommunityIdAndStatusAndCareTeamRoleCodeIn(
                referralRequest.getCommunity().getId(), EmployeeStatus.ACTIVE, Set.of(
                        CareTeamRoleCode.ROLE_COMMUNITY_ADMINISTRATOR)));
        return listCtm;
    }

    private List<Employee> getRecipientsAdmins(ReferralRequest referralRequest) {
        return employeeDao.findByOrganizationIdAndStatusAndCareTeamRoleCodeIn(
                referralRequest.getCommunity().getOrganizationId(), EmployeeStatus.ACTIVE, Set.of(
                        CareTeamRoleCode.ROLE_ADMINISTRATOR));

    }

    private void send(List<ReferralRequestNotification> list) {
        list.stream()
                .filter(referralRequestNotification -> referralRequestNotification.getDestination() != null)
                .map(ReferralRequestNotification::getId)
                .forEach(this::send);
    }

    private void send(ReferralRequestNotification notification) {
        if (notification.getDestination() != null) {
            send(notification.getId());
        }
    }

    private void send(Long id) {
        referralNotificationSender.send(id);
    }
}
