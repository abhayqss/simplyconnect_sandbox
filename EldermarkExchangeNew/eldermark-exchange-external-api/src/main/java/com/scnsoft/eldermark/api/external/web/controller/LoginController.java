package com.scnsoft.eldermark.api.external.web.controller;

import com.scnsoft.eldermark.api.external.service.UserRegistrationService;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.api.shared.service.AuthTokenService;
import com.scnsoft.eldermark.api.shared.web.dto.*;
import com.scnsoft.eldermark.api.external.web.dto.UserAppDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

/**
 * @author phomal
 * Created by phomal on 1/19/2018.
 */
@Api(value = "Authentication", description = "Authentication")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/auth")
public class LoginController {

    Logger logger = Logger.getLogger(LoginController.class.getName());

    @Autowired
    UserRegistrationService userRegistrationService;

    @Autowired
    AuthTokenService authTokenService;

    @ApiOperation(value = "Validate token", notes = "This step is not mandatory, but recommended. Once per session, prior to calling other endpoints make a call to `POST /auth/login` OR `POST /auth/validateToken`. Successful response from any of these endpoints indicates that:  \n" +
            " <ul><li><p>SimplyHIE REST service is up & running</p></li>\n" +
            " <li><p>Supplied token is valid</p></li><ul>")
    @PostMapping(value = "/validateToken")
    public Response validateToken(
            @ApiParam(value = "token", required = true)
            @RequestParam("token") String tokenStr
    ) {
        if (!authTokenService.validate(tokenStr)) throw new PhrException(PhrExceptionType.INVALID_TOKEN);
        return Response.successResponse();
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Login", notes = "This step is not mandatory, but recommended. Once per session, prior to calling other endpoints make a call to `POST /auth/login` OR `POST /auth/validateToken`. Successful response from any of these endpoints indicates that:  \n" +
            " <ul><li><p>SimplyHIE REST service is up & running</p></li>\n" +
            " <li><p>Supplied token is valid</p></li><ul>")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWithToken<UserAppDTO> login(
            @ApiParam(value = "token", required = true)
            @RequestParam("token") String tokenStr,
            @DecimalMin(value = "-720")
            @DecimalMax(value = "720")
            @ApiParam(value = "Timezone Offset - the time difference between UTC time and local time, in minutes (example -120 for GMT+2). Specify it only if you want to change the value submitted during registration.")
            @RequestParam(value = "timeZoneOffset", required = false) String timeZoneOffset
    ) {
        Token token = authTokenService.validateTokenOrThrow(tokenStr);

        userRegistrationService.updateTimeZone(token.getUserId(), timeZoneOffset);
        UserAppDTO userData = userRegistrationService.getAppDataBrief(token.getUserId());
        return ResponseWithToken.tokenResponse(token, userData);
    }
}
