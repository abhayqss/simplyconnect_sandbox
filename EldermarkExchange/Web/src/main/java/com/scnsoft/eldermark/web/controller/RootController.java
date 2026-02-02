package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.services.carecoordination.OrganizationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@Controller
//@RequestMapping(value = "/")
public class RootController {

    @Autowired
    OrganizationService organizationService;

    @RequestMapping
    @PreAuthorize(SecurityExpressions.IS_EXCHANGE_USER)
    public String rootView(@RequestParam(value = "startPage", required = false) String startPage,
                           @RequestParam(value = "id", required = false) String id,
                           @RequestParam(value = "orgId", required = false) String orgId,
                           @RequestParam(value = "note", required = false) String noteId,
                           @RequestParam(value = "patient", required = false) String patientId,
                           Model model) {
        if (StringUtils.isNotBlank(startPage)) {
            model.addAttribute("startPage", startPage);
            model.addAttribute("id", id);
            model.addAttribute("note", noteId);
            model.addAttribute("patient", patientId);
            if (StringUtils.isNotBlank(orgId)) {
                organizationService.setCurrentOrganization(Long.parseLong(orgId));
            }
        }

        model.addAttribute("unaffiliatedUser",SecurityUtils.isUnaffiliatedUser());
        return "start.view";
    }

    @RequestMapping("header")
    @PreAuthorize("isAuthenticated()")
    public String initHeaderView(Model model){
        if (SecurityUtils.hasAnyRole(CareTeamRoleCode.ALL_ROLES)) {
            boolean showOrganizationFilter = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR) || !organizationService.isSingleOrganizationAccessible();
            model.addAttribute("showOrganizationFilter", showOrganizationFilter);
        }
        boolean unaffiliatedUser = SecurityUtils.isUnaffiliatedUser();
        if (SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_VIEW_REPORTS) && !unaffiliatedUser) {
            model.addAttribute("showReports", true);
        }
        model.addAttribute("unaffiliatedUser",unaffiliatedUser);
        return "header.view";
    }

    @RequestMapping("footer")
    @PreAuthorize("isAuthenticated()")
    public String initFooterView(){
        return "footer.view";
    }

    @RequestMapping("start")
    @PreAuthorize(SecurityExpressions.IS_EXCHANGE_USER)
    public String initStartView(){
        return "start.view";
    }

//    @RequestMapping(value = "redirect")
//    @PreAuthorize(SecurityExpressions.IS_EXCHANGE_USER)
//    public String redirectedView(@PathVariable(value = "startPage") String startPage) {
////        model.addAttribute("startPage", startPage);
//        return "forward:/start?startPage="+startPage;
//    }
}
