package com.scnsoft.eldermark.web.controller.ccd;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.facades.ccd.CcdFacade;
import com.scnsoft.eldermark.services.ccd.section.ProblemService;
import com.scnsoft.eldermark.shared.SearchScope;
import com.scnsoft.eldermark.shared.ccd.CcdCodeDto;
import com.scnsoft.eldermark.shared.ccd.CcdSectionDto;
import com.scnsoft.eldermark.shared.ccd.ProblemObservationDto;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.scnsoft.eldermark.web.controller.provider.CcdModelAttributesProvider;
import com.scnsoft.eldermark.web.controller.provider.CcdModelAttributesProviderFactory;
import com.scnsoft.eldermark.ws.server.exceptions.InternalServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


//@Controller
//@RequestMapping(value = "/patient-info/{residentId}/ccd")
//@PreAuthorize(SecurityExpressions.IS_ELDERMARK_USER)
public class CcdController {

    @Autowired
    private CcdFacade ccdFacade;

    @Autowired
    private CcdModelAttributesProviderFactory ccdModelAttributesProviderFactory;

    @Autowired
    private ProblemService problemService;

    @ResponseBody
    @RequestMapping(value = "/{sectionName}/{aggregated}/total", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Long getCcdSectionsTotal(@PathVariable(value = "residentId") Long residentId,
                                    @PathVariable(value = "sectionName") String sectionName,
                                    @PathVariable(value = "aggregated") Boolean aggregated) {
        ccdFacade.canViewCcdOrThrow(residentId);
        if (!ccdFacade.validateSectionName(sectionName)) {
            throw new InternalServerException();
        }
        return ccdFacade.getCcdSectionDtoCount(sectionName, residentId, aggregated);
    }

    @RequestMapping(value = "/headerDetails/{aggregated}", method = RequestMethod.GET)
    public String initHeaderDetailsView(@PathVariable(value = "residentId") Long residentId,
                                        @PathVariable(value = "aggregated") Boolean aggregated,
                                        Model model) {
        model.addAttribute("ccdHeaderDetails", ccdFacade.getCcdHeaderDetails(residentId, aggregated));
        model.addAttribute("residentId", residentId);
        return "patient.ccdHeaderDetails.view";
    }

    @RequestMapping(value = "/{sectionName}/{aggregated}", method = RequestMethod.GET)
    public String initCcdSectionView(@PathVariable(value = "residentId") Long residentId,
                                     @PathVariable(value = "sectionName") String sectionName,
                                     @PathVariable(value = "aggregated") Boolean aggregated,
                                     Model model) {
        model.addAttribute("residentId", residentId);
        model.addAttribute("aggregated", aggregated);
        model.addAttribute("searchScope", SearchScope.ELDERMARK.getCode());
        return String.format("patient.%s.view", sectionName);
    }

    @ResponseBody
    @RequestMapping(value = "/{sectionName}/{aggregated}/results", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page getCcdSections(@PathVariable(value = "residentId") Long residentId,
                               @PathVariable(value = "sectionName") String sectionName,
                               @PathVariable(value = "aggregated") Boolean aggregated,
                               Pageable pageRequest) {
        if (!ccdFacade.validateSectionName(sectionName)) {
            throw new InternalServerException();
        }

        List<? extends CcdSectionDto> result = ccdFacade.getCcdSectionDto(sectionName, residentId, pageRequest, aggregated);
        long totalCount = ccdFacade.getCcdSectionDtoCount(sectionName, residentId, aggregated);

        return new PageImpl(result, pageRequest, totalCount);
    }

    @RequestMapping(value = "/{sectionName}/add", method = RequestMethod.GET)
    public String addCcdGet(Model model,
                            @PathVariable(value = "residentId") Long residentId,
                            @PathVariable(value = "sectionName") String sectionName) {
        ccdFacade.canAddCcdOrThrow(residentId);
        final Optional<CcdModelAttributesProvider> provider = ccdModelAttributesProviderFactory.getModelAttributesProvider(sectionName);
        if (!provider.isPresent()) {
            throw new InternalServerException();
        }
        model.addAllAttributes(provider.get().getAttributesForAdd(residentId));
        model.addAttribute("mode", "add");
        model.addAttribute("buttonTitle", "SUBMIT");
        return String.format("patient.%s.modal", sectionName);
    }

    //todo consider generalising for all sections
    @RequestMapping(value = "/problems/add", method = RequestMethod.POST)
    @ResponseBody
    public void addProblemCcdPost(@PathVariable(value = "residentId") Long residentId,
                                  @ModelAttribute("problem") ProblemObservationDto problemObservationDto) {
        ccdFacade.canAddCcdOrThrow(residentId);
        problemService.createProblemObservation(problemObservationDto, residentId);

    }

    @RequestMapping(value = "/problems/edit/{ccdEntryId}", method = RequestMethod.POST)
    @ResponseBody
    public void editProblemCcdPost(@PathVariable(value = "residentId") Long residentId,
                                   @ModelAttribute("problem") ProblemObservationDto problemObservationDto) {
        throw new BusinessAccessDeniedException();
//        ccdFacade.canEditCcdOrThrow(residentId);
//        problemService.editProblemObservation(problemObservationDto, residentId);
    }

    @RequestMapping(value = "/{sectionName}/edit/{ccdEntryId}", method = RequestMethod.GET)
    public String editCcdGet(Model model,
                             @PathVariable(value = "residentId") Long residentId,
                             @PathVariable(value = "sectionName") String sectionName,
                             @PathVariable(value = "ccdEntryId") Long ccdEntryId) {

        throw new BusinessAccessDeniedException();
//        ccdFacade.canEditCcd(residentId);
//        final Optional<CcdModelAttributesProvider> provider = ccdModelAttributesProviderFactory.getModelAttributesProvider(sectionName);
//        if (!provider.isPresent()) {
//            throw new InternalServerException();
//        }
//        model.addAllAttributes(provider.get().getAttributesForEdit(residentId, ccdEntryId));
//        model.addAttribute("mode", "edit");
//        model.addAttribute("buttonTitle", "SUBMIT");
//        return String.format("patient.%s.modal", sectionName);
    }

    @RequestMapping(value = "/{sectionName}/view/{problemObservationId}", method = RequestMethod.GET)
    public String viewCcdGet(Model model,
                             @PathVariable(value = "residentId") Long residentId,
                             @PathVariable(value = "sectionName") String sectionName,
                             @PathVariable(value = "problemObservationId") Long problemObservationId) {
        ccdFacade.canViewCcdOrThrow(residentId);
        final Optional<CcdModelAttributesProvider> provider = ccdModelAttributesProviderFactory.getModelAttributesProvider(sectionName);
        if (!provider.isPresent()) {
            throw new InternalServerException();
        }
        model.addAllAttributes(provider.get().getAttributesForView(residentId, problemObservationId));
        model.addAttribute("mode", "view");
        return String.format("patient.%s.modal", sectionName);
    }

    @RequestMapping(value = "/problems/delete/{problemObservationId}", method = RequestMethod.POST)
    @ResponseBody
    public void editProblemCcdPost(@PathVariable(value = "residentId") Long residentId,
                                   @PathVariable(value = "problemObservationId") Long problemObservationId) {
        throw new BusinessAccessDeniedException();
//        ccdFacade.canDeleteCcdOrThrow(residentId);
//        problemService.deleteProblemObservation(problemObservationId);
    }

    @ResponseBody
    @RequestMapping(value = "/problems/diagnosisInfo", method = RequestMethod.GET)
    public Page<CcdCodeDto> getDiagnosisInfo(@RequestParam(value = "value.id") Long diagnosisCcdId
    ) {
        List<CcdCodeDto> codes = problemService.listDiagnosisCodesWithSameName(diagnosisCcdId);
        return new PageImpl<>(codes, null, codes.size());
    }

    @ResponseBody
    @RequestMapping(value = "/problems/primary", method = RequestMethod.GET)
    public Long getPrimary(@PathVariable(value = "residentId") Long residentId) {
        return problemService.getPrimaryObservationId(residentId).or(-1L);
    }

    @ResponseBody
    @RequestMapping(value = "/problems/problemValue", method = RequestMethod.GET)
    public Page<CcdCodeDto> getProblemValue(//todo customize searchString param in url
                                            @RequestParam(value = "value.id_searchInput") String searchString,
                                            Pageable pageRequest
    ) {
        return problemService.listDiagnosisCodes(searchString, pageRequest);
    }

    @InitBinder(value = "problem")
    public void dateBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomDateSerializer.EXCHANGE_DATE_FORMAT);
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, "onSetDate", editor);
    }

    @InitBinder(value = "problem")
    public void dateTimeBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a '('XXX')'");
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);
    }

}
