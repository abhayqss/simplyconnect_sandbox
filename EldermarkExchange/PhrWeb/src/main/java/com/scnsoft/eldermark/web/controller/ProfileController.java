package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.service.AvatarService;
import com.scnsoft.eldermark.service.ProfileService;
import com.scnsoft.eldermark.shared.exception.ValidationExceptionFactory;
import com.scnsoft.eldermark.shared.web.entity.AccountTypeDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * @author phomal
 * Created on 5/10/2017.
 */
@Api(value = "PHR - User Profile", description = "User Profile")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class)
})
@RestController
@RequestMapping("/phr/{userId:\\d+}/profile")
public class ProfileController {

    @Autowired
    ProfileService profileService;

    @Autowired
    AvatarService avatarService;

    final static int PHOTO_SIZE_LIMIT = 64 * 1024;    // 64K

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get profile types", notes = "Get a list of profile types for the specified user with their status (just one profile is marked as current).")
    @GetMapping(value = "/accountType")
    public Response<List<AccountTypeDto>> getActiveProfile(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final List<AccountTypeDto> dto = profileService.getAccountTypes(userId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Set active profile", notes = "Switch between account types (care team / patient). The state is stored at backend.")
    @PostMapping(value = "/accountType/{type:provider|consumer|notify|PROVIDER|CONSUMER|NOTIFY}")
    public Response<List<AccountTypeDto>> changeActiveProfile(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") Long userId,
            @ApiParam(value = "Account type", required = true, allowableValues = "provider, consumer, PROVIDER, CONSUMER")
            @PathVariable("type") String type
    ) {
        final AccountType.Type accountType = AccountType.Type.fromValue(type);
        final List<AccountTypeDto> dto = profileService.setActiveAccountType(userId, accountType);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get avatar", notes = "WARNING! Doesn't work in Swagger UI, try in POSTMAN.")
    @GetMapping(value = "/avatar.raw")
    public void getAvatar(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            HttpServletResponse response
    ) {
        avatarService.downloadAvatar(userId, response);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Upload avatar", notes = "Upload avatar photo")
    @PostMapping(value = "/avatar.raw")
    public Response uploadAvatar(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "new avatar photo. file size limit = 64 KB") @RequestPart("photo") MultipartFile photo
    ) {
        if (photo.getSize() > PHOTO_SIZE_LIMIT) {
            throw new MaxUploadSizeExceededException(PHOTO_SIZE_LIMIT);
        }
        avatarService.setAvatar(userId, photo);
        return Response.successResponse();
    }

    @ApiOperation(value = "Get avatar (Base64)", notes = "Get avatar as Base64 encoded image")
    @GetMapping(value = "/avatar")
    public Response<String> getAvatarInBase64(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final String avatarBase64 = avatarService.getAvatarAsBase64(userId);
        return Response.successResponse(avatarBase64);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Upload avatar (Base64)", notes = "Upload avatar photo as Base64 encoded image")
    @PostMapping(value = "/avatar")
    public Response uploadAvatarInBase64(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "new avatar photo (Base64 encoded image). max length is 65536 characters") @RequestParam("photo") String photo
    ) {
        if (photo.length() > PHOTO_SIZE_LIMIT) {
            throw new MaxUploadSizeExceededException(PHOTO_SIZE_LIMIT);
        }
        avatarService.setAvatar(userId, photo);
        return Response.successResponse();
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Delete avatar")
    @DeleteMapping(value = "/avatar")
    public Response<Boolean> deleteAvatar(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final Boolean dto = avatarService.deleteAvatar(userId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get location of personal address")
    @RequestMapping(value = "/personal/location", method = RequestMethod.GET)
    public @ResponseBody
    Response<CoordinatesDto> getPersonalProfileLocation(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final CoordinatesDto dto = profileService.getPersonalAddressCoordinates(userId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get personal profile")
    @GetMapping(value = "/personal")
    public Response<PersonalProfileDto> getPersonalProfile(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final PersonalProfileDto dto = profileService.getPersonalProfile(userId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Edit personal profile")
    @PutMapping(value = "/personal", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<PersonalProfileDto> editPersonalProfile(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @Valid @ApiParam(required = true) @RequestBody PersonalProfileEditDto body,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw ValidationExceptionFactory.fromBindingErrors(errors);
        }
        final PersonalProfileDto dto = profileService.editPersonalProfile(userId, body);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get professional profile", notes = "Returns a map of professional data (field name / field value approach).")
    @GetMapping(value = "/professional")
    public Response<ProfessionalProfileDto> getProfessionalProfile(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final ProfessionalProfileDto dto = profileService.getProfessionalProfile(userId);
        return Response.successResponse(dto);
    }

    @Deprecated
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Edit professional profile")
    @PutMapping(value = "/professional", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ProfessionalProfileDto> editProfessionalProfile(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(required = true) @RequestBody ProfessionalProfileDto body
    ) {
        final ProfessionalProfileDto dto = profileService.editProfessionalProfile(userId, body);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Deactivate profile")
    @DeleteMapping
    public Response<Boolean> deactivateProfile(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final Boolean dto = profileService.deactivateProfile(userId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get profile status", notes = "Returns user's account lock status.")
    @GetMapping(value = "/accountStatus")
    public Response<AccountStatusDto> getAccountStatus(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final AccountStatusDto dto = profileService.getAccountStatus(userId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Change password", notes = "Change password used for mobile app authentication")
    @PostMapping(value = "/password")
    public Response<Boolean> changePassword(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "previous password", required = true)
            @RequestParam(value = "oldPassword", required = true) char[] oldPassword,
            @ApiParam(value = "new password", required = true)
            @RequestParam(value = "newPassword", required = true) char[] newPassword
    ) {
        final Boolean dto = profileService.changePassword(userId, oldPassword, newPassword);
        return Response.successResponse(dto);
    }

}
