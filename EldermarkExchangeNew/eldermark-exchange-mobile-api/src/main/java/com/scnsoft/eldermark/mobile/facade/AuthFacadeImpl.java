package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.UserPrincipal;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.AuthException;
import com.scnsoft.eldermark.exception.EmployeeConfirmedStatusException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.mobile.dto.*;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordChangeDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordComplexityRuleDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordResetDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordResetRequestDto;
import com.scnsoft.eldermark.mobile.security.JwtTokenFacade;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.PasswordService;
import com.scnsoft.eldermark.service.docutrack.DocutrackService;
import com.scnsoft.eldermark.service.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.service.password.OrganizationPasswordSettingsService;
import com.scnsoft.eldermark.service.security.ChatSecurityService;
import com.scnsoft.eldermark.service.security.VideoCallSecurityService;
import com.scnsoft.eldermark.service.twilio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthFacadeImpl implements AuthFacade {

    private static final Logger logger = LoggerFactory.getLogger(AuthFacadeImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtTokenFacade jwtTokenFacade;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmployeePasswordSecurityService employeePasswordSecurityService;

    @Autowired
    private OrganizationPasswordSettingsService organizationPasswordSettingsService;

    @Autowired
    private Converter<Employee, UserDto> employeeToUserDtoConverter;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatSecurityService chatSecurityService;

    @Autowired
    private VideoCallSecurityService videoCallSecurityService;

    @Autowired
    private VideoCallService videoCallService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private DocutrackService docutrackService;

    @Autowired
    private TwilioAccessTokenService accessTokenService;

    @Override
    @Transactional
    public UserDto login(LoginDto loginDto) {
        Authentication authentication = authenticate(loginDto);

        var principal = (UserPrincipal) authentication.getPrincipal();
        checkPermissions(principal);

        var employee = employeeService.getEmployeeById(principal.getEmployeeId());

        processSuccessfulLogin(employee);

        var user = employeeToUserDtoConverter.convert(employee);
        user.setToken(jwtTokenFacade.generateToken(authentication));

        setChatVideoUserDetails(employee, user);
        return user;
    }

    private void checkPermissions(UserPrincipal principal) {
        var hasMobileAppAccessPermission = principal.getLinkedEmployeesPermissions()
                .getOrDefault(Permission.MOBILE_APP_ACCESS, List.of())
                .contains(principal.getEmployeeId());

        if (!hasMobileAppAccessPermission) {
            throw new AuthException(InternalServerExceptionType.APP_ACCESS_DENIED);
        }
    }

    private void setChatVideoUserDetails(Employee employee, ChatVideoUserDetails user) {
        user.setAreConversationsEnabled(chatSecurityService.areChatsAccessibleByEmployee(employee));
        if (user.getAreConversationsEnabled()) {
            try {
                user.setConversationAccessToken(chatService.generateToken(employee));
                user.setServiceConversationSid(chatService.getServiceConversationSid(employee));
                user.setIsDocuTrackEnabled(docutrackService.isDocutrackEnabled(employee));
            } catch (Exception ex) {
                logger.warn("Failed to generate Twilio info for {}", employee.getId(), ex);
            }
        }
        user.setAreVideoCallsEnabled(videoCallSecurityService.areVideoCallsAccessibleByEmployee(employee));
    }

    private Authentication authenticate(LoginDto loginDto) {
        try {
            var authentication = authenticationManager.authenticate(createUsernamePasswordAuthenticationToken(loginDto));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (BadCredentialsException e) {
            processBadCredentialsError(loginDto.getLogin(), loginDto.getCompanyCode());
            throw new AuthException(InternalServerExceptionType.AUTH_BAD_CREDENTIALS, e.getCause());
        } catch (LockedException e) {
            long lockedMinutesLeft = lockedMinutesLeft(loginDto.getLogin(), loginDto.getCompanyCode());
            throw new AuthException(String.format(InternalServerExceptionType.AUTH_ACCOUNT_LOCKED.getMessage(), lockedMinutesLeft),
                    InternalServerExceptionType.AUTH_ACCOUNT_LOCKED.getCode(), e.getCause(), InternalServerExceptionType.AUTH_ACCOUNT_LOCKED.getHttpStatus());
        } catch (CredentialsExpiredException e) {
            throw new AuthException(InternalServerExceptionType.AUTH_CREDENTIALS_EXPIRED, e.getCause());
        } catch (DisabledException e) {
            throw new AuthException(InternalServerExceptionType.AUTH_ACCOUNT_INACTIVE, e.getCause());
        } catch (InternalAuthenticationServiceException notFound) {
            if (notFound.getCause() instanceof EmployeeConfirmedStatusException) {
                throw new AuthException(InternalServerExceptionType.AUTH_ACCOUNT_CONFIRMED, notFound.getCause());
            }
            throw new AuthException(InternalServerExceptionType.AUTH_NOT_FOUND, notFound.getCause());
        }
    }

    private void processSuccessfulLogin(Employee employee) {
        employeePasswordSecurityService.unlockEmployeeAccount(employee);
    }

    private void processBadCredentialsError(String username, String companyId) {
        employeePasswordSecurityService.updateFailedCount(username, companyId);
    }

    private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(LoginDto loginDto) {
        return new UsernamePasswordAuthenticationToken(
                loginDto.getCompanyCode() + UserPrincipal.DELIMITER + loginDto.getLogin(),
                loginDto.getPassword()
        );
    }

    private long lockedMinutesLeft(String username, String companyId) {
        return employeePasswordSecurityService.lockedMinutesLeft(username, companyId);
    }

    @Override
    public void requestPasswordReset(PasswordResetRequestDto dto) {
        passwordService.requestPasswordReset(dto.getLogin(), dto.getCompanyCode());
    }

    @Override
    public void resetPassword(PasswordResetDto dto) {
        passwordService.resetPassword(dto.getToken(), dto.getPassword());
    }

    @Override
    public void changePassword(PasswordChangeDto dto) {
        Employee employee = employeeService.getEmployeeThatCanLogin(dto.getLogin(), dto.getCompanyCode());
        passwordService.changePassword(employee, dto.getOldPassword(), dto.getNewPassword());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordComplexityRuleDto> getPasswordComplexityRules(Long organizationId, String companyCode) {
        if (organizationId == null && companyCode != null)
            organizationId = organizationDao.findBySystemSetup_LoginCompanyId(companyCode, IdAware.class).getId();

        var settings = organizationPasswordSettingsService.getOrganizationPasswordSettings(organizationId, true)
                .stream()
                .filter(setting -> setting.getPasswordSettings().getPasswordSettingsType().hasRegexp())
                .map(setting -> {
                    var dto = new PasswordComplexityRuleDto();
                    var type = setting.getPasswordSettings().getPasswordSettingsType();

                    dto.setType(type.name());
                    dto.setDisplayName(type.getDisplayName(setting.getValue()));
                    dto.setRegexp(type.getRegexp(setting.getValue()).orElseThrow());
                    dto.setValue(setting.getValue());

                    return dto;
                })
                .collect(Collectors.toList());
        return settings;
    }

    @Override
    public RoomSidUserDto loginByRoomToken(RoomTokenLoginDto roomTokenLoginDto) {
        var roomToken = accessTokenService.parse(roomTokenLoginDto.getRoomToken());
        var employeeId = ConversationUtils.employeeIdFromIdentity(roomToken.getIdentity());
        var roomSid = VideoCallUtils.getRoomSidOrThrow(roomToken);
        var employee = employeeService.getEmployeeById(employeeId);
        if (!videoCallSecurityService.areVideoCallsAccessibleByEmployee(employee) ||
                !videoCallService.isCallActiveAndEmployeeOnCallOrHasIncomingCall(roomSid, employeeId)) {
            throw new AuthException(InternalServerExceptionType.AUTH_UNAUTHORIZED);
        }
        String token = jwtTokenFacade.generateTokenByRoomSid(employeeId, roomSid);
        RoomSidUserDto user = new RoomSidUserDto();
        user.setToken(token);
        user.setId(employee.getId());
        setChatVideoUserDetails(employee, user);
        return user;
    }
}
