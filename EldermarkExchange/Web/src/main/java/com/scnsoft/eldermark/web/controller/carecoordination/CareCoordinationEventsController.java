package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationResidentService;
import com.scnsoft.eldermark.services.carecoordination.EventGroupService;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.services.carecoordination.EventTypeService;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventListItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by pzhurba on 13-Nov-15.
 */
//@Controller
//@RequestMapping(value = "/care-coordination/events-log")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationEventsController {
    @Autowired
    private EventService eventService;

    @Autowired
    private CareCoordinationResidentService careCoordinationResidentService;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private EventGroupService eventGroupService;


    @RequestMapping(method = RequestMethod.GET)
    public String initEventsLogView(Model model) {
        long startTime = System.currentTimeMillis();
        final EventFilterDto filter = new EventFilterDto();
        List<Long> patientIds = getPatientIdsFromRequest(model);
        long stopTime0 = System.currentTimeMillis();
        System.out.println("initEventsLogView:" + (stopTime0 - startTime) + "ms");

        filter.setDateFrom(eventService.getEventsMinimumDate(patientIds));
        filter.setDateTo(new Date());
//        System.out.println("");
//        for (Long patientId:patientIds) {
//            System.out.print(patientId+ ",");
//        }
//        System.out.println("");

        model.addAttribute("eventFilter", filter);
        long stopTime = System.currentTimeMillis();
        System.out.println("initEventsLogView:" + (stopTime - stopTime0) + "ms");
        return "events.log";
    }


    @RequestMapping(value = "/events", method = RequestMethod.POST)
    @ResponseBody
    public Page<EventListItemDto> getEvents(@ModelAttribute("eventFilter") EventFilterDto eventFilter, Pageable pageRequest) {
        Set<Long> employeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds();
        return eventService.list(employeeIds, eventFilter, pageRequest);
    }


    @ModelAttribute("patients")
    public List<KeyValueDto> getPatients() {
        return careCoordinationResidentService.getResidentsNamesForEmployee(SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds());
    }

    @ModelAttribute("eventTypes")
    public List<KeyValueDto> getEventServices() {
        return eventTypeService.getAllEventTypes();
    }

    @ModelAttribute("eventGroups")
    public List<KeyValueDto> getEventGroups() {
        return eventGroupService.getAllEventGroups();
    }


    private List<Long> getPatientIdsFromRequest(Model model) {
        Map modelMap = model.asMap();
        List<Long> patientIds = new ArrayList<Long>();
        if (modelMap.containsKey("patients")) {
            List<KeyValueDto> patients = (List<KeyValueDto>)modelMap.get("patients");
            for (KeyValueDto patient : patients) {
                patientIds.add(patient.getId());
            }
        }
        return patientIds;
    }

}
