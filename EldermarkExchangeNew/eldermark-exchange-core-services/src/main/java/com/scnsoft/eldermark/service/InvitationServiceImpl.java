package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.InvitationDto;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.EmployeeRequestDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.basic.CareCoordinationConstants;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
public class InvitationServiceImpl extends BaseInvitationService implements InvitationService {
    private static final Logger logger = LoggerFactory.getLogger(InvitationServiceImpl.class);

    @Value("${portal.url}")
    private String portalUrl;

    @Value("${reset.password.request.url}")
    private String resetPasswordRequestUrl;

    @Value("${reset.password.external.request.url}")
    private String resetPasswordExternalRequestUrl;

    @Autowired
    private EmployeeDao employeeDao;

    //todo use service instead
    @Autowired
    private EmployeeRequestDao employeeRequestDao;

    @Autowired
    private EmployeeRequestService employeeRequestService;

    @Autowired
    private ExternalEmployeeRequestService externalEmployeeRequestService;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private PasswordService passwordService;


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
    public void invite(Long targetEmployeeId) {
        logger.info("Invitation email will be sent for employee {}", targetEmployeeId);
        var targetEmployee = employeeDao.findById(targetEmployeeId).orElseThrow();

        employeeRequestDao.deleteAllByTargetEmployeeAndTokenType(targetEmployee, EmployeeRequestType.INVITE);

        var employeeRequest = createEmployeeRequest(targetEmployee);
        sendInvitationEmail(employeeRequest);

        targetEmployee.setStatus(EmployeeStatus.PENDING);
        employeeDao.save(targetEmployee);
    }

    @Override
    public void confirmRegistration(String token, String password) {
        validateInvitationToken(token);

        var employeeRequest = employeeRequestService.findByToken(token, EmployeeRequestType.INVITE);
        var targetEmployee = employeeRequest.getTargetEmployee();

        passwordService.createPassword(targetEmployee, password);

        employeeRequestService.delete(employeeRequest);

        targetEmployee.setStatus(EmployeeStatus.ACTIVE);
        targetEmployee.setModifiedTimestamp(Instant.now().toEpochMilli());
        employeeDao.save(targetEmployee);

        sendRegistrationConfirmationEmail(targetEmployee, false);
    }

    @Override
    @Transactional
    public void confirmRegistrationExternal(String token, String password, String firstName, String lastName) {
        validateInvitationTokenExternal(token);
        var externalEmployeeRequest = externalEmployeeRequestService.findByToken(token);
        var externalEmployee = externalEmployeeRequest.getEmployeeCommunity().getEmployee();
        externalEmployee.setStatus(EmployeeStatus.ACTIVE);
        externalEmployee.setFirstName(firstName);
        externalEmployee.setLastName(lastName);
        externalEmployee.setPerson(createPerson(externalEmployee));
        passwordService.createPassword(externalEmployee, password);
        externalEmployeeRequestService.deleteById(externalEmployeeRequest.getId());
        sendRegistrationConfirmationEmail(externalEmployee, true);
    }

    @Override
    public void validateInvitationTokenExternal(String token) {
        var externalEmployeeRequest = externalEmployeeRequestService.findByToken(token);
        if (externalEmployeeRequest == null)
            throw new BusinessException("Request already processed.");
    }

    @Override
    public void validateInvitationToken(String token) {
        final EmployeeRequest employeeRequest = employeeRequestService.findByToken(token, EmployeeRequestType.INVITE);
        if (employeeRequest == null || EmployeeStatus.ACTIVE.equals(employeeRequest.getTargetEmployee().getStatus()))
            throw new BusinessException("Request already processed.");

        var shouldBeCreatedAfter = getExpiredInvitationWasCreatedBeforeDate();
        if (employeeRequest.getCreatedDateTime().isBefore(shouldBeCreatedAfter)) {
            throw new BusinessException("Your invitation has expired. Please contact administrator");
        }

        final Employee targetEmployee = employeeRequest.getTargetEmployee();

        if (EmployeeStatus.EXPIRED.equals(targetEmployee.getStatus()))
            throw new BusinessException("Your invitation has expired. Please contact administrator");
        if (EmployeeStatus.INACTIVE.equals(targetEmployee.getStatus()))
            throw new BusinessException("Your account is inactive. Please contact administrator");
        if (EmployeeStatus.DECLINED.equals(targetEmployee.getStatus()))
            throw new BusinessException("Invitation is declined. Please contact administrator");

    }

    @Override
    @Transactional
    public void declineInvitation(String token) {
        final EmployeeRequest employeeRequest = employeeRequestService.findByToken(token, EmployeeRequestType.INVITE);
        if (employeeRequest == null)
            throw new BusinessException("Request already processed.");
        if (EmployeeStatus.PENDING.equals(employeeRequest.getTargetEmployee().getStatus())) {
            employeeRequest.getTargetEmployee().setStatus(EmployeeStatus.DECLINED);
            var associatedClients = employeeRequest.getTargetEmployee().getAssociatedClients();
            if (CollectionUtils.isNotEmpty(associatedClients)) {
                associatedClients.clear();
                employeeRequest.getTargetEmployee().setAssociatedClients(associatedClients);
            }
            employeeDao.save(employeeRequest.getTargetEmployee());
            employeeRequestService.delete(employeeRequest);
        } else
            throw new BusinessException("Not able to decline request.");
    }

    private EmployeeRequest createEmployeeRequest(final Employee employee) {
        final EmployeeRequest employeeRequest = prepareInviteEmployeeRequest();
        var loggedUser = loggedUserService.getCurrentEmployee();
        employeeRequest.setTargetEmployee(employee);
        employeeRequest.setCreatedEmployee(loggedUser);
        return employeeRequestDao.save(employeeRequest);
    }

    private EmployeeRequest prepareInviteEmployeeRequest() {
        final EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setCreatedDateTime(Instant.now());
        employeeRequest.setTokenType(EmployeeRequestType.INVITE);
        employeeRequest.setToken(UUID.randomUUID().toString());
        return employeeRequest;
    }

    private void sendInvitationEmail(EmployeeRequest employeeRequest) {
        InvitationDto invitationDto = createInvitationDto(employeeRequest);
        exchangeMailService.sendInvitation(invitationDto);
    }

    private InvitationDto createInvitationDto(final EmployeeRequest employeeRequest) {
        var result = new InvitationDto();

        var targetEmployee = employeeRequest.getTargetEmployee();

        result.setCreator(getCreatorFullName(employeeRequest));
        result.setToEmail(targetEmployee.getLoginName());
        result.setTargetFullname(targetEmployee.getFullName());
        result.setButtonUrl(buildButtonUrl(result.getCreator(), employeeRequest));
        result.setTargetUsername(targetEmployee.getLoginName());
        result.setTargetCompanyId(targetEmployee.getOrganization().getSystemSetup().getLoginCompanyId());

        return result;

    }

    private String buildButtonUrl(String inviterName, EmployeeRequest employeeRequest) {
        try {
            return String.format("%sinvitation?organizationId=%s&inviter=%s&companyId=%s&token=%s",
                    portalUrl,
                    employeeRequest.getTargetEmployee().getOrganizationId(),
                    URLEncoder.encode(inviterName, StandardCharsets.UTF_8.toString()),
                    URLEncoder.encode(employeeRequest.getTargetEmployee().getOrganization().getSystemSetup().getLoginCompanyId(), StandardCharsets.UTF_8.toString()),
                    employeeRequest.getToken());

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCreatorFullName(EmployeeRequest employeeRequest) {
        if (employeeRequest.getCreatedEmployee() != null) {
            return employeeRequest.getCreatedEmployee().getFullName();
        }
        if (employeeRequest.getCreatedClient() != null) {
            return employeeRequest.getCreatedClient().getFullName();
        }
        return StringUtils.EMPTY;
    }

    private Person createPerson(Employee employee) {
        var person = new Person();
        person.setOrganizationId(employee.getOrganization().getId());
        person.setOrganization(employee.getOrganization());
        person.setNames(new ArrayList<>());
        CareCoordinationConstants.setLegacyId(person);
        person.setLegacyTable(CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE);

        CareCoordinationUtils.createAndAddName(person, employee.getFirstName(), employee.getLastName());

        var telecom = new PersonTelecom();
        telecom.setSyncQualifier(PersonTelecomCode.EMAIL.getCode());
        telecom.setUseCode(PersonTelecomCode.EMAIL.name());
        telecom.setOrganization(person.getOrganization());
        telecom.setOrganizationId(person.getOrganizationId());
        telecom.setLegacyTable(com.scnsoft.eldermark.service.CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE);
        CareCoordinationConstants.setLegacyId(telecom);
        telecom.setPerson(person);
        telecom.setValue(employee.getLoginName());
        telecom.setNormalized(CareCoordinationUtils.normalizeEmail(employee.getLoginName()));
        person.setTelecoms(new ArrayList<>());
        person.getTelecoms().addAll(Collections.singletonList(telecom));
        return person;

    }

    @Override
    @Transactional
    public void expireInvitations() {
        var expiredInvitationWasCreatedBeforeDate = getExpiredInvitationWasCreatedBeforeDate();
        var requests = employeeRequestService.findRequestsCreatedBefore(expiredInvitationWasCreatedBeforeDate,
                EmployeeRequestType.INVITE, InvitationProjection.class);
        for (InvitationProjection invitation : requests) {
            expireInvitation(invitation);
        }
    }

    private void expireInvitation(InvitationProjection invitation) {
        if (EmployeeStatus.ACTIVE.equals(invitation.getTargetEmployeeStatus())) {
            logger.error("Trying to expire invitation invite request for employee that is already activated. Request token is {}", invitation.getToken());
            return;
        }
        //todo should we change status from INACTIVE to EXPIRED?
        else {
            //todo also remove expired employee request. The reason why it is not deleted is lost in mists of time.
//            employeeRequestDao.delete(employeeRequest);
//            careTeamMemberDao.deleteCareTeamMembersForEmployee(employeeRequest.getTargetEmployee());
//            activityDao.deleteByEmployee(employeeRequest.getTargetEmployee());

            if (EmployeeStatus.EXPIRED.equals(invitation.getTargetEmployeeStatus())) {
                logger.info("Employee [{}] is already expired", invitation.getTargetEmployeeId());
            } else {
                logger.info("Expiring employee [{}]", invitation.getTargetEmployeeId());
                employeeDao.updateStatus(EmployeeStatus.EXPIRED, invitation.getTargetEmployeeId());

            }
        }
    }

    private Instant getExpiredInvitationWasCreatedBeforeDate() {
        return Instant.now().minus(msInvitationExpiration, ChronoUnit.MILLIS);
    }

    private interface InvitationProjection {
        Long getTargetEmployeeId();

        EmployeeStatus getTargetEmployeeStatus();

        String getToken();
    }
}
