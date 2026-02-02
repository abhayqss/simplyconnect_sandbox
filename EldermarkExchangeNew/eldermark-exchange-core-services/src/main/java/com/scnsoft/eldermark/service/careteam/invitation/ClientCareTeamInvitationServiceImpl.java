package com.scnsoft.eldermark.service.careteam.invitation;

import com.scnsoft.eldermark.beans.ClientCareTeamInvitationFilter;
import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.careteam.invitation.ClientCareTeamInvitationDao;
import com.scnsoft.eldermark.dao.specification.ClientCareTeamInvitationSpecificationGenerator;
import com.scnsoft.eldermark.dto.employee.EmployeeUpdates;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.careteam.invitation.*;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientCareTeamInvitationServiceImpl extends BaseInvitationService implements ClientCareTeamInvitationService {

    private static final Logger logger = LoggerFactory.getLogger(ClientCareTeamInvitationServiceImpl.class);

    @Value("${portal.url}")
    private String portalUrl;

    @Autowired
    private ClientCareTeamInvitationSpecificationGenerator clientCareTeamInvitationSpecificationGenerator;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientCareTeamInvitationEmployeeFactory clientCareTeamInvitationEmployeeFactory;

    @Autowired
    private ClientCareTeamInvitationDao clientCareTeamInvitationDao;

    @Autowired
    private ClientCareTeamInvitationNotificationService clientCareTeamInvitationNotificationService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private ClientCareTeamInvitationClientCareTeamMemberFactory clientCareTeamInvitationClientCareTeamMemberFactory;

    @Autowired
    private ClientCareTeamInvitationClientResolver clientCareTeamInvitationClientResolver;

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    private long msInvitationExpiration;

    @Value("${invitation.expiration.time.ms:0}")
    public void setMsInvitationExpiration(long msInvitationExpiration) {
        if (msInvitationExpiration < 0) {
            throw new IllegalArgumentException("Invalid \"invitation.expiration.time.ms\" property: expected a positive number or 0, but got " +
                    msInvitationExpiration + ".");
        }
        this.msInvitationExpiration = msInvitationExpiration;
    }

    @Override
    @Transactional(readOnly = true)
    public <P> Page<P> find(ClientCareTeamInvitationFilter filter, PermissionFilter permissionFilter,
                            Class<P> projectionClass, Pageable pageable) {
        var listSpecification = listSpecification(filter, permissionFilter);

        return clientCareTeamInvitationDao.findAll(listSpecification, projectionClass, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(ClientCareTeamInvitationFilter filter, PermissionFilter permissionFilter) {
        var listSpecification = listSpecification(filter, permissionFilter);

        return clientCareTeamInvitationDao.count(listSpecification);
    }

    private Specification<ClientCareTeamInvitation> listSpecification(ClientCareTeamInvitationFilter filter, PermissionFilter permissionFilter) {
        var byFilter = clientCareTeamInvitationSpecificationGenerator.byFilter(filter);
        var isNotHidden = clientCareTeamInvitationSpecificationGenerator.isNotHidden();
        var hasAccess = clientCareTeamInvitationSpecificationGenerator.hasAccess(permissionFilter);

        return byFilter.and(isNotHidden.and(hasAccess));
    }

    @Override
    public void validateHieConsent(Long clientId) {
        validateHieConsent(clientService.getById(clientId));
    }

    private void validateHieConsent(Client targetClient) {
        clientCareTeamInvitationClientResolver.findExistingClient(targetClient);
    }

    @Override
    public ClientCareTeamInvitation invite(InviteCareTeamMemberData invitationData) {
        logger.info("Inviting care team member {} to care team of client {}",
                invitationData.getEmail(), invitationData.getClientId());

        var targetClient = clientService.findById(invitationData.getClientId());

        validateHieConsent(targetClient);
        validateNotAlreadyInCareTeam(invitationData);

        var targetEmployee = findTargetEmployeeByLoginAndValidate(invitationData, true)
                .map(e -> updateDemographicsIfPending(e, invitationData))
                .orElseGet(() -> clientCareTeamInvitationEmployeeFactory.createNewPendingContact(invitationData));

        var invitation = createPendingInvitation(invitationData, targetClient, targetEmployee);
        return saveAndSendInvitation(invitation);
    }

    private Employee updateDemographicsIfPending(Employee employee, InviteCareTeamMemberData invitationData) {
        if (employee.getStatus() == EmployeeStatus.PENDING) {
            var updateData = new EmployeeUpdates(employee);
            updateData.setFirstName(invitationData.getFirstName());
            updateData.setLastName(invitationData.getLastName());
            updateData.setBirthDate(invitationData.getBirthDate());
            contactService.update(updateData, getCreatorId(invitationData));

        }
        return employee;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canResend(Long invitationId) {
        var invitation = clientCareTeamInvitationDao
                .findById(invitationId, ClientCareTeamInvitationStatusAware.class)
                .orElseThrow();

        return canResend(invitation);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canResend(ClientCareTeamInvitationStatusAware invitation) {
        return invitation.getStatus().isResendable();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCancel(Long invitationId) {
        var invitation = clientCareTeamInvitationDao
                .findById(invitationId, ClientCareTeamInvitationStatusAware.class)
                .orElseThrow();

        return canCancel(invitation);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCancel(ClientCareTeamInvitationStatusAware invitation) {
        return invitation.getStatus().isCancelable();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAcceptOrDecline(Long invitationId) {
        var invitation = clientCareTeamInvitationDao
                .findById(invitationId, ClientCareTeamInvitationAcceptDeclineValidationFieldsAware.class)
                .orElseThrow();

        return canAcceptOrDecline(invitation);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAcceptOrDecline(ClientCareTeamInvitationAcceptDeclineValidationFieldsAware invitation) {
        try {
            validateExistingUserInvitation(invitation);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void decline(Long invitationId) {
        var invitation = clientCareTeamInvitationDao.findById(invitationId).orElseThrow();
        validateExistingUserInvitation(invitation);

        invitation.setStatus(ClientCareTeamInvitationStatus.DECLINED);
        invitation.setDeclinedAt(Instant.now());
        clientCareTeamInvitationDao.save(invitation);
    }

    private boolean isPending(ClientCareTeamInvitationStatusAware invitation) {
        return ClientCareTeamInvitationStatus.PENDING.equals(invitation.getStatus());
    }

    @Override
    public void accept(Long invitationId) {
        var invitation = clientCareTeamInvitationDao.findById(invitationId).orElseThrow();
        validateExistingUserInvitation(invitation);

        var rctm = addAsCareTeamMember(invitation.getTargetEmployee(), invitation.getClientId(), invitation.getCreatedByEmployeeId());

        invitation.setStatus(ClientCareTeamInvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(Instant.now());
        invitation.setHidden(true);
        invitation.setFamilyAppClientId(rctm.getClientId());

        clientCareTeamInvitationDao.save(invitation);
    }

    @Override
    public void confirmInvitation(ConfirmInviteCareTeamMemberData confirmInvitationData) {
        var invitation = clientCareTeamInvitationDao.findByToken(confirmInvitationData.getToken())
                .orElseThrow(() -> new BusinessException("Request already processed."));

        validateNewUserInvitation(invitation);

        var targetEmployee = invitation.getTargetEmployee();

        updateAndSetConfirmedEmployee(targetEmployee, confirmInvitationData);

        var rctm = addAsCareTeamMember(targetEmployee, invitation.getClientId(), invitation.getCreatedByEmployeeId());

        invitation.setStatus(ClientCareTeamInvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(Instant.now());
        invitation.setHidden(true);
        invitation.setToken(null);
        invitation.setFamilyAppClientId(rctm.getClientId());

        clientCareTeamInvitationDao.save(invitation);

        //other invitations should be accepted in app
        clientCareTeamInvitationDao.deleteInvitationTokens(targetEmployee.getId());

        sendRegistrationConfirmationEmail(targetEmployee, false);
    }

    private void validateNewUserInvitation(ClientCareTeamInvitation invitation) {
        if (isExpiredByDate(invitation)) {
            throw new BusinessException("Your invitation has expired. Please contact administrator");
        }

        var targetEmployee = invitation.getTargetEmployee();

        if (ClientCareTeamInvitationStatus.CANCELED.equals(invitation.getStatus()))
            throw new BusinessException("Invitation is declined. Please contact administrator");

        if (EmployeeStatus.CONFIRMED.equals(targetEmployee.getStatus()) ||
                EmployeeStatus.ACTIVE.equals(targetEmployee.getStatus()) ||
                ClientCareTeamInvitationStatus.ACCEPTED.equals(invitation.getStatus())) {
            throw new BusinessException("Request already processed.");
        }

        if (EmployeeStatus.EXPIRED.equals(targetEmployee.getStatus()) ||
                ClientCareTeamInvitationStatus.EXPIRED.equals(invitation.getStatus()))
            throw new BusinessException("Your invitation has expired. Please contact administrator");

        if (EmployeeStatus.INACTIVE.equals(targetEmployee.getStatus()))
            throw new BusinessException("Your account is inactive. Please contact administrator");

        if (EmployeeStatus.DECLINED.equals(targetEmployee.getStatus()) ||
                ClientCareTeamInvitationStatus.DECLINED.equals(invitation.getStatus()))
            throw new BusinessException("Invitation is declined. Please contact administrator");

        if (!EmployeeStatus.PENDING.equals(targetEmployee.getStatus())) {
            throw new BusinessException("User is not Pending");
        }
    }

    private void updateAndSetConfirmedEmployee(Employee employee, ConfirmInviteCareTeamMemberData data) {

        passwordService.createPassword(employee, data.getPassword());

        var employeeUpdates = new EmployeeUpdates(employee);

        employeeUpdates.setFirstName(data.getFirstName());
        employeeUpdates.setLastName(data.getLastName());
        employeeUpdates.setBirthDate(data.getBirthDate());
        employeeUpdates.setCellPhone(data.getMobilePhone());
        employeeUpdates.setStatus(EmployeeStatus.CONFIRMED);

        contactService.update(employeeUpdates, employee.getId());
    }

    private void validateExistingUserInvitation(ClientCareTeamInvitationAcceptDeclineValidationFieldsAware invitation) {
        if (isExpiredByDate(invitation)) {
            throw new BusinessException("Your invitation has expired. Please contact administrator");
        }

        if (!EmployeeStatus.ACTIVE.equals(invitation.getTargetEmployeeStatus())) {
            throw new BusinessException("Your account is not active. Please contact administrator");
        }

        if (ClientCareTeamInvitationStatus.CANCELED.equals(invitation.getStatus()))
            throw new BusinessException("Invitation is declined. Please contact administrator");

        if (ClientCareTeamInvitationStatus.ACCEPTED.equals(invitation.getStatus())) {
            throw new BusinessException("Request already processed.");
        }

        if (ClientCareTeamInvitationStatus.EXPIRED.equals(invitation.getStatus()))
            throw new BusinessException("Your invitation has expired. Please contact administrator");

        if (ClientCareTeamInvitationStatus.DECLINED.equals(invitation.getStatus()))
            throw new BusinessException("Invitation is declined. Please contact administrator");
    }

    private void validateExistingUserInvitation(ClientCareTeamInvitation invitation) {
        validateExistingUserInvitation(new ClientCareTeamInvitationAcceptDeclineValidationFieldsAwareAdapter(invitation));
    }

    private boolean isExpiredByDate(ClientCareTeamInvitationCreatedAtAware invitation) {
        var shouldBeCreatedAfter = getExpiredInvitationWasCreatedBeforeDate();
        return invitation.getCreatedAt().isBefore(shouldBeCreatedAfter);
    }

    private Instant getExpiredInvitationWasCreatedBeforeDate() {
        return Instant.now().minus(msInvitationExpiration, ChronoUnit.MILLIS);
    }

    private ClientCareTeamMember addAsCareTeamMember(Employee targetEmployee, Long clientId, Long performedById) {
        var client = clientCareTeamInvitationClientResolver.resolveClient(clientId);
        return clientCareTeamInvitationClientCareTeamMemberFactory.createCareTeamMember(
                targetEmployee, client, performedById
        );
    }

    @Override
    public ClientCareTeamInvitation resendInvitation(Long id, InviteCareTeamMemberData data) {
        var originalInvitation = clientCareTeamInvitationDao.findById(id).orElseThrow();

        if (!canResend(originalInvitation)) {
            throw new BusinessException("Can't resend invitation");
        }

        var targetClient = clientService.findById(originalInvitation.getClientId());

        validateNotAlreadyInCareTeam(data);

        var targetEmployee = findTargetEmployeeByLoginAndValidate(data, false)
                .orElseGet(originalInvitation::getTargetEmployee);
        updateEmployeeForResend(targetEmployee, data);

        hideInvitation(originalInvitation);

        var newInvitation = createPendingInvitation(data, targetClient, targetEmployee);
        newInvitation.setResentFromInvitationId(originalInvitation.getId());

        saveAndSendInvitation(newInvitation);
        return newInvitation;
    }

    private Employee updateEmployeeForResend(Employee employee, InviteCareTeamMemberData data) {
        var employeeUpdates = new EmployeeUpdates(employee);

        if (employeeUpdates.getStatus() == EmployeeStatus.PENDING) {
            fillDemographics(employeeUpdates, data);
        }

        if (!employee.getStatus().canLogin()) {
            employeeUpdates.setStatus(EmployeeStatus.PENDING);
        }

        employeeUpdates.setEmail(data.getEmail());

        return contactService.update(employeeUpdates, getCreatorId(data));
    }

    private void fillDemographics(EmployeeUpdates employeeUpdates, BaseInviteCareTeamMemberData data) {
        employeeUpdates.setFirstName(data.getFirstName());
        employeeUpdates.setLastName(data.getLastName());
        employeeUpdates.setBirthDate(data.getBirthDate());
    }

    private void validateNotAlreadyInCareTeam(InviteCareTeamMemberData data) {
        var alreadyInCareTeam = careTeamMemberService.doesEmployeeWithSameLoginExistInClientCareTeam(
                data.getClientId(),
                data.getEmail()
        );

        if (alreadyInCareTeam) {
            throw new BusinessException("This user is already on your Care Team");
        }
    }

    private Optional<Employee> findTargetEmployeeByLoginAndValidate(InviteCareTeamMemberData invitationData,
                                                                    boolean validateNotAlreadyInvited) {
        var employee = employeeDao.findFirst(
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.and(
                                        criteriaBuilder.equal(
                                                JpaUtils.getOrCreateJoin(root, Employee_.organization).get(Organization_.alternativeId),
                                                CareCoordinationConstants.FAMILY_APP_ALTERNATIVE_ID),
                                        criteriaBuilder.equal(
                                                root.get(Employee_.loginName),
                                                invitationData.getEmail())
                                ),
                        Employee.class
                )
                .map(this::validateNotInactive);

        if (validateNotAlreadyInvited) {
            employee = employee.map(e -> validateNotAlreadyInvited(e, invitationData));
        }
        return employee;
    }

    private Employee validateNotInactive(Employee employee) {
        if (EmployeeStatus.INACTIVE.equals(employee.getStatus())) {
            throw new BusinessException("Can't invite - contact is inactive");
        }
        return employee;
    }

    private Employee validateNotAlreadyInvited(Employee employee, InviteCareTeamMemberData invitationData) {
        var clientCareTeamInvitationFilter = new ClientCareTeamInvitationFilter();
        clientCareTeamInvitationFilter.setTargetEmployeeId(employee.getId());
        //todo also check date, may be already expired
        clientCareTeamInvitationFilter.setStatuses(Set.of(ClientCareTeamInvitationStatus.PENDING));
        clientCareTeamInvitationFilter.setClientId(invitationData.getClientId());

        var alreadyInvited = clientCareTeamInvitationDao.exists(
                clientCareTeamInvitationSpecificationGenerator.byFilter(clientCareTeamInvitationFilter).and(
                        clientCareTeamInvitationSpecificationGenerator.isNotHidden()
                )
        );

        if (alreadyInvited) {
            throw new BusinessException("This user is already invited");
        }

        return employee;
    }

    private ClientCareTeamInvitation createPendingInvitation(InviteCareTeamMemberData invitationData, Client targetClient, Employee targetEmployee) {
        var invitation = new ClientCareTeamInvitation();
        invitation.setCreatedByEmployeeId(invitationData.getCreatedByEmployee().getId());
        invitation.setCreatedByEmployee(invitationData.getCreatedByEmployee());
        invitation.setTargetEmployee(targetEmployee);
        invitation.setTargetEmployeeId(targetEmployee.getId());
        invitation.setFirstName(invitationData.getFirstName());
        invitation.setLastName(invitationData.getLastName());
        invitation.setBirthDate(invitationData.getBirthDate());
        invitation.setEmail(invitationData.getEmail());
        invitation.setClient(targetClient);
        invitation.setClientId(targetClient.getId());
        invitation.setCreatedAt(Instant.now());
        invitation.setStatus(ClientCareTeamInvitationStatus.PENDING);
        return invitation;
    }

    @Override
    public void cancelInvitation(Long id) {
        var invitation = clientCareTeamInvitationDao.findById(id).orElseThrow();
        cancelInvitation(invitation);
    }

    private void cancelInvitation(ClientCareTeamInvitation invitation) {
        if (!canCancel(invitation)) {
            throw new BusinessException("Can't cancel invitation");
        }

        invitation.setCancelledAt(Instant.now());
        invitation.setToken(null);
        invitation.setStatus(ClientCareTeamInvitationStatus.CANCELED);

        clientCareTeamInvitationDao.save(invitation);

        clientCareTeamInvitationNotificationService.sendCancelledNotificationsAsync(invitation.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return clientCareTeamInvitationDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientCareTeamInvitationDao.findByIdIn(ids, projection);
    }

    private void hideInvitation(ClientCareTeamInvitation originalInvitation) {
        originalInvitation.setHidden(true);
        clientCareTeamInvitationDao.save(originalInvitation);
    }

    private ClientCareTeamInvitation saveAndSendInvitation(ClientCareTeamInvitation invitation) {
        boolean shouldSendInvitationLinkEmail = !invitation.getTargetEmployee().getStatus().canLogin();

        if (shouldSendInvitationLinkEmail) {
            invitation.setToken(UUID.randomUUID().toString());
        }

        clientCareTeamInvitationDao.save(invitation);

        var invitationId = invitation.getId();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
                clientCareTeamInvitationNotificationService.sendInvitationNotificationsAsync(invitationId);
            }
        });

        return invitation;
    }

    private boolean isCanceled(ClientCareTeamInvitation invitation) {
        return ClientCareTeamInvitationStatus.CANCELED.equals(invitation.getStatus());
    }

    private String urlEncode(String src) {
        return URLEncoder.encode(src, StandardCharsets.UTF_8);
    }

    private Long getCreatorId(InviteCareTeamMemberData data) {
        return Optional.of(data.getCreatedByEmployee()).map(BasicEntity::getId).orElse(null);
    }

    @Override
    public void expireInvitations() {
        var expiredInvitationWasCreatedBeforeDate = getExpiredInvitationWasCreatedBeforeDate();
        clientCareTeamInvitationDao.findAll(
                        clientCareTeamInvitationSpecificationGenerator.createdBeforeOrEqualDate(expiredInvitationWasCreatedBeforeDate)
                                .and(clientCareTeamInvitationSpecificationGenerator.byType(ClientCareTeamInvitationStatus.PENDING))
                                .and(clientCareTeamInvitationSpecificationGenerator.isNotHidden())
                )
                .forEach(invitation -> expireInvitation(invitation, expiredInvitationWasCreatedBeforeDate));
    }

    private void expireInvitation(ClientCareTeamInvitation invitation, Instant expiredInvitationWasCreatedBeforeDate) {
        invitation.setStatus(ClientCareTeamInvitationStatus.EXPIRED);
        invitation.setExpiredAt(invitation.getCreatedAt().plus(msInvitationExpiration, ChronoUnit.MILLIS));
        invitation.setToken(null);

        clientCareTeamInvitationDao.save(invitation);

        if (!clientCareTeamInvitationDao.exists(
                clientCareTeamInvitationSpecificationGenerator.createdAfterDate(expiredInvitationWasCreatedBeforeDate)
                        .and(clientCareTeamInvitationSpecificationGenerator.byType(ClientCareTeamInvitationStatus.PENDING))
                        .and(clientCareTeamInvitationSpecificationGenerator.byTargetEmployeeId(invitation.getTargetEmployeeId()))
                        .and(clientCareTeamInvitationSpecificationGenerator.isNotHidden())
        )) {
            expireEmployee(invitation.getTargetEmployee());
        }
    }

    private void expireEmployee(Employee employee) {
        if (EmployeeStatus.PENDING.equals(employee.getStatus())) {
            contactService.update(new EmployeeUpdates(employee, EmployeeStatus.EXPIRED), null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String resolveRedirectCreateAccountFromEmail(String token) {
        return clientCareTeamInvitationDao.findByToken(token)
                .map(invitation -> {
                    var base = portalUrl;
                    String webScreenLink;
                    if (isExpiredByDate(invitation)) {
                        webScreenLink = "care-team-invitations/expired";
                    } else if (isCanceled(invitation)) {
                        webScreenLink = "care-team-invitations/cancelled";
                    } else {
                        webScreenLink = buildAcceptLinkWithParameters(invitation);
                    }
                    return base + webScreenLink;
                })
                .orElseGet(() -> portalUrl + "care-team-invitations/expired");
    }

    private String buildAcceptLinkWithParameters(ClientCareTeamInvitation invitation) {
        return String.format("care-team-invitations/accept?firstName=%s&lastName=%s&birthDate=%s&email=%s&token=%s&organizationId=%s",
                urlEncode(invitation.getTargetEmployee().getFirstName()),
                urlEncode(invitation.getTargetEmployee().getLastName()),
                urlEncode(invitation.getBirthDate().toString()),
                urlEncode(invitation.getEmail()),
                urlEncode(invitation.getToken()),
                invitation.getTargetEmployee().getOrganizationId()
        );
    }

    @Override
    public boolean existsInbound(Long clientId, Set<Long> targetEmployeeIds) {
        var notHiddenPending = notHiddenPending();
        var byClientId = clientCareTeamInvitationSpecificationGenerator.byClientId(clientId);
        var byTargetEmployeeIdIn = clientCareTeamInvitationSpecificationGenerator.byTargetEmployeeIdIn(targetEmployeeIds);

        return clientCareTeamInvitationDao.exists(notHiddenPending.and(byClientId.and(byTargetEmployeeIdIn)));
    }

    @Override
    public boolean existsAccessibleToTargetEmployee(Long employeeId, PermissionFilter permissionFilter) {
        var hasAccess = clientCareTeamInvitationSpecificationGenerator.hasAccess(permissionFilter);
        var notHidden = clientCareTeamInvitationSpecificationGenerator.isNotHidden();
        var byTargetEmployeeId = clientCareTeamInvitationSpecificationGenerator.byTargetEmployeeId(employeeId);

        return clientCareTeamInvitationDao.exists(hasAccess.and(notHidden).and(byTargetEmployeeId));
    }

    @Override
    public boolean existsIncomingForHieConsentChange(Long clientId) {
        var client = clientService.findById(clientId);
        return new IncomingInvitationsForHieConsentChangeResolver(client).exists();
    }

    @Override
    public <T> List<T> findIncomingForHieConsentChangeInCommunity(Set<Long> affectedClientIds, String organizationAlternativeId,
                                                                  Class<T> projectionClass) {
        return new IncomingInvitationsForHieConsentChangeResolver(affectedClientIds, organizationAlternativeId).getInvitations(
                projectionClass
        );
    }

    @Override
    @Async
    public void cancelInvitationsForOptOutAsync(Long clientId, String organizationAlternativeId) {
        var invitations = new IncomingInvitationsForHieConsentChangeResolver(clientId, organizationAlternativeId)
                .getInvitations();

        invitations.forEach(invitation -> {
            try {
                cancelInvitation(invitation);
            } catch (Exception e) {
                logger.error("Failed to cancel invitation {}", invitation.getId());
            }
        });
    }

    @Override
    @Async
    public void cancelInvitationsForOptOutInCommunityAsync(Set<Long> affectedClientIds, String organizationAlternativeId) {
        var invitations = new IncomingInvitationsForHieConsentChangeResolver(
                affectedClientIds,
                organizationAlternativeId
        )
                .getInvitations();

        invitations.forEach(invitation -> {
            try {
                cancelInvitation(invitation);
            } catch (Exception e) {
                logger.error("Failed to cancel invitation {}", invitation.getId());
            }
        });
    }

    private Specification<ClientCareTeamInvitation> incomingForHieConsentChangeSpec(Collection<Long> clientIds) {
        var byClientId = clientCareTeamInvitationSpecificationGenerator.byClientIdIn(clientIds);
        var notHiddenPending = notHiddenPending();
        return byClientId.and(notHiddenPending);
    }

    private Specification<ClientCareTeamInvitation> notHiddenPending() {
        var byType = clientCareTeamInvitationSpecificationGenerator.byType(ClientCareTeamInvitationStatus.PENDING);
        var notHidden = clientCareTeamInvitationSpecificationGenerator.isNotHidden();
        return byType.and(notHidden);
    }

    private Specification<ClientCareTeamInvitation> notFamilyAppClient() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.notEqual(
                JpaUtils.getOrCreateJoin(
                        JpaUtils.getOrCreateJoin(root, ClientCareTeamInvitation_.client),
                        Client_.organization
                ).get(Organization_.alternativeId),
                CareCoordinationConstants.FAMILY_APP_ALTERNATIVE_ID
        );
    }



    private class IncomingInvitationsForHieConsentChangeResolver {

        private final boolean familyAppClient;
        private List<Client> resolvedNonFamilyAppClients;
        private Specification<ClientCareTeamInvitation> specForNonFamilyAppClient;

        public IncomingInvitationsForHieConsentChangeResolver(Long clientId, String organizationAlternativeId) {
            this(
                    Lazy.of(() -> List.of(clientId)),
                    Lazy.of(() -> List.of(clientService.findById(clientId))),
                    organizationAlternativeId
            );
        }

        public IncomingInvitationsForHieConsentChangeResolver(Client client) {
            this(
                    Lazy.of(() -> List.of(client.getId())),
                    Lazy.of(() -> List.of(client)),
                    client.getOrganization().getAlternativeId()
            );
        }

        public IncomingInvitationsForHieConsentChangeResolver(Set<Long> clientIds, String organizationAlternativeId) {
            this(
                    Lazy.of(clientIds),
                    Lazy.of(() -> clientService.findAllById(clientIds, Client.class)),
                    organizationAlternativeId
            );
        }

        private IncomingInvitationsForHieConsentChangeResolver(Lazy<Collection<Long>> clientIdsSupplier, Lazy<List<Client>> clientsSupplier, String organizationAlternativeId) {
            if (CareCoordinationConstants.FAMILY_APP_ALTERNATIVE_ID.equals(organizationAlternativeId)) {
                familyAppClient = true;
                //1. get clients with pending invitations not in family app
                var clientIds = clientCareTeamInvitationDao.findAll(notHiddenPending().and(notFamilyAppClient()),
                                ClientIdAware.class)
                        .stream()
                        .map(ClientIdAware::getClientId)
                        .collect(Collectors.toList());
                var clientsToCheck = clientService.findAllById(clientIds);

                //2. among them find merges or potential merges or with the same unique fields
                resolvedNonFamilyAppClients = clientCareTeamInvitationClientResolver.findForHiePolicyChangeAmong(
                        clientIdsSupplier.get(),
                        clientsSupplier.get(),
                        clientsToCheck
                );
            } else {
                familyAppClient = false;
                //for updating non-family app client - check just this client
                specForNonFamilyAppClient = incomingForHieConsentChangeSpec(clientIdsSupplier.get());
            }
        }

        public List<ClientCareTeamInvitation> getInvitations() {
            return getInvitations(ClientCareTeamInvitation.class);
        }

        public <T> List<T> getInvitations(Class<T> projectionClass) {
            if (familyAppClient) {
                return clientCareTeamInvitationDao.findAll(notHiddenPending().and(clientCareTeamInvitationSpecificationGenerator.byClientIdIn(
                                CareCoordinationUtils.toIdsSet(resolvedNonFamilyAppClients))),
                        projectionClass);
            } else {
                return clientCareTeamInvitationDao.findAll(specForNonFamilyAppClient, projectionClass);
            }
        }

        public boolean exists() {
            if (familyAppClient) {
                return CollectionUtils.isNotEmpty(resolvedNonFamilyAppClients);
            } else {
                return clientCareTeamInvitationDao.exists(specForNonFamilyAppClient);
            }
        }
    }
}
