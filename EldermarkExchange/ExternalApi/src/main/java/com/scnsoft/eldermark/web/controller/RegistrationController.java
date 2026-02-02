package com.scnsoft.eldermark.web.controller;


import com.scnsoft.eldermark.service.UserRegistrationService;
import com.scnsoft.eldermark.shared.validation.Phone;
import com.scnsoft.eldermark.shared.validation.Uuid;
import com.scnsoft.eldermark.shared.web.entity.*;
import com.scnsoft.eldermark.web.entity.*;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

/**
 * Registration process controller.
 * This layer validates request parameters and passes them to Service layer,
 * in case of successful execution it wraps result in response wrapper.
 * Request handlers declared here are documented by the means of Swagger Annotations.
 *
 * @author phomal
 * Created by phomal on 1/15/2018.
 */
@Api(value = "Registration", description = "Registration (public access)")
@Validated
@RestController
@RequestMapping("/register")
public class RegistrationController {

    Logger logger = Logger.getLogger(RegistrationController.class.getName());

    @Autowired
    UserRegistrationService userRegistrationService;

    /**
     * Register new application (Sign Up as Third-Party application)
     */
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Step 1. Register new application (Sign Up as Third-Party application)",
            notes = "This method creates a new User account with non-active status (active = false) and account type of APPLICATION.<br/>All applications are reviewed by Administration (SimplyHIE dev team).<br/>The registered user (if approved) will have access to Simply Connect external API.<br/>Before the registration is completed use this method to resubmit it with a different data if necessary.")
    @PostMapping(value = "/signup/application")
    public Response<RegistrationAnswerDto> registerApplication(
            @Size(min = 2, max = 255)
            @ApiParam(value = "application name", required = true)
            @RequestParam(value = "appName") String appName,
            @Size(max = 500)
            @ApiParam(value = "application description")
            @RequestParam(value = "appDescription", required = false) String appDescription,
            @Size(max = 50)
            @Phone
            @ApiParam(value = "phone number (max length 50)")
            @RequestParam(value = "phone", required = false) String phone,
            @Size(min = 3, max = 255)
            @Email
            @ApiParam(value = "email (min length 3, max length 255)")
            @RequestParam(value = "email", required = false) String email,
            @DecimalMin(value = "-720")
            @DecimalMax(value = "720")
            @ApiParam(value = "Timezone Offset - the time difference between UTC time and local time, in minutes (example -120 for GMT+2; affects events filtering by date in `GET /phr/{userId}/events`)", defaultValue = "0")
            @RequestParam(value = "timeZoneOffset", required = false, defaultValue = "0") String timeZoneOffset
    ) {
        final RegistrationAnswerDto dto = userRegistrationService.signupNew3rdPartyAppUser(phone, email, appName, appDescription, timeZoneOffset);
        return Response.successResponse(dto);
    }

    /**
     * Complete the registration and generate access token
     */
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Step 2 (final). Complete the registration and generate access token",
            notes = "Call this method once to get access token when your registration application is approved. After the registration is completed send your access token in `X-Auth-Token` header with any request that requires authentication.")
    @PostMapping(value = "/complete")
    public ResponseWithToken<UserAppDTO> completeRegistration(
            @ApiParam(value = "registration flow guid (returned in response to `POST /register/signup/application`)", required = true)
            @Uuid
            @RequestParam(value = "flowId", required = true) String flowId,
            @ApiParam(value = "application name (the same as was specified in request to `POST /register/signup/application`)", required = true)
            @RequestParam(value = "appName", required = true) String appName
    ) {
        final Token token = userRegistrationService.complete(flowId, appName);
        final UserAppDTO userData = userRegistrationService.getAppDataBrief(token.getUserId());
        return ResponseWithToken.tokenResponse(token, userData);
    }

}
