package com.scnsoft.eldermark.web.controller.carecoordination;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.services.carecoordination.ServicePlanService;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanHistoryListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanListItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static com.scnsoft.eldermark.dao.serviceplan.ServicePlanDao.ORDER_BY_DATE_CREATED;

//@Controller
//@RequestMapping(value = "/care-coordination/patients/patient/{patientId}/service-plans")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationServicePlanController {

    @Autowired
    private ServicePlanService servicePlanService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<ServicePlanListItemDto> listServicePlans(@PathVariable("patientId") Long patientId, @RequestParam("search") String search,
                                                         Pageable pageRequest) {
        Pageable pageableToSearch = pageRequest;
        if (pageRequest.getSort() == null) {
            pageableToSearch = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), new Sort(ORDER_BY_DATE_CREATED));
        } else  if (pageRequest.getSort().getOrderFor("author") != null) {
            Sort.Order order = pageRequest.getSort().getOrderFor("author");
            pageableToSearch = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), order.getDirection(), "employee.firstName", "employee.lastName");
        } else if (pageRequest.getSort().getOrderFor("scoring") != null) {
            Sort.Order order = pageRequest.getSort().getOrderFor("scoring");
            pageableToSearch = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), order.getDirection(), "scoring.totalScore");
        } else if (pageRequest.getSort().getOrderFor("status") != null) {
            Sort.Order order = pageRequest.getSort().getOrderFor("status");
            pageableToSearch = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), order.getDirection(), "servicePlanStatus");
        }
        return servicePlanService.listPatientServicePlans(patientId, search, pageableToSearch);
    }

    @ResponseBody
    @RequestMapping(value = "/total", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Long getServicePlansTotal(@PathVariable("patientId") Long patientId) {
        return servicePlanService.count(patientId);
    }

    @ResponseBody
    @RequestMapping(value = "/{servicePlanId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ServicePlanDto getServicePlanDetails(@PathVariable("patientId") Long patientId, @PathVariable("servicePlanId") Long servicePlanId) {
        //TODO security
        return servicePlanService.getServicePlanDetails(servicePlanId);
    }

    @ResponseBody
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Long addServicePlan(@PathVariable("patientId") Long patientId, @RequestBody ServicePlanDto servicePlanDto) {
        //TODO security
        return servicePlanService.save(servicePlanDto, patientId);
    }

    @RequestMapping(value = "/{servicePlanId}/pdf", method = RequestMethod.GET)
    public void downloadPdf(
            @PathVariable("patientId") Long patientId, @PathVariable("servicePlanId") Long servicePlanId,
            @RequestParam("timeZoneOffset") Long timeZoneOffset,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        ServicePlan servicePlan = servicePlanService.getServicePlan(servicePlanId);
        ByteArrayOutputStream baos = servicePlanService.generatePdf(servicePlan, timeZoneOffset);
        response.setContentType("application/pdf");
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String dateStartedOrCompleted;
        if (servicePlan.getDateCompleted() != null) {
            dateStartedOrCompleted = df.format(servicePlan.getDateCompleted());
        } else {
            dateStartedOrCompleted = df.format(servicePlan.getDateCreated());
        }
        response.setHeader("Content-Disposition", "attachment;filename=\""
                + "Client Service Plan for " + servicePlan.getResident().getFullName() + " " + dateStartedOrCompleted + ".pdf\"");
        response.setContentLength(baos.size());
        OutputStream os = response.getOutputStream();
        baos.writeTo(os);
        os.flush();
        os.close();  //TODO close in finally block
        baos.close();
    }

    @ResponseBody
    @RequestMapping(value = "/can-create", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean canCreateServicePlan(@PathVariable("patientId") Long patientId) {
        return servicePlanService.isNewServicePlanCanBeAddedForPatient(patientId);
    }

    @ResponseBody
    @RequestMapping(value = "/{servicePlanId}/history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ServicePlanHistoryListItemDto> getServicePlanHistory(@PathVariable("patientId") Long patientId, @PathVariable("servicePlanId") Long servicePlanId, Pageable pageRequest) {
        Pageable pageableToSearch = pageRequest;
        final Sort lastModifiedSort = new Sort(new Sort.Order(Sort.Direction.DESC, "lastModifiedDate"));
        if (pageRequest.getSort() == null) {
            pageableToSearch = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), lastModifiedSort);
        }
        return servicePlanService.listServicePlanHistory(servicePlanId, pageableToSearch);
    }
}
