package com.scnsoft.eldermark.services.carecoordination;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanHistoryListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.serviceplan.ServicePlanListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ServicePlanService extends AuditableEntityService<ServicePlan, ServicePlanDto> {

    Page<ServicePlanListItemDto> listPatientServicePlans(Long patientId, String search, Pageable pageRequest);

    Long count(Long patientId);

    Long save(ServicePlanDto servicePlanDto, Long patientId);

    ServicePlanDto getServicePlanDetails(Long servicePlanId);

    ByteArrayOutputStream generatePdf(ServicePlan servicePlan, Long timeZoneOffset) throws DocumentException, IOException;

    ServicePlan getServicePlan(Long servicePlanId);

    Boolean isNewServicePlanCanBeAddedForPatient(Long patientId);

    Page<ServicePlanHistoryListItemDto> listServicePlanHistory(Long servicePlanId, Pageable pageRequest);
}
