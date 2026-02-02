package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.LabResearchOrderFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.LabOrderSecurityAwareEntity;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.lab.*;
import com.scnsoft.eldermark.entity.lab.review.LabResearchOrderBulkReviewListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface LabResearchOrderService extends ProjectingService<Long> {

    String COVID_CODE = "703398";

    LabResearchOrder create(LabResearchOrder entity);

    LabResearchOrder findById(Long id);

    void review(Long id);

    Page<LabResearchOrderListItem> findLabOrders(LabResearchOrderFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    List<LabIcd10Group> findIcdGroupCodes();

    List<SpecimenType> findSpecimens();

    boolean isRequisitionNumberUniqueInOrganization(String requisitionNumber, Long organizationId);

    LabResearchOrderORU createOruInNewTransaction(LabResearchOrderORU orderOru);

    void updateOrderOruFailInNewTransaction(Long id, String errorMessage);

    Page<LabResearchOrderObservationResult> findLabResults(Long labResearchOrderId, Pageable pageable);

    long count(LabResearchOrderFilter filter, PermissionFilter permissionFilter);

    List<LabResearchOrderBulkReviewListItem> findReviewOrders(LabResearchOrderFilter filter, PermissionFilter permissionFilter, Sort sort);

    LabOrderSecurityAwareEntity findSecurityAware(Long id);

    List<Employee> findReviewers(LabResearchOrder labResearchOrder);
}
