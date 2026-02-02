package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.facade.*;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.validation.AccessibleResidentsUserProvider;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Logger;

import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;

@Api(value = "PHR Info", description = "Personal Health Record information (retreived by user id)")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/phr/users/{userId:\\d+}")
public class PhrInfoUserController {
    Logger logger = Logger.getLogger(PhrInfoUserController.class.getName());

    @Autowired
    PhrService phrService;

    @Autowired
    VitalSignService vitalSignService;

    @Autowired
    MedicationService medicationService;

    @Autowired
    ProblemService problemService;

    @Autowired
    AllergyService allergyService;

    @Autowired
    ImmunizationService immunizationService;

    @Autowired
    CcdSectionService ccdSectionService;

    @Autowired
    PayerService payerService;

    @Autowired
    SocialHistoryService socialHistoryService;

    @Autowired
    private PlanOfCareFacade planOfCareFacade;

    @Autowired
    AccessibleResidentsUserProvider accessibleResidentsUserProvider;

    @Autowired
    private ProcedureActivityFacade procedureActivityFacade;

    @Autowired
    private EncountersFacade encountersFacade;

    @Autowired
    private AdvanceDirectivesFacade advanceDirectivesFacade;

    @Autowired
    private FamilyHistoryFacade familyHistoryFacade;

    @Autowired
    private ResultsFacade resultsFacade;

    @Autowired
    private MedicalEquipmentFacade medicalEquipmentFacade;

    @Autowired
    private ParticipantFacade participantFacade;

    @Autowired
    private AdmitIntakeHistoryFacade admitIntakeHistoryFacade;

    //encounters section begin - TODO
    @ApiOperation(value = "Get encounters", notes = "TODO")
    @RequestMapping(value = "/encounters", method = RequestMethod.GET)
    public Response<List<EncounterInfoDto>> getEncounters(
            @ApiParam(value = "user id from table ResidentCareTeamMember", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<EncounterInfoDto> encounters = encountersFacade.getEncountersInfoForUser(userId, pageable);
        return Response.pagedResponse(encounters);
    }

    @ApiOperation(value = "Get Encounter details")
    @RequestMapping(value = "/encounters/{encounterId}", method = RequestMethod.GET)
    public Response<EncounterDto> getEncounter(
            @ApiParam(value = "userId ", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "encounter Id", required = true) @PathVariable("encounterId") Long encounterId
    ) {
        final EncounterDto dto = encountersFacade.getEncounter(encounterId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get Participant details")
    @RequestMapping(value = "/participants/{participantId}", method = RequestMethod.GET)
    public Response<ParticipantDto> getParticipant(
            @ApiParam(value = "userId", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "participant Id", required = true) @PathVariable("participantId") Long participantId
    ) {
        return Response.successResponse(getParticipantFacade().getParticipant(participantId));
    }
    //encounters section end

    //Advance Directives section begin - TODO
    @ApiOperation(value = "Get Advance Directives details")
    @RequestMapping(value = "/advanced-directives/{directiveId}", method = RequestMethod.GET)
    public Response<AdvanceDirectiveDto> getAdvanceDirectiveDetails(
            @ApiParam(value = "userId", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "Advance Directive Id", required = true) @PathVariable("directiveId") Long directiveId
    ) {
        final AdvanceDirectiveDto dto = advanceDirectivesFacade.getAdvanceDirective(directiveId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get Advance Directives", notes = "TODO")
    @RequestMapping(value = "/advanced-directives", method = RequestMethod.GET)
    public Response<List<AdvanceDirectiveInfoDto>> getAdvanceDirectivesList(
            @ApiParam(value = "user id from table ResidentCareTeamMember", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all Advance Directives ), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<AdvanceDirectiveInfoDto> advanceDirectiveInfoDtos = advanceDirectivesFacade.getAdvanceDirectivesForUser(userId, pageable);
        return Response.pagedResponse(advanceDirectiveInfoDtos);
    }
    //Advance Directives section end

    //Procedures section begin
    @ApiOperation(value = "Get Procedure details", notes = "Returns user's procedure details", tags={"not-implemented"})
    @RequestMapping(value = "/procedures/{procedureId:\\d+}", method = RequestMethod.GET)
    public Response<ProcedureDetailsDto> getUserProcedure(
            @ApiParam(value = "User id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "Prodecudure Id", required = true) @PathVariable("procedureId") Long procedureId
    ) {
        return Response.successResponse(procedureActivityFacade.getProcedureActivity(procedureId));
    }

    @ApiOperation(value = "Get procedures list", notes = "Returns a list of user's procedures", tags={"not-implemented"})
    @RequestMapping(value = "/procedures", method = RequestMethod.GET)
    public Response<List<ProcedureListItemDto>> getUserProcedures(
            @ApiParam(value = "User id", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        return Response.pagedResponse(procedureActivityFacade.getProcedureActivitiesForUser(userId, pageable));
    }
    //Procedures section end

    //Plan of Care section begin
    @ApiOperation(value = "Get plan of care activity details")
    @RequestMapping(value = "/planOfCare/{activityId}", method = RequestMethod.GET)
    public @ResponseBody
    Response<PlanOfCareDto> getPlanOfCareActivityByUser(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "activity Id", required = true) @PathVariable("activityId") Long activityId
    ) {
        return Response.successResponse(planOfCareFacade.getPlanOfCareActivity(activityId));
    }

    @ApiOperation(value = "Get plan of care")
    @RequestMapping(value = "/planOfCare", method = RequestMethod.GET)
    public @ResponseBody
    Response<List<PlanOfCareInfoDto>> getPlansOfCareByUser(
            @ApiParam(value = "user id from table ResidentCareTeamMember", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        return Response.pagedResponse(planOfCareFacade.getPlanOfCareActivitiesForUser(userId, pageable));
    }
    //Plan of Care section end

    //Social History section begin - TODO

    @ApiOperation(value = "Get social history (pregnancy observations) details", notes = "Get social history (pregnancy observations) details")
    @RequestMapping(value = "/social-history/pregnancy-observations/{observationId}", method = RequestMethod.GET)
    public Response<PregnancyObservationDto> getSocialHistoryPregnancy(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "social history pregnancy observation id", required = true) @PathVariable("observationId") Long observationId
    ) {
        final PregnancyObservationDto dto = socialHistoryService.getPregnancyObservation(observationId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get social history 4 sections count", notes = "Get social history 4 sections count")
    @RequestMapping(value = "/social-history/sections", method = RequestMethod.GET)
    public Response<SocialHistoryCountDto> getSocialHistoryCount(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final SocialHistoryCountDto dto = socialHistoryService.getSocialHistorySectionsCount(userId, accessibleResidentsUserProvider);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get social history (pregnancy observations)", notes = "Get social history (pregnancy observations)")
    @RequestMapping(value = "/social-history/pregnancy-observations", method = RequestMethod.GET)
    public Response<List<PregnancyObservationInfoDto>> getSocialHistoryPregnancyObservations(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<PregnancyObservationInfoDto> pregnancyObservationInfoDtos = socialHistoryService.getPregnancyObservations(userId, pageable, accessibleResidentsUserProvider);
        return Response.pagedResponse(pregnancyObservationInfoDtos);
    }

    @ApiOperation(value = "Get social history (smoking statuses observations) details", notes = "Get social history (smoking statuses observations) details")
    @RequestMapping(value = "/social-history/smoking-status-observations/{observationId}", method = RequestMethod.GET)
    public Response<SmokingStatusDto> getSocialHistorySmokingStatus(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "social history smoking status observation id", required = true) @PathVariable("observationId") Long observationId
    ) {
        final SmokingStatusDto dto = socialHistoryService.getSmokingStatus(observationId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get social history (smoking statuses observations)", notes = "Get social history (smoking statuses observations)")
    @RequestMapping(value = "/social-history/smoking-status-observations", method = RequestMethod.GET)
    public Response<List<SmokingStatusInfoDto>> getSocialHistorySmokingStatuses(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<SmokingStatusInfoDto> smokingStatusInfoDtos = socialHistoryService.getSmokingStatuses(userId,pageable, accessibleResidentsUserProvider);
        return Response.pagedResponse(smokingStatusInfoDtos);
    }

    @ApiOperation(value = "Get social history (observations) details", notes = "Get social history (observations) details")
    @RequestMapping(value = "/social-history/observations/{observationId}", method = RequestMethod.GET)
    public Response<SocialHistoryObservationDto> getSocialHistoryObservation(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "social history observation id", required = true) @PathVariable("observationId") Long observationId
    ) {
        final SocialHistoryObservationDto dto = socialHistoryService.getSocialHistoryObservation(observationId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get social history (observations)", notes = "Get social history (observations)")
    @RequestMapping(value = "/social-history/observations", method = RequestMethod.GET)
    public Response<List<SocialHistoryObservationInfoDto>> getSocialHistoryObservations(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<SocialHistoryObservationInfoDto> socialHistoryObservationInfoDtos = socialHistoryService.getSocialHistoryObservations(userId, pageable, accessibleResidentsUserProvider);
        return Response.pagedResponse(socialHistoryObservationInfoDtos);
    }

    @ApiOperation(value = "Get social history (tobacco use) details", notes = "Get social history (tobacco use) details")
    @RequestMapping(value = "/social-history/tobacco-use/{tobaccoUseId}", method = RequestMethod.GET)
    public Response<TobaccoUseDto> getSocialHistoryTobaccoUse(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "social history tobacco use id", required = true) @PathVariable("tobaccoUseId") Long tobaccoUseId
    ) {
        final TobaccoUseDto dto = socialHistoryService.getTobaccoUse(tobaccoUseId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get social history (tobacco use)", notes = "Get social history (tobacco use)")
    @RequestMapping(value = "/social-history/tobacco-use", method = RequestMethod.GET)
    public Response<List<TobaccoUseInfoDto>> getSocialHistoryTobaccoUses(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<TobaccoUseInfoDto> tobaccoUseInfoDtos = socialHistoryService.getTobaccoUses(userId, pageable, accessibleResidentsUserProvider);
        return Response.pagedResponse(tobaccoUseInfoDtos);
    }

    //Social History section end

    //Results begin - TODO

    @ApiOperation(value = "Get results details", notes = "Get results details")
    @RequestMapping(value = "/results/{resultId}", method = RequestMethod.GET)
    public Response<ResultDto> getResult(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "result id", required = true) @PathVariable("resultId") Long resultId
    ) {
        final ResultDto dto = resultsFacade.getResult(resultId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get results", notes = "Get results")
    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public Response<List<ResultInfoDto>> getResults(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<ResultInfoDto> resultInfoDtos = resultsFacade.getResultsForUser(userId, pageable);
        return Response.pagedResponse(resultInfoDtos);
    }

    //Results section end

    //Family History section begin - TODO

    @ApiOperation(value = "Get family history", notes = "Get a list of user's family history entries", tags={"not-implemented"})
    @RequestMapping(value = "/family-history", method = RequestMethod.GET)
    public Response<List<FamilyHistoryListItemDto>> getUserFamilyHistoryList(
            @ApiParam(value = "User id", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        // do some magic!
        return Response.pagedResponse(familyHistoryFacade.listFamilyHistoryForUser(userId, pageable));
    }

    @ApiOperation(value = "Get family history details", notes = "Get user's family history details", tags={"not-implemented"})
    @RequestMapping(value = "/family-history/{familyHistoryId}", method = RequestMethod.GET)
    public Response<FamilyHistoryInfoDto> getUserFamilyHistoryDetails(
            @ApiParam(value = "User id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "Family histoty id", required = true) @PathVariable("familyHistoryId") Long familyHistoryId
    ) {
        return Response.successResponse(familyHistoryFacade.getFamilyHistoryInfo(familyHistoryId));
    }

    @ApiOperation(value = "Get family history observation details", notes = "Get user's family history observation details", tags={"not-implemented"})
    @RequestMapping(value = "/family-history/observations/{observationId}", method = RequestMethod.GET)
    public Response<FamilyHistoryObservationInfoDto> getUserFamilyHistoryObservationDetails(
            @ApiParam(value = "User id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "Family histoty observation id", required = true) @PathVariable("observationId") Long observationId
    ) {
        return Response.successResponse(familyHistoryFacade.getFamilyHistoryObservationInfo(observationId));
    }
    //Family History section end

    //Medical equipment section begin - TODO

    @ApiOperation(value = "Get medical equipment details", notes = "Get medical equipment details")
    @RequestMapping(value = "/medical-equipment/{medicalEquipmentId}", method = RequestMethod.GET)
    public Response<MedicalEquipmentDto> getMedicalEquipment(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "medical equipment id", required = true) @PathVariable("medicalEquipmentId") Long medicalEquipmentId
    ) {
        final MedicalEquipmentDto dto = medicalEquipmentFacade.getMedicalEquipment(medicalEquipmentId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get medical equipment", notes = "Get medical equipment")
    @RequestMapping(value = "/medical-equipment", method = RequestMethod.GET)
    public Response<List<MedicalEquipmentInfoDto>> getMedicalEquipments(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<MedicalEquipmentInfoDto> medicalEquipmentInfoDtos = medicalEquipmentFacade.getMedicalEquipmentsForUser(userId, pageable);
        return Response.pagedResponse(medicalEquipmentInfoDtos);
    }

    //Medical equipment section end

    @ApiOperation(value = "Get admit dates of user", notes = "Get admit dates of user")
    @GetMapping(value = "/admit-dates")
    public Response<List<AdmitDateDto>> getUserAdmitDates(
            @ApiParam(value = "User id", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        return Response.pagedResponse(admitIntakeHistoryFacade.getAdmitIntakeDatesForUser(userId, pageable));
    }
    public ParticipantFacade getParticipantFacade() {
        return participantFacade;
    }

    public void setParticipantFacade(final ParticipantFacade participantFacade) {
        this.participantFacade = participantFacade;
    }
}
