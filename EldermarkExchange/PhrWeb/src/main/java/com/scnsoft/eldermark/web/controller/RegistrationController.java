package com.scnsoft.eldermark.web.controller;


import com.scnsoft.eldermark.dao.phr.chat.PhrChatUserDao;
import com.scnsoft.eldermark.entity.phr.chat.PhrChatHandset;
import com.scnsoft.eldermark.service.ProfileService;
import com.scnsoft.eldermark.service.UserRegistrationService;
import com.scnsoft.eldermark.service.chat.PhrChatService;
import com.scnsoft.eldermark.shared.validation.ExchangeLogin;
import com.scnsoft.eldermark.shared.validation.Phone;
import com.scnsoft.eldermark.shared.validation.Ssn;
import com.scnsoft.eldermark.shared.web.entity.*;
import com.scnsoft.eldermark.web.entity.*;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Registration process controller.
 * This layer validates request parameters and passes them to Service layer,
 * in case of successful execution it wraps result in response wrapper.
 * Request handlers declared here are documented by the means of Swagger Annotations.
 *
 * @author averazub
 * @author phomal
 * Created by averazub on 12/26/2016.
 */
@Api(value = "Registration", description = "Registration")
@Validated
@RestController
@RequestMapping("/register")
public class RegistrationController {

    Logger logger = Logger.getLogger(RegistrationController.class.getName());

    @Autowired
    UserRegistrationService userRegistrationService;

    @Autowired
    ProfileService profileService;
    
    @Autowired
    PhrChatService phrChatService;
    
    @Autowired
    PhrChatUserDao phrChatUserDao;
    
    @Value("${phr.chat.server.host.address}")
    private String phrChatURL;

    /**
     * Register. This method creates a new mobile User account with non-active status (active = false),
     * generates a random code and sends it via SMS to confirm the user phone number submitted during registration.
     *
     * @param ssn                   social security number
     * @param phone                 phone number
     * @param signupWithoutResident if set to false, request will throw “no.associated.patients.found” error, user will not be registered.
     *                              Otherwise, user will be registered without any associated records.
     *                              By default, {@code false}.
     */
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Forbidden", response = ResponseErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Step 1. Register new user (Sign Up as Consumer)",
            notes = "This method creates a new Mobile User account with non-active status (active = false), generates a random code and sends it via SMS to confirm the user phone number submitted during registration.\nDefault account type is CONSUMER, but if there's any matching Contact exists in WEB Simply Connect system or if Web Access is configured at the 3rd step, then this user becomes linked to the Contact and gets PROVIDER account type.")
    @PostMapping(value = "/signup")
    public Response<RegistrationAnswerDto> register(
            @Ssn
            @NotBlank
            @ApiParam(value = "social security number", example = "123456789", required = true)
            @RequestParam("ssn") String ssn,
            @Size(max = 50)
            @Phone
            @ApiParam(value = "phone number", example = "1234567890", required = true)
            @RequestParam("phone") String phone,
            @Email
            @Size(min = 3, max = 150)
            @ApiParam(value = "email", example = "test@mail.com", required = true)
            @RequestParam("email") String email,
            @DecimalMin(value = "-720")
            @DecimalMax(value = "720")
            @ApiParam(value = "timeZoneOffset", example = "180", required = true)
            @RequestParam("timeZoneOffset") String timeZoneOffset,
            @Size(min = 2, max = 255)
            @ApiParam(value = "first name")
            @RequestParam(value = "firstName", required = false) String firstName,
            @Size(min = 2, max = 255)
            @ApiParam(value = "last name")
            @RequestParam(value = "lastName", required = false) String lastName,
            @ApiParam(value = "If set to false and no associated residents present in the system, request will throw “no.associated.patients.found” error, user will not be registered. Otherwise, user will be registered without any associated records.", defaultValue = "false")
            @RequestParam(name = "signupWithoutResident", required = false, defaultValue = "false") Boolean signupWithoutResident
    ) {
        final RegistrationAnswerDto dto = userRegistrationService.signupNewUser(ssn, phone, email, timeZoneOffset, firstName, lastName, signupWithoutResident);
        return Response.successResponse(dto);
    }

    /**
     * Register provider. This method creates a new Mobile User account with non-active status (active = false) and account type of PROVIDER,
     * generates a random code and sends it via SMS to confirm the user phone number submitted during registration. <br/>
     * The registered user (if approved) will have access to WEB Simply Connect system.
     *
     * @param phone phone number
     */
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Step 1. Register new user (Sign Up as Provider)",
            notes = "This method creates a new Mobile User account with non-active status (active = false) and account type of PROVIDER, generates a random code and sends it via SMS to confirm the user phone number submitted during registration. <br/>The registered user (if approved) will have access to WEB Simply Connect system and will be searchable in Physicians list.\nNOTE! Doesn't work in Swagger UI.")
    @PostMapping(value = "/signup/provider",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<RegistrationAnswerDto> registerProvider(
            @Size(max = 50)
            @Phone
            @ApiParam(value = "phone number", required = true)
            @RequestParam(value = "phone", required = true) String phone,
            @Size(min = 3, max = 150)
            @Email
            @ApiParam(value = "email", required = true)
            @RequestParam(value = "email", required = true) String email,
            @DecimalMin(value = "-720")
            @DecimalMax(value = "720")
            @ApiParam(value = "Timezone Offset", required = true)
            @RequestParam(value = "timeZoneOffset", required = true) String timeZoneOffset,
            @Size(max = 40)
            @Phone
            @ApiParam(value = "fax")
            @RequestParam(value = "fax", required = false) String fax,
            @Size(min = 2, max = 255)
            @ApiParam(value = "first name", defaultValue = "Gregory")
            @RequestParam(value = "firstName", required = false) String firstName,
            @Size(min = 2, max = 255)
            @ApiParam(value = "last name", defaultValue = "House")
            @RequestParam(value = "lastName", required = false) String lastName,
            @Size(max = 255)
            @ApiParam(value = "street address")
            @RequestParam(value = "address.street", required = false) String addressStreet,
            @Size(max = 128)
            @ApiParam(value = "city")
            @RequestParam(value = "address.city", required = false) String addressCity,
            @Pattern(regexp = "^\\d{5}$")
            @ApiParam(value = "USA zip code (5 digits)", defaultValue = "55343")
            @RequestParam(value = "address.postalCode", required = false) String addressPostalCode,
            @ApiParam(value = "US state (abbreviation)", allowableValues = "AL, AZ, CA, CT, FL, GA, ID, IN, KS, NH, NM, ND, OK, OR, RI, SD, TN, UT, VA, WV, WY, ME, MA, MN, MO, NE, NV, NJ, NY, NC, OH, PA, SC, VT, WA, WI, AK, AR, CO, DE, HI, IL, IA, KY, LA, MD, MI, MS, MT, TX")
            @RequestParam(value = "address.state", required = false) String addressState,
            @Size(min = 1)
            @ApiParam(value = "Specialities (array of IDs)", required = true)
            @RequestParam(value = "professional.specialities", required = true) List<Long> specialities,
            @Size(min = 1)
            @ApiParam(value = "In-Network Insurances (array of IDs)", required = true)
            @RequestParam(value = "professional.inNetworkInsurances", required = true) List<Long> inNetworkInsurances,
            @Size(max = 255)
            @ApiParam(value = "Hospital Name", required = true)
            @RequestParam(value = "professional.hospitalName", required = true) String hospitalName,
            @Size(max = 255)
            @ApiParam(value = "Professional Statement", required = true)
            @RequestParam(value = "professional.professionalStatement", required = true) String professionalStatement,
            @Size(max = 255)
            @ApiParam(value = "Education")
            @RequestParam(value = "professional.education", required = false) String education,
            @Size(max = 255)
            @ApiParam(value = "Board of certifications")
            @RequestParam(value = "professional.boardOfCertifications", required = false) String boardOfCertifications,
            @Size(max = 255)
            @ApiParam(value = "Professional Memberships")
            @RequestParam(value = "professional.professionalMemberships", required = false) String professionalMemberships,
            @Size(max = 255)
            @ApiParam(value = "NPI")
            @RequestParam(value = "professional.npi", required = false) String npi,
            @Size(max = 8, message = "The amount of attachments is limited to 8 items.")
            @ApiParam(value = "file detail", allowMultiple = true)
            @RequestPart("files") List<MultipartFile> files
    ) {
        // TODO refactor parameters binding?
        // TODO parameter `fax` is duplicated in two DTOs. remove any.
        AddressEditDto addressEditDto = new AddressEditDto();
        addressEditDto.setStreetAddress(addressStreet);
        addressEditDto.setCity(addressCity);
        addressEditDto.setState(addressState);
        addressEditDto.setPostalCode(addressPostalCode);

        ProfessionalProfileDto professionalProfileDto = ProfessionalProfileDto.Builder.aProfessionalProfileDto()
                .withProfessionalMembership(professionalMemberships)
                .withProfessionalStatement(professionalStatement)
                .withBoardOfCertifications(boardOfCertifications)
                .withEducation(education)
                .withFax(fax)
                .withNpi(npi)
                .withHospitalName(hospitalName)
                .withInNetworkInsurancesIds(inNetworkInsurances)
                .withSpecialitiesIds(specialities)
                .build();

        ProviderRegistrationForm form = ProviderRegistrationForm.Builder.aProviderRegistrationForm()
                .withPhone(phone)
                .withEmail(email)
                .withTimeZoneOffset(timeZoneOffset)
                .withFax(fax)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withAddress(addressEditDto)
                .withProfessional(professionalProfileDto)
                .withFiles(files)
                .build();

        final RegistrationAnswerDto dto = userRegistrationService.signupNewUser(form);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Bad Credentials", response = ResponseErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Forbidden", response = ResponseErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Step 1. Register new user (Sign Up via \"I already have an account in Web S.C.\")",
            notes = "This method creates a new Mobile User account with non-active status (active = false), generates a random code and sends it via SMS to confirm the user phone number added via WEB Simply Connect system during Contact creation.\nIf `login` matches email of an existing user from the same organization, then this user becomes linked to the Web contact (no new records created in this case).\nNew user's account type is PROVIDER, existing user's account type may be PROVIDER or PROVIDER + CONSUMER.\nThe 401 - \"Bad credentials\" response indicates that authorization has been refused for those credentials (check `companyId` / `login` / `password` parameters and try again)")
    @PostMapping(value = "/signup/fromweb")
    public Response<TelecomsDto> registerFromWeb(
            @Size(max = 10)
            @ApiParam(value = "company id (e.g. RBA), max length = 10 characters, CAN NOT contain slash character (`/`)", required = true)
            @RequestParam(value = "companyId", required = true) String companyId,
            @ExchangeLogin
            @Size(max = 150)
            @ApiParam(value = "login (usually it's email, but in general it can be any string), CAN NOT contain slash character (`/`)", required = true)
            @RequestParam(value = "login", required = true) String login,
            @ApiParam(value = "password used for web authentication, max length = 255 characters", required = true)
            @RequestParam(value = "password", required = true) char[] password,
            @DecimalMin(value = "-720")
            @DecimalMax(value = "720")
            @ApiParam(value = "Timezone Offset - the time difference between UTC time and local time, in minutes (example -120 for GMT+2; affects events filtering by date in `GET /phr/{userId}/events`)", required = true)
            @RequestParam(value = "timeZoneOffset", required = true) String timeZoneOffset
    ) {
        final TelecomsDto dto = userRegistrationService.signupUserAsWebEmployee(companyId, login, password, timeZoneOffset);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Step 3 (optional). Create account for access to WEB-based S.C.",
            notes = "This method creates a password for new Web User account (web system role = Person Receiving Services).")
    @PostMapping(value = "/webaccount")
    public Response registerWebAccount(
            @ExchangeLogin
            @Size(min = 3, max = 70)
            @ApiParam(value = "login (email used for registration), CAN NOT contain slash character (`/`)", required = true)
            @RequestParam(value = "login", required = true) String login,
            @Size(min = 8, max = 255)
            @ApiParam(value = "password used for web authentication, max length = 255 characters", required = true)
            @RequestParam(value = "password", required = true) char[] password
    ) {
        userRegistrationService.registerWebEmployeeConsumer(login, password);
        return Response.successResponse();
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Step 3 (optional). Create account for access to WEB-based S.C.",
            notes = "This method creates a password for Web User account.")
    @PostMapping(value = "/webaccount/provider")
    public Response registerProviderWebAccount(
            @ExchangeLogin
            @Size(min = 3, max = 70)
            @ApiParam(value = "login (email used for registration), CAN NOT contain slash character (`/`)", required = true)
            @RequestParam(value = "login", required = true) String login,
            @Size(min = 8, max = 255)
            @ApiParam(value = "password used for web authentication, max length = 255 characters", required = true)
            @RequestParam(value = "password", required = true) char[] password
    ) {
        userRegistrationService.registerWebEmployeeProvider(login, password);
        return Response.successResponse();
    }

    /**
     * Resend registration code. Generate a new random code and send it via SMS in order to confirm the phone number used during registration.
     *
     * @param ssn   social security number
     * @param phone phone number
     */
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Step 2. Resend registration code", notes = "Generate a new random code and send it via SMS in order to confirm the phone number used during registration.")
    @PostMapping(value = "/regenerate")
    public Response resendRegistrationCode(
            @Ssn
            @ApiParam(value = "social security number. May be null in case of physician (provider) registration", example = "123456789", required = false)
            @RequestParam(value = "ssn", required = false) String ssn,
            @Phone
            @ApiParam(value = "phone number", example = "1234567890", required = true)
            @RequestParam("phone") String phone,
            @Email
            @ApiParam(value = "email", example = "test@mail.com", required = true)
            @RequestParam("email") String email,
            @ApiParam(value = "Timezone Offset (not used)", required = false)
            @RequestParam(value = "timeZoneOffset", required = false) String timeZoneOffset
    ) {
        userRegistrationService.reGenerateCode(ssn, phone, email);
        return Response.successResponse();
    }

    /**
     * Confirm phone number. Once phone number is confirmed the account becomes active.
     *
     * @param ssn   social security number. May be null in case of physician (provider) registration
     * @param phone phone number
     * @param code  confirmation code
     */
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Step 2. Confirm phone number", notes = "Once phone number is confirmed user can proceed with the registration. Confirmation code lifetime is 15 minutes.")
    @PostMapping(value = "/confirm")
    public Response confirmRegistration(
            @Ssn
            @ApiParam(value = "social security number. May be null in case of physician (provider) registration", example = "123456789", required = false)
            @RequestParam(value = "ssn", required = false) String ssn,
            @Phone
            @ApiParam(value = "phone number", example = "1234567890", required = true)
            @RequestParam("phone") String phone,
            @Email
            @ApiParam(value = "email", example = "test@mail.com", required = true)
            @RequestParam("email") String email,
            @ApiParam(value = "Timezone Offset (not used)", required = false)
            @RequestParam(name = "timeZoneOffset", required = false) String timeZoneOffset,
            @ApiParam(value = "confirmation code", example = "1234", required = true)
            @RequestParam("code") String code
    ) {
        userRegistrationService.confirmRegistration(ssn, phone, email, code);
        return Response.successResponse();
    }

    /**
     * Generate access token.
     *
     * @param ssn   social security number. May be null in case of physician (provider) registration
     * @param phone phone number
     */
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Step 4 (final). Save password",
            notes = "Finish registration. Generate access token and activate mobile user account. Create resident record in Unaffiliated org (if none). Create active web account (if none) with password submitted at step 3. Send an email containing the credentials to access WEB system. Notify already registered friends/family members (if any of them invited you) awaiting your registration.\n" +
                    "### Warning!\n" +
                    "This endpoint is not intended for password / passcode change for a registered user. To change password after registration use `POST /phr/{userId}/profile/password`.")
    @PostMapping(value = "/savepass")
    public ResponseWithToken<UserDTO> savePassword(
            @Ssn
            @ApiParam(value = "social security number. May be null in case of physician (provider) registration", example = "123456789", required = false)
            @RequestParam(value = "ssn", required = false) String ssn,
            @Phone
            @ApiParam(value = "phone number", example = "1234567890", required = true)
            @RequestParam("phone") String phone,
            @Email
            @ApiParam(value = "email", example = "test@mail.com", required = true)
            @RequestParam("email") String email,
            @ApiParam(value = "Timezone Offset (not used)", required = false)
            @RequestParam(name = "timeZoneOffset", required = false) String timeZoneOffset,
            @Size(min = 4, max = 255)
            @ApiParam(value = "password that will be used for mobile app authentication, max length = 255 characters", required = false)
            @RequestParam(value = "password", required = false) char[] password,
            @ApiParam(value = "push notification token")
            @RequestParam(value="pnToken" , required = false) String pushNotificationToken,
            @ApiParam(value = "ios, android")
            @RequestParam(value = "deviceType", required = false) String deviceType
    ) {
        Token token = userRegistrationService.savePassword(ssn, phone, email, password);
        UserDTO userData = userRegistrationService.getUserDataBrief(token.getUserId());
        List<AccountTypeDto> accountTypes = userRegistrationService.getUserAccounts(token.getUserId());
        
        if(phrChatUserDao.findByNotifyUserId(token.getUserId()) == null) {
            phrChatService.userCreatePhrChat(token.getUserId());
        }        
        userData.setChatUserId(phrChatUserDao.findByNotifyUserId(token.getUserId()).getId());
        userData.setChatServer(phrChatService.getLoginAuthTokenFromChatServer(token.getUserId(),token.getUuid(),pushNotificationToken, deviceType));
        userData.setChatUrl(phrChatURL); 
        PhrChatHandset phrChatHandset = new PhrChatHandset();
        phrChatHandset.setPnToken(pushNotificationToken);
        phrChatHandset.setType(deviceType);
        phrChatHandset.setUuid(token.getUuid());
        phrChatHandset.setCompany(null);
        return ResponseWithToken.tokenResponse(token, userData, accountTypes);
    }

}
