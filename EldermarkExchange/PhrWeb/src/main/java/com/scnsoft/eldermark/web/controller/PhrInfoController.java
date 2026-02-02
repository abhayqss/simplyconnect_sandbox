package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.phr.SectionUpdateRequest;
import com.scnsoft.eldermark.entity.phr.VitalSignType;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.shared.ccd.CcdHeaderPatientDto;
import com.scnsoft.eldermark.shared.ccd.GuardianDto;
import com.scnsoft.eldermark.shared.web.entity.ReportPeriod;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.*;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;

/**
 * @author phomal
 * @author averazub
 * Created on 1/3/2017.
 */
@Api(value = "PHR Info", description = "Personal Health Record information")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/phr/{userId:\\d+}")
public class PhrInfoController {
    Logger logger = Logger.getLogger(PhrInfoController.class.getName());

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

    @ApiOperation(value = "Get Demographics", notes = "Get user demographics info.<br/>Send `Accept: application/vnd.demographics-v2+json` in headers for receiving demographics DTO v2 (with guardians).<br/>Send `Accept: application/json` or skip `Accept` header for receiving demographics DTO v1 (without guardians)")
    @GetMapping(value = "/demographics", headers = "accept=application/json", produces = "application/json")
    public Response<CcdHeaderPatientDto> getDemographics(@PathVariable("userId") Long userId) {
        CcdHeaderPatientDto dto = phrService.getUserDemographics(userId, false);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get Demographics (v2)", notes = "Get user demographics info.<br/>Send `Accept: application/vnd.demographics-v2+json` in headers for receiving demographics DTO v2 (with guardians).<br/>Send `Accept: application/json` or skip `Accept` header for receiving demographics DTO v1 (without guardians)")
    @GetMapping(value = "/demographics", headers = "accept=application/vnd.demographics-v2+json", produces = "application/vnd.demographics-v2+json")
    public Response<CcdHeaderPatientDto> getDemographics2(@PathVariable("userId") Long userId) {
        CcdHeaderPatientDto dto = phrService.getUserDemographics(userId, true);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get Guardians")
    @GetMapping(value = "/demographics/guardians")
    public Response<List<GuardianDto>> getGuardians(@PathVariable("userId") Long userId) {
        List<GuardianDto> dto = phrService.getUserGuardiansInfo(userId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get Vital Sign details")
    @GetMapping(value = "/vitalSigns/{vitalSignType}")
    public Response<VitalSignObservationReport> getVitalSignDetails(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") Long userId,
            @ApiParam(value = "Type of Vital Sign", required = true, allowableValues="RESP, HEART_BEAT, O2_SAT, INTR_SYSTOLIC, INTR_DIASTOLIC, TEMP, HEIGHT, HEIGHT_LYING, CIRCUMFERENCE, WEIGHT")
            @PathVariable("vitalSignType") VitalSignType vitalSignType,
            @ApiParam(value = "Reporting period (if not specified, system will return last 7 vital sign observations)", allowableValues = "WEEK, MONTH, YEAR")
            @RequestParam(name = "period", required = false) ReportPeriod reportPeriod,
            @Min(0)
            @ApiParam(value = "Maximum results to appear in report (if not specified, system will return last 7 vital sign observations, in case period is omitted; and 100 vital sign observations otherwise)")
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @Deprecated
            @ApiParam(value = "Maximum results to appear in report. Old parameter name preserved for back compatibility; You SHOULD refrain from usage of this parameter.")
            @RequestParam(name = "maxResults", required = false) Integer maxResults,
            @Max(value = 0, message = "Page parameter should be equal or less than 0")
            @ApiParam(value = "Report page. 0 will return report for last Report period. -1 will return report for preceding Report Period, etc.", defaultValue = "0")
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @ApiParam(value = "Preferred unit of measurement (e.g. \"cm\" or \"in\" for `HEIGHT`)")
            @RequestParam(value = "unit", required = false) String unit
    ) {
        logger.info("Get Vital Sign Details : " + vitalSignType);
        if (maxResults == null) {
            maxResults = pageSize;
        }
        if (maxResults == null) {
            maxResults = (reportPeriod == null) ? 7 : 100;
        }
        VitalSignObservationReport dto = vitalSignService.getVitalSignDetails(userId, vitalSignType, reportPeriod, maxResults, page);
        return Response.successResponse(dto);
    }

    @ApiOperation(
            value = "Get a map of vital signs to their latest observation results",
            notes = "Response type is {{" +
                    "   'body': {" +
                    "      'success': true,\n" +
                    "      'data': { ::VitalSignObservationLatestResultsType:: }\n" +
                    "   },\n" +
                    "   'statusCode': '200'\n" +
                    "}}\n" +
                    "::VitalSignObservationLatestResultsType:: = {\n" +
                    "  “RESP” : ::VitalSignObservationType::,\n" +
                    "  “HEART_BEAT ” : ::VitalSignObservationType::,\n" +
                    "  “O2_SAT ” : ::VitalSignObservationType::,\n" +
                    "  “INTR_SYSTOLIC ” : ::VitalSignObservationType::,\n" +
                    "  “INTR_DIASTOLIC ” : ::VitalSignObservationType::,\n" +
                    "  “TEMP ” : ::VitalSignObservationType::,\n" +
                    "  “HEIGHT ” : ::VitalSignObservationType::,\n" +
                    "  “HEIGHT_LYING ” : ::VitalSignObservationType::,\n" +
                    "  “CIRCUMFERENCE ” : ::VitalSignObservationType::,\n" +
                    "  “WEIGHT” : ::VitalSignObservationType::\n" +
                    "}\n")
    @GetMapping(value = "/vitalSigns/latestResults")
    public Response<Map<String, VitalSignObservationDto>> getVitalSignLatestResults(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") Long userId
    ) {
        Map<String, VitalSignObservationDto> dto = vitalSignService.getVitalSignLatestResults(userId);
        return Response.successResponse(dto);
    }

    @ApiOperation(
            value = "Get a map of vital signs to their earliest measurement dates",
            notes = "Response type is {{" +
                    "   'body': {" +
                    "      'success': true,\n" +
                    "      'data': { ::VitalSignObservationEarliestDate:: }\n" +
                    "   },\n" +
                    "   'statusCode': '200'\n" +
                    "}}\n" +
                    "::VitalSignObservationEarliestDate:: = {\n" +
                    "  “RESP” : ::VitalSignObservationDate::,\n" +
                    "  “HEART_BEAT” : ::VitalSignObservationDate::,\n" +
                    "  “O2_SAT” : ::VitalSignObservationDate::,\n" +
                    "  “INTR_SYSTOLIC” : ::VitalSignObservationDate::,\n" +
                    "  “INTR_DIASTOLIC” : ::VitalSignObservationDate::,\n" +
                    "  “TEMP” : ::VitalSignObservationDate::,\n" +
                    "  “HEIGHT” : ::VitalSignObservationDate::,\n" +
                    "  “HEIGHT_LYING” : ::VitalSignObservationDate::,\n" +
                    "  “CIRCUMFERENCE” : ::VitalSignObservationDate::,\n" +
                    "  “WEIGHT” : ::VitalSignObservationDate::\n" +
                    "}\n\n")
    @GetMapping(value = "/vitalSigns/earliestMeasurementDates")
    public Response<Map<String, DateDto>> getEarliestMeasurementDates(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") Long userId
    ) {
        Map<String, DateDto> dto = vitalSignService.getVitalSignEarliestMeasurementDates(userId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get the earliest vital sign observation")
    @GetMapping(value = "/vitalSigns/{vitalSignType}/earliestMeasurement")
    public @ResponseBody
    Response<VitalSignObservationDto> getEarliestMeasurement(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") Long userId,
            @ApiParam(value = "Type of Vital Sign", required = true, allowableValues="RESP, HEART_BEAT, O2_SAT, INTR_SYSTOLIC, INTR_DIASTOLIC, TEMP, HEIGHT, HEIGHT_LYING, CIRCUMFERENCE, WEIGHT")
            @PathVariable("vitalSignType") VitalSignType vitalSignType,
            @ApiParam(value = "Preferred unit of measurement (e.g. \"cm\" or \"in\" for `HEIGHT`)")
            @RequestParam(value = "unit", required = false) String unit
    ) {
        VitalSignObservationDto dto = vitalSignService.getVitalSignEarliestMeasurement(userId, vitalSignType, unit);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get sections with amount of the items")
    @GetMapping(value = "/sections")
    public Response<Map<Section, Long>> getSections(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        final Map<Section, Long> dto = ccdSectionService.getSectionsWithCount(userId);
        return Response.successResponse(dto);
    }


    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get Active Medications", notes = "Note that `DataSource` is always null cause it's not used anyway.")
    @GetMapping(value = "/medications/active")
    public Response<List<MedicationInfoDto>> getActiveMedications(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") final Long userId,
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all medications), ≥ 1")
            @Min(1)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        Pageable pageable = buildPageable(pageSize, page);
        final Page<MedicationInfoDto> medications = medicationService.getUserMedicationsActive(userId, pageable);
        return Response.pagedResponse(medications);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get Medications History", notes = "Note that `DataSource` is always null cause it's not used anyway.")
    @GetMapping(value = "/medications/inactive")
    public Response<List<MedicationInfoDto>> getMedicationsHistory(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") final Long userId,
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all medications), ≥ 1")
            @Min(1)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        Pageable pageable = buildPageable(pageSize, page);
        final Page<MedicationInfoDto> medications = medicationService.getUserMedicationsHistory(userId, pageable);
        return Response.pagedResponse(medications);
    }


    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get Active Problems", notes = "###Sorting rules\n" +
            " * The data is sorted by the start date (from the newest to the oldest).\n" +
            " * If the start date is omitted, the system shall sort the data alphabetically (from A to Z).")
    @GetMapping(value = "/problems/active")
    public Response<List<ProblemInfoDto>> getActiveProblems(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId,
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all problems), ≥ 1")
            @Min(1)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        Pageable pageable = buildPageable(pageSize, page);
        final Page<ProblemInfoDto> problems = problemService.getUserProblemsActive(userId, pageable);
        return Response.pagedResponse(problems);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get Resolved Problems", notes = "###Sorting rules\n" +
            " * The data is sorted by the start date (from the newest to the oldest).\n" +
            " * If the start date is omitted, the system shall sort the data alphabetically (from A to Z).")
    @GetMapping(value = "/problems/resolved")
    public Response<List<ProblemInfoDto>> getResolvedProblems(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId,
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all problems), ≥ 1")
            @Min(1)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        Pageable pageable = buildPageable(pageSize, page);
        final Page<ProblemInfoDto> problems = problemService.getUserProblemsResolved(userId, pageable);
        return Response.pagedResponse(problems);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get Other Problems", notes = "###Sorting rules\n" +
            " * The data is sorted by the start date (from the newest to the oldest).\n" +
            " * If the start date is omitted, the system shall sort the data alphabetically (from A to Z).")
    @GetMapping(value = "/problems/other")
    public Response<List<ProblemInfoDto>> getOtherProblems(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId,
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all problems), ≥ 1")
            @Min(1)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        Pageable pageable = buildPageable(pageSize, page);
        final Page<ProblemInfoDto> problems = problemService.getUserProblemsOther(userId, pageable);
        return Response.pagedResponse(problems);
    }

    @ApiOperation(value = "Get Problem details")
    @GetMapping(value = "/problems/{problemId:\\d+}")
    public Response<ProblemInfoDto> getProblemDetails(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "problem id", required = true) @PathVariable("problemId") Long problemId
    ) {
        return Response.successResponse(problemService.getUserProblem(userId, problemId));
    }

    @ApiOperation(value = "Get Immunization details")
    @GetMapping(value = "/immunizations/{immunizationId:\\d+}")
    public Response<ImmunizationInfoDto> getImmunizationDetails(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "immunization id", required = true) @PathVariable("immunizationId") Long immunizationId
    ) {
        return Response.successResponse(immunizationService.getUserImmunization(userId, immunizationId));
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get Immunizations", notes = "### Sorting rules\n" +
            " * The data is sorted by the start date of the immunization (from the newest to the oldest).\n" +
            " * If the start date of the immunization is empty, the system shall sort data alphabetically (from A to Z).")
    @GetMapping(value = "/immunizations")
    public Response<List<ImmunizationInfoDto>> getImmunizations(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId,
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all immunizations), ≥ 1")
            @Min(1)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<ImmunizationInfoDto> immunizations = immunizationService.getUserImmunizations(userId, pageable);
        return Response.pagedResponse(immunizations);
    }

    @ApiOperation(value = "Get Payers", notes = "### Sorting rules\n" +
            " * The data is sorted by the start date of the coverage period (from the newest to the oldest).\n" +
            " * If the start date of the coverage period is empty, the system shall sort data alphabetically (from A to Z).")
    @GetMapping(value = "/payers")
    public Response<List<PayerInfoDto>> getPayers(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId
    ) {
        return Response.successResponse(payerService.getUserPayers(userId));
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get active Allergies",
            notes = "###Sorting rules\nThe data is sorted alphabetically by the allergy product name (from A to Z).")
    @GetMapping(value = "/allergies/active")
    public Response<List<AllergyInfoDto>> getActiveAllergies(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") final Long userId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all allergies), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<AllergyInfoDto> allergies = allergyService.getUserAllergiesActive(userId, pageable);
        return Response.pagedResponse(allergies);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get inactive Allergies",
            notes = "###Sorting rules\nThe data is sorted alphabetically by the allergy product name (from A to Z).")
    @GetMapping(value = "/allergies/inactive")
    public Response<List<AllergyInfoDto>> getInactiveAllergies(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") final Long userId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all allergies), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<AllergyInfoDto> allergies = allergyService.getUserAllergiesInactive(userId, pageable);
        return Response.pagedResponse(allergies);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Get resolved Allergies",
            notes = "###Sorting rules\nThe data is sorted alphabetically by the allergy product name (from A to Z).")
    @GetMapping(value = "/allergies/resolved")
    public Response<List<AllergyInfoDto>> getResolvedAllergies(
            @ApiParam(value = "user id", required = true)
            @PathVariable("userId") final Long userId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all allergies), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        final Page<AllergyInfoDto> allergies = allergyService.getUserAllergiesResolved(userId, pageable);
        return Response.pagedResponse(allergies);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "Request to update data", notes = "NOTE! Doesn't work in Swagger UI.")
    @PostMapping(value = "/{section}/updateRequest",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response sectionUpdateRequest(
            @ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "section", required = true,
                    allowableValues = "allergies, medications, problems, vitalSigns, immunizations, payers, documents," +
                            " notes, procedures, encounters, planOfCare, advancedDirectives, socialHistory, results, familyHistory, medicalEquipment")
            @PathVariable("section") String section,
            @ApiParam(value = "free text comment", required = true)
            @RequestParam(value = "comment", required = true) String comment,
            @ApiParam(value = "send request to all providers", required = true)
            @RequestParam(value = "sendToAll", required = true) Boolean sendToAll,
            @ApiParam(value = "Request type", required = true, allowableValues = "ADD_NEW, UPDATE, DELETE")
            @RequestParam(value = "requestType", required = true) SectionUpdateRequest.Type requestType,
            @Size(max = 8, message = "The amount of attachments is limited to 8 items.")
            @ApiParam(value = "file attachment (limited to 2MB)", allowMultiple = true)
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        final Section ccdSection = Section.fromName(StringUtils.lowerCase(section));
        ccdSectionService.createUpdateRequest(userId, ccdSection, comment, sendToAll, requestType, files);
        return Response.successResponse();
    }

}
