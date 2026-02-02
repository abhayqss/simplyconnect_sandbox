package com.scnsoft.eldermark.web.controller.administration;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.facades.ResidentFacade;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilterUiDto;
import com.scnsoft.eldermark.shared.SearchScope;
import com.scnsoft.eldermark.shared.administration.MatchStatus;
import com.scnsoft.eldermark.shared.administration.MergeStatus;
import com.scnsoft.eldermark.shared.administration.SearchFilter;
import com.scnsoft.eldermark.shared.administration.SearchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author phomal
 * Created on 07/04/17.
 */
//@Controller
//@RequestMapping(value = "/administration/suggested-matches")
//@SessionAttributes("patientFilterSuggested")
//@PreAuthorize(SecurityExpressions.IS_CC_SUPERADMIN)
public class AdministrationSuggestedMatchesController {

    @Autowired
    private ResidentFacade residentFacade;

    @ModelAttribute("patientFilterSuggested")
    public SearchFilter getSimplePatientFilter() {
        return new SearchFilter();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String initView(Model model) {
        model.addAttribute("filter", new SearchFilter());

        return "administration.matching.suggested";
    }

    private static ResidentFilterUiDto dtoToFilter(ResidentDto defaultResident, SearchFilter searchFilter) {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();

        filter.setGender(null);
        filter.setSearchScopes(Collections.singleton(SearchScope.ELDERMARK));
        filter.setMode(SearchMode.MATCH_ANY_LIKE);
        filter.setMatchStatus(MatchStatus.MAYBE_MATCHED);
        filter.setMergeStatus(MergeStatus.NOT_MERGED);
        filter.setDateOfBirth(defaultResident.getDateOfBirth());
        filter.setLastFourDigitsOfSsn(defaultResident.getSsn());
        filter.setSsnRequired(false);
        filter.setDateOfBirthRequired(false);

        filter.setFirstName(searchFilter.getQuery());
        filter.setLastName(searchFilter.getQuery());
        filter.setSsn(searchFilter.getQuery());
        filter.setCommunity(searchFilter.getQuery());
        filter.setProviderOrganization(searchFilter.getQuery());

        return filter;
    }

    @RequestMapping(value = "/patients", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<ResidentDto> searchSuggestedPatients(@ModelAttribute("patientFilterSuggested") SearchFilter searchFilter,
                                                     Pageable pageRequest) throws Exception {
        List<ResidentDto> result;
        long totalCount;

        ResidentDto defaultResident = residentFacade.getDefaultResident();
        ResidentFilterUiDto filter = dtoToFilter(defaultResident, searchFilter);
        PageRequest pageRequestWithSort = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                new Sort(Sort.Direction.ASC, "firstName"));

        result = residentFacade.getResidents(filter, pageRequestWithSort,true);
        // can't short-circuit calculations here
        totalCount = residentFacade.getResidentCount(filter);

        return new PageImpl<ResidentDto>(result, pageRequestWithSort, totalCount);
    }

    @RequestMapping(value = "/patients", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<ResidentDto> comparePatients(@RequestParam("id") Long mainPatientId) throws Exception {
        List<ResidentDto> resultList = residentFacade.getProbablyMatchedResidentsById(mainPatientId);
        return new PageImpl<ResidentDto>(resultList, null, resultList.size());
    }

    @RequestMapping(value = "/step2", method = RequestMethod.GET)
    public String initSuggestedMatchesStep2View() {
        return "administration.matching.suggested.step2";
    }

    @RequestMapping(value = "/confirmation", method = RequestMethod.GET)
    public String initSuggestedMatchesConfirmationView(@RequestParam(value = "matching", required = true) List<Long> matchingIds,
                                                       @RequestParam(value = "mismatching", required = false) List<Long> mismatchingIds,
                                                       Model model) {
        model.addAttribute("matchingResidents", residentFacade.getResidentsByIds(matchingIds));
        model.addAttribute("mismatchingResidents", residentFacade.getResidentsByIds(mismatchingIds));
        return "administration.matching.suggested.confirmation";
    }

    @RequestMapping(value = "/resolve", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void resolveSuggestedMatches(@RequestBody Map<Long, Boolean> residents) {
        residentFacade.updateMatchedResidents(residents);
    }

}
