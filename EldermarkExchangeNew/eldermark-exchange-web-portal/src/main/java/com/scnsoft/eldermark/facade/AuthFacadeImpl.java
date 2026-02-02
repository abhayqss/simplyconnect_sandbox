package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dto.PasswordComplexityRules;
import com.scnsoft.eldermark.dto.UserDto;
import com.scnsoft.eldermark.dto.password.CreatePasswordExternalDto;
import com.scnsoft.eldermark.dto.password.PasswordChangeDto;
import com.scnsoft.eldermark.dto.password.PasswordResetDto;
import com.scnsoft.eldermark.dto.password.PasswordResetRequestDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.InvitationService;
import com.scnsoft.eldermark.service.PasswordService;
import com.scnsoft.eldermark.service.docutrack.DocutrackService;
import com.scnsoft.eldermark.service.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.service.security.ChatSecurityService;
import com.scnsoft.eldermark.service.security.PaperlessHealthcareSecurityService;
import com.scnsoft.eldermark.service.security.VideoCallSecurityService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthFacadeImpl implements AuthFacade {

    private static final Logger logger = LoggerFactory.getLogger(AuthFacadeImpl.class);

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private EmployeePasswordSecurityService employeePasswordSecurityService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private Converter<Employee, UserDto> employeeToUserDtoConverter;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private ReleaseNoteFacade releaseNoteFacade;

    @Autowired
    private ChatSecurityService chatSecurityService;

    @Autowired
    private VideoCallSecurityService videoCallSecurityService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private DocutrackService docutrackService;

    @Autowired
    private PaperlessHealthcareSecurityService paperlessHealthcareSecurityService;

    @Override
    public void requestPasswordReset(PasswordResetRequestDto dto) {
        passwordService.requestPasswordReset(dto.getEmail(), dto.getCompanyCode());
    }

    @Override
    public void resetPassword(PasswordResetDto dto) {
        passwordService.resetPassword(dto.getToken(), dto.getPassword());
    }

    @Override
    public void createPasswordAndActivateAccount(PasswordResetDto dto) {
        invitationService.confirmRegistration(dto.getToken(), dto.getPassword());
    }

    @Override
    public void createPasswordAndActivateAccountExternal(CreatePasswordExternalDto dto) {
        invitationService.confirmRegistrationExternal(dto.getToken(), dto.getPassword(), dto.getFirstName(), dto.getLastName());
    }

    @Override
    public void changePassword(PasswordChangeDto dto) {
        Employee employee = employeeService.getEmployeeThatCanLogin(dto.getUsername(), dto.getCompany());
        passwordService.changePassword(employee, dto.getPassword(), dto.getNewPassword());
    }

    @Override
    public UserDto getUser(Long id) {
        var employee = employeeService.getEmployeeById(id);
        return getUser(employee);
    }

    @Override
    public UserDto getUserWithConversationToken(Long id) {
        var employee = employeeService.getEmployeeById(id);
        var userDto = getUser(employee);
        if (userDto.getAreConversationsEnabled()) {
            userDto.setConversationAccessToken(chatService.generateToken(employee));
        }
        return userDto;
    }

    private UserDto getUser(Employee employee) {
        var userDto = employeeToUserDtoConverter.convert(employee);
        var lastLoginDate = DateTimeUtils.toInstant(userDto.getLastLoginDate());
        if (lastLoginDate != null) {
            //userDto.setNotifications(releaseNoteFacade.findReleaseNotificationsByCreatedDateAfterWithInAppEnabled(lastLoginDate));
            userDto.setNotifications(releaseNoteFacade.findLatestReleaseNotificationByCreatedDateAfterWithInAppEnabled(lastLoginDate).map(List::of).orElse(null));
        } else {
            userDto.setNotifications(releaseNoteFacade.findLatestReleaseNotificationWithInAppEnabled().map(List::of).orElse(null));
        }

        userDto.setAreConversationsEnabled(chatSecurityService.areChatsAccessibleByEmployee(employee));
        if (userDto.getAreConversationsEnabled()) {
            try {
                userDto.setServiceConversationSid(chatService.getServiceConversationSid(employee));
                userDto.setIsDocuTrackEnabled(docutrackService.isDocutrackEnabled(employee));
            } catch (Exception ex) {
                logger.warn("Failed to generate Twilio info for {}", employee.getId(), ex);
            }
        }
        userDto.setAreVideoCallsEnabled(videoCallSecurityService.areVideoCallsAccessibleByEmployee(employee));
        userDto.setIsPaperlessHealthcareEnabled(paperlessHealthcareSecurityService.canView());
        return userDto;
    }

    @Override
    public PasswordComplexityRules getPasswordComplexityRules(Long organizationId, String companyId) {
        if (organizationId == null && companyId != null)
            organizationId = organizationDao.findBySystemSetup_LoginCompanyId(companyId, IdAware.class).getId();
        return employeePasswordSecurityService.getPasswordComplexityRules(organizationId);
    }

    @Override
    public void declineInvitation(String token) {
        invitationService.declineInvitation(token);
    }

    @Override
    public void validateInvitationToken(String token) {
        invitationService.validateInvitationToken(token);
    }

    @Override
    public void validateInvitationTokenExternal(String token) {
        invitationService.validateInvitationTokenExternal(token);
    }

    @Override
    public void validateResetPasswordRequestToken(String token) {
        passwordService.validateResetPasswordToken(token);
    }

    @Override
    public void processBadCredentialsError(String username, String companyId) {
        employeePasswordSecurityService.updateFailedCount(username, companyId);
    }

    @Override
    public long lockedMinutesLeft(String username, String companyId) {
        return employeePasswordSecurityService.lockedMinutesLeft(username, companyId);
    }

    @Override
    @Async
    //todo [contacts] why async?
    public void processSuccessfulLogin(Long employeeId) {
        employeePasswordSecurityService.unlockEmployeeAccount(employeeService.getEmployeeById(employeeId));
    }

    @Override
    public boolean validateLogin(String login, Long organizationId) {
        return !employeeService.existsLoginInOrganization(login, organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findActiveByLegacyIdAndLoginCompanyId(String legacyId, String loginCompanyId) {
        return employeeService.findActiveByLegacyIdAndLoginCompanyId(legacyId, loginCompanyId);
    }
}
