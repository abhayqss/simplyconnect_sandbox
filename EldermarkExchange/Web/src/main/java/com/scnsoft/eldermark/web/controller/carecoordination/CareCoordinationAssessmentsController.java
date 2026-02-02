package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.AssessmentStatus;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.service.AssessmentFacade;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.assessments.*;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

import static com.scnsoft.eldermark.dao.carecoordination.ResidentAssessmentResultDao.ORDER_BY_DATE_COMPLETED;

//@Controller
//@RequestMapping(value = "/care-coordination/assessment")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationAssessmentsController {

    private final static String DEFAULT_ASSESSMENT_NAME = "Adolescent alcohol use disorder screening (CRAFFT)";

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessmentFacade assessmentFacade;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private AssessmentGroupService assessmentGroupService;

    @Autowired
    private ResidentAssessmentResultService residentAssessmentResultService;

    @Autowired
    private AssessmentScoringGroupService assessmentScoringGroupService;

    @Autowired
    private SlumsAssessmentScoringGroupService slumsAssessmentScoringGroupService;

    @Autowired
    private SADPersonsScoringResultService sadPersonsScoringResultService;

    @Autowired
    CareCoordinationResidentService careCoordinationResidentService;

    @RequestMapping(value = "/patient/{patientId}/new-result", method = RequestMethod.GET)
    public String addNewAssessmentResultView(@PathVariable("patientId") Long patientId, final Model model) {
        CareCoordinationResident resident = careCoordinationResidentService.get(patientId);
        List<AssessmentGroupDto> assessmentGroups = assessmentGroupService.getAllAssessmentGroups(resident.getDatabaseId());

        SelectedAssessmentDto selectedAssessmentDto = new SelectedAssessmentDto();
        selectedAssessmentDto.setPatientId(patientId);

        boolean defaultFound = false;
        if (!CollectionUtils.isEmpty(assessmentGroups)) {
            for (AssessmentGroupDto assessmentGroupDto : assessmentGroups) {
                if (!CollectionUtils.isEmpty(assessmentGroupDto.getAssessments())) {
                    for (Pair<Long, String> assessmentDto : assessmentGroupDto.getAssessments()) {
                        if (assessmentDto.getSecond().contains(DEFAULT_ASSESSMENT_NAME)) {
                            selectedAssessmentDto.setAssessmentId(assessmentDto.getFirst());
                            defaultFound = true;
                            break;
                        }
                    }
                }
                if (defaultFound) {
                    break;
                }
            }
        }

        assessmentFacade.sortAssessmentGroups(assessmentGroups);
        model.addAttribute("assessmentGroups", assessmentGroups);
        model.addAttribute("modalTitle", "Select Assessment Type");
        model.addAttribute("selectedAssessmentDto", selectedAssessmentDto);
        return "patient.assessment.select.type";
    }

    @RequestMapping(value = "/result/{assessmentResultId}/history/view", method = RequestMethod.GET)
    public String viewPatientAssessmentHistoryResult(@PathVariable("assessmentResultId") Long assessmentResultId, final Model model) {
        ResidentAssessmentScoringDto residentAssessmentScoringDto = residentAssessmentResultService.calculateAssessmentScoringResults(assessmentResultId);
        model.addAttribute("residentAssessmentScoringDto", residentAssessmentScoringDto);
        model.addAttribute("readOnly", Boolean.TRUE);
        return "patient.assessment.history.view";
    }

    @RequestMapping(value = "/result/{assessmentResultId}/view", method = RequestMethod.GET)
    public String viewPatientAssessmentResult(@PathVariable("assessmentResultId") Long assessmentResultId, final Model model) {
        ResidentAssessmentScoringDto residentAssessmentScoringDto = residentAssessmentResultService.calculateAssessmentScoringResults(assessmentResultId);
        model.addAttribute("residentAssessmentScoringDto", residentAssessmentScoringDto);
        model.addAttribute("readOnly", Boolean.TRUE);
        return "patient.assessment.view";
    }

    @RequestMapping(value = "/patient/{patientId}/new-assessment-data", method = RequestMethod.GET)
    public String addNewAssessmentResult(@ModelAttribute("selectedAssessmentDto") SelectedAssessmentDto dto, @PathVariable("patientId") Long patientId, final Model model) {
        ResidentAssessmentResultDto residentAssessmentResultDto = new ResidentAssessmentResultDto();
        residentAssessmentResultDto.setEmployeeName(SecurityUtils.getAuthenticatedUser().getEmployee().getFullName());
        residentAssessmentResultDto.setEmployeeNameAndRole(SecurityUtils.getAuthenticatedUser().getEmployee().getFullName() + ", " + SecurityUtils.getAuthenticatedUser().getEmployee().getCareTeamRole().getDisplayName());
        residentAssessmentResultDto.setEmployeeId(SecurityUtils.getAuthenticatedUser().getEmployeeId());
        residentAssessmentResultDto.setPatientId(patientId);
        residentAssessmentResultDto.setAssessmentId(dto.getAssessmentId());
        model.addAttribute("residentAssessmentResultDto", residentAssessmentResultDto);
        return "patient.assessment.result";
    }

    @RequestMapping(value = "/patient/{patientId}/edit-assessment-data/{assessmentResultId}", method = RequestMethod.GET)
    public String editAssessmentResult(@ModelAttribute("selectedAssessmentDto") SelectedAssessmentDto dto, @PathVariable("patientId") final Long patientId, @PathVariable("assessmentResultId") final Long assessmentResultId, final Model model) {
        ResidentAssessmentResultDto residentAssessmentResultDto = residentAssessmentResultService.find(assessmentResultId);
        residentAssessmentResultDto.setEmployeeNameAndRole(SecurityUtils.getAuthenticatedUser().getEmployee().getFullName() + ", " + SecurityUtils.getAuthenticatedUser().getEmployee().getCareTeamRole().getDisplayName());
        residentAssessmentResultDto.setEmployeeId(SecurityUtils.getAuthenticatedUser().getEmployeeId());
        model.addAttribute("residentAssessmentResultDto", residentAssessmentResultDto);
        return "patient.assessment.result.edit";
    }

    @RequestMapping(value = "/patient/{patientId}/assessment/{assessmentResultId}/download", method = RequestMethod.GET)
    public void downloadAssessment(@PathVariable("patientId") final Long patientId, @PathVariable("assessmentResultId") final Long assessmentResultId, HttpServletResponse response,
                                   @RequestParam(value = "timeZoneOffset", defaultValue = "0", required = false) Integer timeZoneOffset) {
        residentAssessmentResultService.downloadResidentAssessmentResult(assessmentResultId, response, timeZoneOffset);
    }


    @RequestMapping(value = "/assessment-details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AssessmentDto assessmentDetails(@ModelAttribute("residentAssessmentResultDto") ResidentAssessmentResultDto dto) {
        AssessmentDto assessmentDto = assessmentService.getAssessmentDetails(dto.getAssessmentId());
        assessmentDto.setPatientId(dto.getPatientId());
        return assessmentDto;
    }

    @RequestMapping(value = "/assessment/{assessmentStatus}", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public Long createAssessment(@ModelAttribute("residentAssessmentResultDto") ResidentAssessmentResultDto dto, @PathVariable("assessmentStatus") final Long assessmentStatus) {
        Long resultId;
        dto.setAssessment_status(AssessmentStatus.values()[assessmentStatus.intValue()]);
        if (dto.getId() == null) {
            resultId = residentAssessmentResultService.createAuditableEntityFromDto(dto);
        } else {
            resultId = residentAssessmentResultService.updateAuditableEntityFromDto(dto);
        }
        residentService.updateResidentAccordingToComprehensiveAssessment(dto.getAssessmentId(), resultId);
        return resultId;
    }

    @RequestMapping(value = "/scoring", method = RequestMethod.POST)
    public String getScoringPage(@ModelAttribute("residentAssessmentResultDto") ResidentAssessmentResultDto dto, final Model model) {
        ResidentAssessmentScoringDto residentAssessmentScoringDto = residentAssessmentResultService.calculateAssessmentScoringResults(dto.getAssessmentId(), dto.getResultJson());
        model.addAttribute("residentAssessmentScoringDto", residentAssessmentScoringDto);
        return "patient.assessment.scoring";
    }

    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public String getReviewPage(@ModelAttribute("residentAssessmentResultDto") ResidentAssessmentResultDto dto, final Model model) {
        ResidentAssessmentPriorityCheckDto residentAssessmentPriorityCheckDto = residentAssessmentResultService.showPriorityCheckResult(dto);
        model.addAttribute("residentAssessmentPriorityCheckDto", residentAssessmentPriorityCheckDto);
        return "patient.assessment.priority.review";
    }

    @ResponseBody
    @RequestMapping(value = "/patient/{patientId}/total", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Long getAssessmentsTotal(@PathVariable("patientId") Long patientId) {
        return residentAssessmentResultService.count(patientId);
    }

    @RequestMapping(value = "/patient/{patientId}/list", method = RequestMethod.POST)
    @ResponseBody
    public Page<ResidentAssessmentResultListDto> getResidentAssessmentsList(@PathVariable("patientId") Long patientId,
                                                                            @ModelAttribute("assessmentsFilter") AssessmentsFilterDto assessmentsFilterDto, Pageable pageable) {
        Pageable pageableToSearch = pageable;
        if (pageable.getSort() == null) {
            pageableToSearch = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(ORDER_BY_DATE_COMPLETED));
        }
        //TODO implement sort by status when there would be multiple statuses
        else if (pageable.getSort().getOrderFor("status") != null) {
            pageableToSearch = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
        } else if (pageable.getSort().getOrderFor("author") != null) {
            Sort.Order order = pageable.getSort().getOrderFor("author");
            pageableToSearch = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), order.getDirection(), "employee.firstName", "employee.lastName");
        }
        return residentAssessmentResultService.list(patientId, assessmentsFilterDto, pageableToSearch);
    }

    @RequestMapping(value = "/{assessmentId}/scoring/list", method = RequestMethod.POST)
    @ResponseBody
    public Page<AssessmentScoringGroupListDto> getAssessmentsScoringGroupList(@PathVariable("assessmentId") Long assessmentId, Pageable pageable) {
        return assessmentScoringGroupService.getScoringGroups(assessmentId, pageable);
    }

    @RequestMapping(value = "/slums/{assessmentId}/scoring/list", method = RequestMethod.POST)
    @ResponseBody
    public Page<SlumsAssessmentScoringGroupDto> getSlumsAssessmentsScoringResult(@PathVariable("assessmentId") Long assessmentId, Pageable pageable) {
        return slumsAssessmentScoringGroupService.getScoringGroups(assessmentId, pageable);
    }

    @RequestMapping(value = "/sad/{assessmentId}/scoring/list", method = RequestMethod.POST)
    @ResponseBody
    public Page<SADPersonsAssessmentScoringResult> getSADPersonsAssessmentsScoringResult(@PathVariable("assessmentId") Long assessmentId, Pageable pageable) {
        return sadPersonsScoringResultService.getScoringGroups(assessmentId, pageable);
    }

    @RequestMapping(value = "/{id}/history", method = RequestMethod.POST)
    @ResponseBody
    public Page<AssessmentHistoryDto> getAssessmentsHistory(@PathVariable("id") Long id, Pageable pageable) {
        return assessmentScoringGroupService.getHistory(id, pageable);
    }
}
