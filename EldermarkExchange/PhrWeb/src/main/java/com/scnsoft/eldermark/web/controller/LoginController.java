package com.scnsoft.eldermark.web.controller;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Logger;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.scnsoft.eldermark.dao.phr.chat.PhrChatUserDao;
import com.scnsoft.eldermark.entity.phr.chat.PhrChatHandset;
import com.scnsoft.eldermark.service.HealthProviderService;
import com.scnsoft.eldermark.service.UserRegistrationService;
import com.scnsoft.eldermark.service.chat.PhrChatService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.service.AuthTokenService;
import com.scnsoft.eldermark.shared.web.entity.AccountTypeDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseWithToken;
import com.scnsoft.eldermark.shared.web.entity.Token;
import com.scnsoft.eldermark.web.entity.UserDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author averazub
 * @author phomal Created by averazub on 1/3/2017.
 */
@Api(value = "Authentication", description = "Authentication")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class) })
@Validated
@RestController
@RequestMapping("/auth")
public class LoginController {

    Logger logger = Logger.getLogger(LoginController.class.getName());

    @Autowired
    UserRegistrationService userRegistrationService;

    @Autowired
    AuthTokenService tokenAuthService;

    @Autowired
    HealthProviderService healthProviderService;

    @Autowired
    PhrChatService phrChatService;

    @Autowired
    PhrChatUserDao phrChatUserDao;

    @Value("${phr.chat.server.host.address}")
    private String phrChatURL;

    @ApiOperation("Validate token")
    @PostMapping(value = "/validateToken")
    public Response validateToken(@RequestHeader(value = "X-App-Ver", required = false) String appVersion,
            @ApiParam(value = "token", required = true) @RequestParam("token") String tokenStr) {
        if ("1.0b".equalsIgnoreCase(appVersion))
            throw new PhrException(PhrExceptionType.APP_VERSION_NOT_SUPPORTED);
        if (!tokenAuthService.validate(tokenStr))
            throw new PhrException(PhrExceptionType.INVALID_TOKEN);
        return Response.successResponse();
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class) })
    @ApiOperation("Login")
    @PostMapping(value = "/login", produces = "application/json")
    public ResponseWithToken<UserDTO> login(
            @ApiParam(value = "token", required = true) @RequestParam("token") String tokenStr,
            @ApiParam(value = "password used for mobile app authentication", required = false) @RequestParam(value = "password", required = false) char[] password,
            @DecimalMin(value = "-720") @DecimalMax(value = "720") @ApiParam(value = "Timezone Offset - the time difference between UTC time and local time, in minutes (example -120 for GMT+2)") @RequestPart(value = "timeZoneOffset", required = false) String timeZoneOffset,
            @ApiParam(value = "push notification token for chat server") @RequestParam(value = "pnToken", required = false) String pushNotificationToken,
            @ApiParam(value = "ios, android") @RequestParam(value = "deviceType", required = false) String deviceType) {
        Token token = tokenAuthService.validateTokenOrThrow(tokenStr);
        userRegistrationService.validatePasswordOrThrow(token.getUserId(), password);

        userRegistrationService.updateTimeZone(token.getUserId(), timeZoneOffset);
        UserDTO userData = userRegistrationService.getUserDataBrief(token.getUserId());
        List<AccountTypeDto> accountTypes = userRegistrationService.getUserAccounts(token.getUserId());

        if (phrChatUserDao.findByNotifyUserId(token.getUserId()) == null) {
            phrChatService.userCreatePhrChat(token.getUserId());
        }

        userData.setChatUserId(phrChatUserDao.findByNotifyUserId(token.getUserId()).getId());
        userData.setChatServer(phrChatService.getLoginAuthTokenFromChatServer(token.getUserId(), token.getUuid(),
                pushNotificationToken, deviceType));
        userData.setChatUrl(phrChatURL);
        PhrChatHandset phrChatHandset = new PhrChatHandset();
        phrChatHandset.setPnToken(pushNotificationToken);
        phrChatHandset.setType(deviceType);
        phrChatHandset.setUuid(token.getUuid());
        phrChatHandset.setCompany(null);

        userData.setPhrChatHandset(phrChatHandset);

        // healthProviderService.updateUserResidentRecords(token.getUserId());
        return ResponseWithToken.tokenResponse(token, userData, accountTypes);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class) })
    @ApiOperation("current-user")
    @GetMapping(value = "/current-user", produces = "application/json")
    public ResponseWithToken<UserDTO> getCurrentUser(
            @ApiParam(value = "token", required = true) @RequestParam("token") String tokenStr,
            @ApiParam(value = "ios, android") @RequestParam(value = "deviceType", required = true) String deviceType,
            @ApiParam(value = "push notification token for chat server") @RequestParam(value = "pnToken", required = false) String pnToken) {
        Token token = tokenAuthService.validateTokenOrThrow(tokenStr);
        UserDTO userData = userRegistrationService.getUserDataBrief(token.getUserId());
        List<AccountTypeDto> accountTypes = userRegistrationService.getUserAccounts(token.getUserId());
        if (phrChatUserDao.findByNotifyUserId(token.getUserId()) == null) {
            phrChatService.userCreatePhrChat(token.getUserId());
        }
        userData.setChatUserId(phrChatUserDao.findByNotifyUserId(token.getUserId()).getId());
        userData.setChatServer(phrChatService.getLoginAuthTokenFromChatServer(token.getUserId(), token.getUuid(),
                pnToken, deviceType));
        userData.setChatUrl(phrChatURL);
        PhrChatHandset phrChatHandset = new PhrChatHandset();
        phrChatHandset.setType(deviceType);
        phrChatHandset.setUuid(token.getUuid());
        userData.setPhrChatHandset(phrChatHandset);
        return ResponseWithToken.tokenResponse(token, userData, accountTypes);
    }
}
