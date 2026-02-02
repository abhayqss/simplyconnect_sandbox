package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.CareTeamRelation;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.service.CareTeamService;
import com.scnsoft.eldermark.service.VideoCallNucleusService;
import com.scnsoft.eldermark.shared.validation.Phone;
import com.scnsoft.eldermark.shared.validation.Ssn;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.*;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;
import static com.scnsoft.eldermark.shared.utils.PaginationUtils.lazyTotalCount;

/**
 * @author phomal
 * Created on 5/2/2017.
 */
@Api(value = "PHR - Care Team", description = "Care Team Member information")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/phr/{userId:\\d+}/careteam")
public class CareTeamController {

    final CareTeamService careTeamService;
    private final VideoCallNucleusService videoCallNucleusService;

    @Autowired
    public CareTeamController(CareTeamService careTeamService, VideoCallNucleusService videoCallNucleusService) {
        this.careTeamService = careTeamService;
        this.videoCallNucleusService = videoCallNucleusService;
    }

    @ApiOperation(value = "Get access rights of a specific care team member")
    @GetMapping(value = "/{contactId:\\d+}/accessRights")
    public Response<Map<AccessRight.Code, Boolean>> getCareteamMemberAccessRights(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId
    ) {
        final Map<AccessRight.Code, Boolean> dto = careTeamService.getAccessRights(userId, contactId);
        Boolean isEditable = careTeamService.canManageAccessRights(contactId);
        return Response.successResponse(dto, isEditable);
    }

    @ApiOperation(value = "Change access rights of a specific care team member",
            notes = "Patient can manage the access rights only for people added by themselves both family/friend member and medical staff. Medications list is dependent on My Personal Health Record screen so it's not allowed to have the former enabled and the latter disabled at the same time (i.e. `{ MEDICATIONS: true, MY_PHR: false }` - bad combination).")
    @PostMapping(value = "/{contactId:\\d+}/accessRights", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Map<AccessRight.Code, Boolean>> editCareteamMemberAccessRights(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId,
            @ApiParam(value = "map of access rights (Map<String, Boolean>)", required = true) @RequestBody Map<AccessRight.Code, Boolean> accessRightsMap
    ) {
        careTeamService.updateAccessRights(userId, contactId, accessRightsMap);
        final Map<AccessRight.Code, Boolean> dto = careTeamService.getAccessRights(userId, contactId);
        return Response.successResponse(dto, true);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get a list of recent activities with read/unread status.",
            notes = "(Implementation Notes) See 4 possible ActivityDTOs in the Models section. Due to limitations of Swagger API it's not possible to specify polymorphic models in a response.<br>Read/unread status works for events only.")
    @GetMapping(value = "/{contactId:\\d+}/activitylog")
    public Response<? extends List<? extends ActivityDto>> getCareteamMemberActivities(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") final Long contactId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in activity log (if not specified, system will return last 100 activities)", defaultValue = "100")
            @RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize,
            @Min(0)
            @ApiParam(value = "Activity log page. The first page is 0, the second page is 1, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final List<? extends ActivityDto> dto = careTeamService.getRecentActivity(userId, contactId, pageable);
        final Long count = lazyTotalCount(dto.size(), page, pageSize, new Callable<Long>() {
            @Override
            public Long call() {
                return careTeamService.getRecentActivityCount(userId, contactId);
            }
        });
        return Response.pagedResponse(dto, count);
    }

    @ApiOperation(value = "Request to log a new activity",
            notes = "(Implementation Notes) See ActivityDTOs in the Models section. Due to limitations of Swagger API it's not possible to specify polymorphic models in a response.")
    @PostMapping(value = "/{contactId:\\d+}/activitylog", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public <T extends ActivityDto> Response<? extends List<? extends ActivityDto>> logCareteamMemberActivity(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId,
            @ApiParam(value = "Activity (Should be either CALL activity or VIDEO activity)", required = true) @RequestBody T activityDto
    ) {
        careTeamService.logActivity(userId, contactId, activityDto);
        final Pageable pageable = buildPageable(0, 100);
        final List<? extends ActivityDto> dto = careTeamService.getRecentActivity(userId, contactId, pageable);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a specific care team member")
    @GetMapping(value = "/{contactId:\\d+}")
    public Response<CareteamMemberDto> getCareteamMember(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId,
            @ApiParam(value = "chat token", required = false) @RequestParam("chatToken") String chatToken
    ) {
        final CareteamMemberDto dto = careTeamService.getUserCareTeamMember(userId, contactId, chatToken);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a list of care team members",
            notes = "Send `Accept application/vnd.ctm-v2+json` in headers for receiving a list of CTM DTO v2.<br/>Send `Accept application/json` or skip `Accept` header for receiving a list of CTM DTO v1.\n### Sorting rules\n * The items of the list are sorted alphabetically by name (from A to Z).")
    @GetMapping(headers = "accept=application/json", produces = "application/json")
    public Response<List<CareteamMemberDto>> getCareteamMembers(
            @ApiParam(value = "chat token", required = false) @RequestParam(value ="chatToken", required = false) String chatToken,
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final List<CareteamMemberDto> dto = careTeamService.getUserCareTeamMembers(userId, chatToken);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a list of care team members (v2)",
            notes = "Send `Accept: application/vnd.ctm-v2+json` in headers for receiving a list of CTM DTO v2.<br/>Send `Accept: application/json` or skip `Accept` header for receiving a list of CTM DTO v1\n### Sorting rules\n * The items of the list are sorted alphabetically by name (from A to Z).")
    @GetMapping(headers = "accept=application/vnd.ctm-v2+json", produces = "application/vnd.ctm-v2+json")
    public Response<List<CareteamMemberBriefDto>> getCareteamMembers2(
            @ApiParam(value = "chat token", required = false) @RequestParam(value ="chatToken", required = false) String chatToken,
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final List<CareteamMemberBriefDto> dto = careTeamService.getUserCareTeamMembersBrief(userId, chatToken);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Delete a specific member from user's care team")
    @DeleteMapping(value = "/{contactId:\\d+}")
    public Response<Boolean> deleteCareteamMember(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId
    ) {
        final Boolean dto = careTeamService.deleteUserCareTeamMember(userId, contactId);
        return Response.successResponse(dto);
    }

    @Deprecated
    @ApiOperation(value = "Edit a specific care team member (allowed for some friend contacts)")
    @PutMapping(value = "/{contactId:\\d+}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<CareteamMemberDto> editCareteamMember(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId,
            @ApiParam(value = "CareteamMember object that needs to be updated", required = true) @RequestBody CareteamMemberDto body
    ) {
        final CareteamMemberDto dto = careTeamService.updateUserCareTeamMember(userId, contactId, body);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Conflict", response = ResponseErrorDto.class),
            @ApiResponse(code = 422, message = "Unprocessable Entity", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Invite a friend or family member to join your care team",
            notes = "NOTE! `relation` attribute should be uppercase.<br/>The invited person should accept the invitation and go through the registration process implemented in WEB Simply Connect.")
    @PostMapping(value = "/friend")
    public Response<CareteamMemberDto> inviteFriend(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @Phone
            @ApiParam(value = "mobile phone number", required = true) @RequestParam(value = "phone", required = true) String phone,
            @Email
            @ApiParam(value = "email", required = true) @RequestParam(value = "email", required = true) String email,
            @Size(min = 2, max = 255)
            @ApiParam(value = "first name", required = true) @RequestParam(value = "firstName", required = true) String firstName,
            @Size(min = 2, max = 255)
            @ApiParam(value = "last name", required = true) @RequestParam(value = "lastName", required = true) String lastName,
            @ApiParam(value = "the nature of the relationship between a patient and a contact person for that patient", required = true,
                    allowableValues = "FAMILY, FRIEND, GUARDIAN, PARENT, PARTNER, WORK")
            @RequestParam(value = "relation", required = true) CareTeamRelation.Relation relation,
            @Ssn
            @ApiParam(value = "social security number", required = false) @RequestParam(value = "ssn", required = false) String ssn
    ) {
        if (StringUtils.isBlank(ssn)) {
            ssn = null;
        }
        CareteamMemberDto dto = careTeamService.inviteFriend(userId, ssn, email, phone, firstName, lastName, relation);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = 422, message = "Unprocessable Entity", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Invite a physician to join your care team")
    @PostMapping(value = "/physician")
    public Response<CareteamMemberDto> invitePhysician(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "physician id", required = true) @RequestParam(value = "physicianId", required = true) Long physicianId
    ) {
        CareteamMemberDto dto = careTeamService.invitePhysician(userId, physicianId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Mark a specific care team member as an emergency contact",
            notes = "Consumers are allowed to toggle Emergency Contact mark for any member of their care team.<br>Providers are allowed to toggle Emergency Contact mark only for members of a care team of their care receivers (including themselves).")
    @PutMapping(value = "/{contactId:\\d+}/emergency")
    public Response<Boolean> markCareteamMemberEmergency(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId
    ) {
        final Boolean dto = careTeamService.setCareTeamMemberEmergency(userId, contactId, true);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Unmark a specific care team member as an emergency contact",
            notes = "Consumers are allowed to toggle Emergency Contact mark for any member of their care team.<br>Providers are allowed to toggle Emergency Contact mark only for members of a care team of their care receivers (including themselves).")
    @DeleteMapping(value = "/{contactId:\\d+}/emergency")
    public Response<Boolean> unmarkCareteamMemberEmergency(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId
    ) {
        final Boolean dto = careTeamService.setCareTeamMemberEmergency(userId, contactId, false);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get Nucleus user IDs", notes = "Get Nucleus user IDs (if known) that can be used to make calls between PHR app and Nucleus device.")
    @GetMapping(value = "/{contactId:\\d+}/nucleus/info")
    public Response<List<NucleusInfoDto>> getCareteamMemberNucleusInfo(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId,
            @ApiParam(value = "Current account type.", allowableValues = "PROVIDER, CONSUMER, PROVIDER, CONSUMER", defaultValue = "consumer")
            @RequestParam(value = "accountType", required = false, defaultValue="consumer") String type
    ) {
        final AccountType.Type accountType = AccountType.Type.fromValue(type);
        final List<NucleusInfoDto> dto = videoCallNucleusService.listNucleusInfoForCareTeamMember(userId, contactId, accountType);
        return Response.successResponse(dto);
    }
    
    @ApiOperation(value = "Delete a specific contact from facesheet document")
    @DeleteMapping(value="/facesheet/contacts/{contactId}")
    public Response<Boolean> deleteContactFromFacesheet(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @PathVariable("contactId") Long contactId){
        final Boolean status = careTeamService.deleteContactFromFacesheet(userId,contactId);
        return Response.successResponse(status);
    }
    
    @ApiOperation(value = "Add a specific contact into facesheet document")
    @PostMapping(value="/facesheet/contacts")
    public Response<Boolean> addContactIntoFacesheet(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "contact id", required = true) @RequestParam(value = "contactId", required = true) Long contactId){
        final Boolean status = careTeamService.addContactIntoFacesheet(userId,contactId);
        return Response.successResponse(status);
    }

}
