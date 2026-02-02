package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationResidentService;
import com.scnsoft.eldermark.services.carecoordination.CommunityCrudService;
import com.scnsoft.eldermark.services.exceptions.BusinessException;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientsFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.form.UploadDocumentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by pzhurba on 13-Nov-15.
 */
//@Controller
//@RequestMapping(value = "/care-coordination/patients")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationPatientsController {
    @Autowired
    private CareCoordinationResidentService careCoordinationResidentService;
    @Autowired
    private PatientFacade patientFacade;
    @Autowired
    private CommunityCrudService communityCrudService;
    @Autowired
    private StateService stateService;

    @RequestMapping(method = RequestMethod.GET)
    public String getPatientsView(Model model) {
        model.addAttribute("filter", new PatientsFilterDto());
        model.addAttribute("affiliatedView", SecurityUtils.isAffiliatedView());
        model.addAttribute("uploadDocumentForm", new UploadDocumentForm());
        return "care.coordination.patients";
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Page<PatientListItemDto> getPatients(@ModelAttribute("filter") PatientsFilterDto filter, Pageable pageable) {
        return careCoordinationResidentService.getPatientListItemDtoForEmployee(SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds(), filter, pageable);
    }

    @RequestMapping(value = "/find-matches",method = RequestMethod.POST)
    public Object findPatientMatches(@ModelAttribute PatientDto dto, Model model, HttpServletResponse response) throws IOException {
        if ( careCoordinationResidentService.isExistResident(dto)) {
                throw new BusinessException("Resident with such SSN, Date Of Birth, LastName and FirstName already exists in the system");
        }

        List<PatientDto> patientDtoList = patientFacade.findMatchedPatients(dto);
        if (patientDtoList ==null) {
            MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            MediaType jsonMimeType = MediaType.APPLICATION_JSON;
            jsonConverter.write(false, jsonMimeType, new ServletServerHttpResponse(response));
////            response.setContentType("application/json");
////            PrintWriter out = response.getWriter();
////            out.print("{data:false}");
//            out.flush();
            return null;

        }
        Long communityId = dto.getCommunityId();
        if (communityId!=null)  {
            dto.setCommunity(communityCrudService.getCommunityName(communityId));
        }
        Gender gender = Gender.getGenderByCode(dto.getGender());
        dto.setGender(gender.getLabel());
        State state = stateService.get(dto.getAddress().getState().getId());
        dto.getAddress().setState(CareCoordinationUtils.createKeyValueDto(state));
        model.addAttribute("patientMatchesList", patientDtoList);
        model.addAttribute("newPatient", dto);
        return "care.coordination.patient.matches";
    }

    @ModelAttribute("genderValues")
    public Gender[] getFilterGenders() {
        return Gender.values();
    }

    @RequestMapping(method=RequestMethod.GET, value="/count")
    public @ResponseBody Long getPatientsCount() {
        return careCoordinationResidentService.getResidentsCountForCurrentUserAndOrganization();
    }

}
