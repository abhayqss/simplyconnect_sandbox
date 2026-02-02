package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.annotations.SwaggerDoc;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.UserPrincipal;
import com.scnsoft.eldermark.dto.ClientAccessibleUserDto;
import com.scnsoft.eldermark.dto.PasswordComplexityRules;
import com.scnsoft.eldermark.dto.UserDto;
import com.scnsoft.eldermark.dto.password.CreatePasswordExternalDto;
import com.scnsoft.eldermark.dto.password.PasswordChangeDto;
import com.scnsoft.eldermark.dto.password.PasswordResetDto;
import com.scnsoft.eldermark.dto.password.PasswordResetRequestDto;
import com.scnsoft.eldermark.dto.security.LoginDto;
import com.scnsoft.eldermark.dto.security.LoginSso4dDto;
import com.scnsoft.eldermark.dto.sso4d.LoginSso4dResponseDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.exception.AuthException;
import com.scnsoft.eldermark.exception.EmployeeConfirmedStatusException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.facade.AuthFacade;
import com.scnsoft.eldermark.facade.ClientFacade;
import com.scnsoft.eldermark.security.JwtTokenFacade;
import com.scnsoft.eldermark.security.Sso4DFacade;
import com.scnsoft.eldermark.service.security.LoggedUserDetailsService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static com.scnsoft.eldermark.service.CareCoordinationConstants.EXTERNAL_COMPANY_ID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenFacade jwtTokenFacade;

    @Autowired
    private AuthFacade authFacade;

    @Autowired
    private Sso4DFacade sso4DFacade;

    @Autowired
    private LoggedUserDetailsService userDetailsService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientFacade clientFacade;

    public static final String NO_TARGET_CLIENT_ID = "-1";

    @SwaggerDoc
    @PostMapping("/login")
    public Response<UserDto> login(@Valid @RequestBody LoginDto loginDto,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        return Response.successResponse(authenticateAndGenerateToken(loginDto, request, response));
    }

    @SwaggerDoc
    @PostMapping("/external/login")
    public Response<UserDto> loginExternal(@Valid @RequestBody LoginDto loginDto,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        loginDto.setCompanyId(EXTERNAL_COMPANY_ID);
        return Response.successResponse(authenticateAndGenerateToken(loginDto, request, response));
    }

    @SwaggerDoc
    @PostMapping("/sso4d")
    public Response<ClientAccessibleUserDto> sso4D(@Valid @RequestBody LoginSso4dDto loginDto,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        LoginSso4dResponseDto result = sso4DFacade.get4dLoginDetails(loginDto);
        return Response.successResponse(authenticateSso4dAndGenerateToken(loginDto, result, request, response));
    }

    @GetMapping("/user")
    public Response<UserDto> getCurrentUser() {
        var loggedEmployeeId = loggedUserService.getCurrentEmployeeId();
        var user = authFacade.getUser(loggedEmployeeId);
        return Response.successResponse(user);
    }

    private UserDto authenticateAndGenerateToken(LoginDto loginDto,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        Authentication authentication = authenticate(loginDto);
        String token = jwtTokenFacade.generateToken(authentication);

        var loggedEmployeeId = ((UserPrincipal) authentication.getPrincipal()).getEmployeeId();
        authFacade.processSuccessfulLogin(loggedEmployeeId);

        if (jwtTokenFacade.isAuthWithCookies(request)) {
            jwtTokenFacade.setTokenInCookie(request, response, token);
            token = null;
        }

        var user = authFacade.getUserWithConversationToken(loggedEmployeeId);
        user.setToken(token);
        return user;
    }

    private ClientAccessibleUserDto authenticateSso4dAndGenerateToken(LoginSso4dDto loginRequestDto,
                                                                      LoginSso4dResponseDto loginResponseDto,
                                                                      HttpServletRequest request,
                                                                      HttpServletResponse response) {
        validate4dSso(loginRequestDto, loginResponseDto);
        Employee employee = authFacade.findActiveByLegacyIdAndLoginCompanyId(loginRequestDto.getUserId(), loginRequestDto.getCompanyId());
        validateRole(employee);

        var loggedEmployeeId = employee.getId();
        UserDetails userDetails = userDetailsService.loadById(loggedEmployeeId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenFacade.generateToken(authentication);
        authFacade.processSuccessfulLogin(loggedEmployeeId);

        if (jwtTokenFacade.isAuthWithCookies(request)) {
            jwtTokenFacade.setTokenInCookie(request, response, token);
            token = null;
        }

        ClientAccessibleUserDto clientAccessibleUser = new ClientAccessibleUserDto();
        var user = authFacade.getUserWithConversationToken(loggedEmployeeId);
        user.setToken(token);
        clientAccessibleUser.setUser(user);
        if (loginRequestDto.getResNum() != null && !NO_TARGET_CLIENT_ID.equals(loginRequestDto.getResNum())) {
            Optional<IdAware> clientIdAware = clientFacade.findByLoginCompanyIdAndLegacyId(loginRequestDto.getCompanyId(), loginRequestDto.getResNum());
            clientAccessibleUser.setIsTargetClientSynced(clientIdAware.isPresent());
            clientIdAware.ifPresent(idAware -> {
                clientAccessibleUser.setTargetClientId(idAware.getId());
                clientAccessibleUser.setCanViewTargetClient(clientFacade.canView(idAware.getId()));
            });
        }
        return clientAccessibleUser;
    }

    private void validateRole(Employee employee) {
        if (employee.getCareTeamRole() == null) {
            throw new AuthException(InternalServerExceptionType.AUTH_ACCESS_DENIED);
        }
    }

    private void validate4dSso(LoginSso4dDto loginRequestDto, LoginSso4dResponseDto loginResponseDto) {
        if (loginResponseDto == null
                || loginResponseDto.getCount() != 1
                || BooleanUtils.isNotTrue(loginResponseDto.getLogins().get(0).getStillLoggedIn())
                || !loginRequestDto.getUserId().equals(loginResponseDto.getLogins().get(0).getUserId())) {
            throw new AuthException(InternalServerExceptionType.AUTH_ACCESS_DENIED);
        }
    }

    private Authentication authenticate(LoginDto loginDto) {
        try {
            var authentication = authenticationManager.authenticate(createUsernamePasswordAuthenticationToken(loginDto));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (BadCredentialsException e) {
            authFacade.processBadCredentialsError(loginDto.getUsername(), loginDto.getCompanyId());
            throw new AuthException(InternalServerExceptionType.AUTH_BAD_CREDENTIALS, e.getCause());
        } catch (LockedException e) {
            long lockedMinutesLeft = authFacade.lockedMinutesLeft(loginDto.getUsername(), loginDto.getCompanyId());
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

    private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(LoginDto loginDto) {
        return new UsernamePasswordAuthenticationToken(
                loginDto.getCompanyId() + UserPrincipal.DELIMITER + loginDto.getUsername(),
                loginDto.getPassword()
        );
    }

    @PostMapping("/logout")
    public Response<Void> logout(HttpServletRequest request,
                                 HttpServletResponse response) {
        jwtTokenFacade.removeTokenFromCookie(request, response);
        return Response.successResponse();
    }

    @PostMapping(value = "/password/reset-request")
    public Response<?> requestPasswordReset(@RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        authFacade.requestPasswordReset(passwordResetRequestDto);
        return Response.successResponse();
    }

    @PostMapping(value = "/external/password/reset-request")
    public Response<?> requestPasswordResetExternal(@RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        passwordResetRequestDto.setCompanyCode(EXTERNAL_COMPANY_ID);
        authFacade.requestPasswordReset(passwordResetRequestDto);
        return Response.successResponse();
    }

    @PostMapping(value = "/password/reset")
    public Response<?> resetPassword(@RequestBody PasswordResetDto dto) {
        authFacade.resetPassword(dto);
        return Response.successResponse();
    }


    @PostMapping(value = "/password/create")
    public Response<?> createPassword(@RequestBody PasswordResetDto dto) {
        authFacade.createPasswordAndActivateAccount(dto);
        return Response.successResponse();
    }

    @PostMapping(value = "/external/password/create")
    public Response<?> createPasswordExternal(@RequestBody CreatePasswordExternalDto dto) {
        authFacade.createPasswordAndActivateAccountExternal(dto);
        return Response.successResponse();
    }

    @PostMapping(value = "/password/change")
    public Response<?> changePassword(@RequestBody PasswordChangeDto dto) {
        authFacade.changePassword(dto);
        return Response.successResponse();
    }

    @PostMapping(value = "/external/password/change")
    public Response<?> changePasswordExternal(@RequestBody PasswordChangeDto dto) {
        dto.setCompany(EXTERNAL_COMPANY_ID);
        authFacade.changePassword(dto);
        return Response.successResponse();
    }

    @GetMapping(value = "/password/complexity-rules")
    public Response<PasswordComplexityRules> getPasswordComplexityRules(
            @RequestParam(required = false) Long organizationId, @RequestParam(required = false) String companyId) {
        return Response.successResponse(authFacade.getPasswordComplexityRules(organizationId, companyId));
    }

    @GetMapping(value = "/external/password/complexity-rules")
    public Response<PasswordComplexityRules> getExternalPasswordComplexityRules() {
        return Response.successResponse(authFacade.getPasswordComplexityRules(null, EXTERNAL_COMPANY_ID));
    }

    @GetMapping(value = "/session/validate")
    public Response<Boolean> validateToken(HttpServletRequest request) {
        var token = jwtTokenFacade.getJwtFromRequest(request);
        return Response.successResponse(jwtTokenFacade.validateToken(token));
    }

    @PostMapping(value = "/invitation-request/decline")
    public Response<?> declineInvitation(@RequestParam("token") String token) {
        authFacade.declineInvitation(token);
        return Response.successResponse();
    }

    @PostMapping(value = "/invitation-request/validate-token")
    public Response<Boolean> validateInvitationRequestToken(@RequestParam(value = "token") String token) {
        //todo [contacts] make return type Responce<Void>?
        authFacade.validateInvitationToken(token);
        return Response.successResponse(true);
    }

    @PostMapping(value = "/external/invitation-request/validate-token")
    public Response<Boolean> validateInvitationRequestTokenExternal(@RequestParam(value = "token") String token) {
        //todo [contacts] make return type Responce<Void>?
        authFacade.validateInvitationTokenExternal(token);
        return Response.successResponse(true);
    }

    @PostMapping(value = "/reset-password-request/validate-token")
    public Response<Boolean> validatePasswordResetRequestToken(@RequestParam(value = "token") String token) {
        //todo [contacts] make return type Responce<Void>?
        authFacade.validateResetPasswordRequestToken(token);
        return Response.successResponse(true);
    }

    @GetMapping(value = "/validate-login")
    public Response<Boolean> validateLogin(@RequestParam(value = "login") String login, @RequestParam(value = "organizationId") Long organizationId) {
        boolean loginValid = authFacade.validateLogin(login, organizationId);
        return Response.successResponse(loginValid);
    }
}
