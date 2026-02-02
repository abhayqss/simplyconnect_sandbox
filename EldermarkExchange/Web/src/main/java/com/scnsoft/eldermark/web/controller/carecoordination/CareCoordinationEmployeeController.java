package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.services.carecoordination.ContactService;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by pzhurba on 21-Oct-15.
 */
//@Controller
//@RequestMapping(value = "/care-coordination/employees")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationEmployeeController {
    @Autowired
    ContactService contactService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    List<KeyValueDto> searchEmployees(@RequestParam(value = "q", required = false) String searchString) {
        return contactService.searchEmployee(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId(), null, searchString);
    }

//    @RequestMapping(value = "/db/",method = RequestMethod.GET)
//    @ResponseBody
//    List<KeyValueDto> searchDbEmployees() {
//        return contactService.searchEmployee(SecurityUtils.getAuthenticatedUser().getEmployee().getDatabaseId(),null);
//    }

    @RequestMapping(value = "/affiliated/{communityId}", method = RequestMethod.GET)
    @ResponseBody
    List<KeyValueDto> getAffiliatedEmployees(@PathVariable(value = "communityId") Long communityId) {
        Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
        if(SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return contactService.getAffiliatedEmployees(communityId);
        }
//        else if(SecurityUtils.hasRole(CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
//            return contactService.searchEmployee(employee.getDatabaseId(),employee.getCommunityId(),null);
//        }
        else {
            return contactService.searchEmployee(employee.getDatabaseId(),null,null);
        }
    }

//    @RequestMapping(value = "/affiliated/forPatient/{patientId}", method = RequestMethod.GET)
//    @ResponseBody
//    List<KeyValueDto> getAffiliatedEmployeesForPatient(@PathVariable(value = "patientId") Long patientId) {
//        if(SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
//
//            return contactService.getAffiliatedEmployees(communityId);
//        }
//        else {
//            return contactService.searchEmployee(SecurityUtils.getAuthenticatedUser().getEmployee().getDatabaseId(),null);
//        }
//    }

}
