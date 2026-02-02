package com.scnsoft.eldermark.web.controller;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.facades.ConnectNhinGateway;
import com.scnsoft.eldermark.facades.ResidentFacade;
import com.scnsoft.eldermark.services.nwhin.CcdMediator;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilterUiDto;
import com.scnsoft.eldermark.shared.SearchScope;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.shared.exceptions.FormValidationException;
import com.scnsoft.eldermark.shared.form.HiddenResidentForm;
import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.scnsoft.eldermark.web.validator.PatientDiscoveryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Controller
//@RequestMapping(value = "/patient-search")
//@PreAuthorize(SecurityExpressions.IS_ELDERMARK_USER)
public class PatientController {

    @Autowired
    private ResidentFacade residentFacade;

    @Autowired
    private ConnectNhinGateway connectNhinGateway;

    @Autowired
    private CcdMediator ccdMediator;

    @Autowired
    private PatientDiscoveryValidator patientSearchValidator;
    public @Value("${patient.discovery.ssn.required}") boolean ssnRequired;
    public @Value("${patient.discovery.dateOfBirth.required}") boolean dateOfBirthRequired;


    @InitBinder(value = "patientFilter")
    public void dateBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomDateSerializer.EXCHANGE_DATE_FORMAT);
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);

        binder.addValidators(patientSearchValidator);
    }


    @ModelAttribute("genderValues")
    public Gender[] getFilterGenders() {
        return Gender.values();
    }

    @ModelAttribute("searchScopeValues")
    public SearchScope[] getFilterSearchScopes() {
        return SearchScope.values();
    }


    @RequestMapping(method = RequestMethod.GET)
    public String initView(Model model) {
        checkUnaffiliatedUser();
        ResidentFilterUiDto filter = new ResidentFilterUiDto();
        ResidentDto defaultResident = residentFacade.getDefaultResident();
        filter.setGender(defaultResident.getGender());
        filter.setFirstName(defaultResident.getFirstName());
        filter.setLastName(defaultResident.getLastName());
        filter.setSearchScopes(Sets.newEnumSet(Sets.newHashSet(SearchScope.values()), SearchScope.class));
        filter.setDateOfBirth(defaultResident.getDateOfBirth());
        filter.setLastFourDigitsOfSsn(defaultResident.getSsn());
        filter.setSsnRequired(ssnRequired);
        filter.setDateOfBirthRequired(dateOfBirthRequired);

        model.addAttribute("patientFilter", filter);

        model.addAttribute("hiddenResidentForm", new HiddenResidentForm());

        return "patient.patientSearch";
    }


    @RequestMapping(value = "/results-scope", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<ResidentDto> search(@Valid @ModelAttribute("patientFilter") ResidentFilterUiDto patientFilter,
                                    @RequestParam(value = "searchScope", required = true) SearchScope searchScope,
                                    @AuthenticationPrincipal ExchangeUserDetails userDetails,
                                    BindingResult bindingResult, Pageable pageRequest, HttpServletRequest request) throws Exception {
        checkUnaffiliatedUser();
        if (bindingResult.hasErrors()) {
            throw new FormValidationException();
        }

        HttpSession session = request.getSession();
        session.setAttribute("patientFilter", patientFilter);

        List<ResidentDto> result = new ArrayList<ResidentDto>();
        long totalCount = 0L;

        if (SearchScope.ELDERMARK == searchScope) {
            result = residentFacade.getResidents(patientFilter, pageRequest, false);
            if (result.size() < pageRequest.getPageSize()) {
                // FIXME: It's not a reliable way to make this optimization cause result.size() here is not the same as a number of distinct residents returned by SQL query.
                // FIXME: (see ResidentFacadeImpl#getResidents() for details)
                // short-circuit calculations
                totalCount = result.size() + pageRequest.getPageSize() * pageRequest.getPageNumber();
            } else {
                totalCount = residentFacade.getResidentCount(patientFilter);
            }
        } else if (SearchScope.NWHIN == searchScope) {
            result = connectNhinGateway.patientDiscovery(patientFilter, searchScope.getHomeCommunityId(), userDetails);
            totalCount = result.size();

            for (ResidentDto resident : result) {
                ccdMediator.processCcd(searchScope.getHomeCommunityId(), userDetails, resident);
            }
        }

        return new PageImpl<ResidentDto>(result, pageRequest, totalCount);
    }

    private void checkUnaffiliatedUser() {
        if (SecurityUtils.isUnaffiliatedUser()) {
            throw new BusinessAccessDeniedException("User does not have enough privileges for that operation.");
        }
    }

}
