package com.scnsoft.eldermark.web.controller.administration;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.facades.ResidentFacade;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilterUiDto;
import com.scnsoft.eldermark.shared.SearchScope;
import com.scnsoft.eldermark.shared.administration.SearchMode;
import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author phomal
 * Created on 07/04/17
 */
//@Controller
//@RequestMapping(value = "/administration/manual-matching")
//@SessionAttributes("patientFilterManual")
//@PreAuthorize(SecurityExpressions.IS_CC_SUPERADMIN)
public class AdministrationManualMatchingController {

    @Autowired
    private ResidentFacade residentFacade;

    @InitBinder(value = "patientFilterManual")
    public void dateBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomDateSerializer.EXCHANGE_DATE_FORMAT);
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);
    }

    @ModelAttribute("genderValues")
    public Gender[] getFilterGenders() {
        return Gender.values();
    }

    @ModelAttribute("patientFilterManual")
    public ResidentFilterUiDto getPatientFilter() {
        ResidentDto defaultResident = residentFacade.getDefaultResident();
        return dtoToFilter(defaultResident);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String initView() {
        return "administration.matching.manual";
    }

    private static ResidentFilterUiDto dtoToFilter(ResidentDto defaultResident) {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();

        filter.setGender(null);
        filter.setFirstName(defaultResident.getFirstName());
        filter.setLastName(defaultResident.getLastName());
        filter.setSearchScopes(Collections.singleton(SearchScope.ELDERMARK));
        filter.setMode(SearchMode.MATCH_ALL);
        filter.setDateOfBirth(defaultResident.getDateOfBirth());
        filter.setLastFourDigitsOfSsn(defaultResident.getSsn());
        filter.setSsnRequired(false);
        filter.setDateOfBirthRequired(false);

        return filter;
    }

    @RequestMapping(value = "/patients", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<ResidentDto> searchPatients(@ModelAttribute("patientFilterManual") ResidentFilterUiDto patientFilter,
                                            Pageable pageRequest) throws Exception {
        List<ResidentDto> result;
        long totalCount;

        patientFilter.setSearchScopes(Collections.singleton(SearchScope.ELDERMARK));

        result = residentFacade.getResidents(patientFilter, pageRequest, true);
        // can't short-circuit calculations here
        totalCount = residentFacade.getResidentCount(patientFilter);

        return new PageImpl<ResidentDto>(result, pageRequest, totalCount);
    }

    @RequestMapping(value = "/patients", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<ResidentDto> comparePatients(@RequestParam("ids") List<Long> patientIds) throws Exception {
        List<ResidentDto> patients = residentFacade.getResidentsByIds(patientIds);
        return new PageImpl<ResidentDto>(patients, null, patients.size());
    }

    @RequestMapping(value = "/step2", method = RequestMethod.GET)
    public String initManualMatchingStep2View() {
        return "administration.matching.manual.step2";
    }

    @RequestMapping(value = "/confirmation", method = RequestMethod.GET)
    public String initManualMatchingConfirmationView(@RequestParam(value = "matching", required = true) List<Long> matchingIds,
                                                     @RequestParam(value = "mismatching", required = false) List<Long> mismatchingIds,
                                                     Model model) {
        model.addAttribute("matchingResidents", residentFacade.getResidentsByIds(matchingIds));
        model.addAttribute("mismatchingResidents", residentFacade.getResidentsByIds(mismatchingIds));
        return "administration.matching.manual.confirmation";
    }

    @RequestMapping(value = "/resolve", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void resolveManualMatching(@RequestBody Map<Long, Boolean> residents) {
        residentFacade.updateMatchedResidents(residents);
    }

}
