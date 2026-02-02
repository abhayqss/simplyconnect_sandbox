package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.LabResearchOrderFilter;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.dto.lab.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface LabResearchOrderFacade {

    Long add(LabResearchOrderDto dto);

    LabResearchOrderDto findDefault(Long clientId);

    List<IdentifiedTitledEntityDto> findCollectorSites(Long organizationId);

    LabResearchOrderDto findById(Long id);

    void review(LabResearchReviewDto reviewDto);

    Page<LabResearchOrderListItemDto> find(LabResearchOrderFilter filter, Pageable pageable);

    Page<LabResearchTestResultListItemDto> findTestResults(Long labResearchOrderId, Pageable pageable);

    List<LabIcd10GroupDto> findIcdGroupCodes();

    List<IdentifiedNamedTitledEntityDto> findSpecimens();

    boolean canAddToCommunity(Long communityId);

    boolean canView();

    LabResearchOrderOrganizationUniquenessDto validateUniqueInOrganization(String requisitionNumber, Long communityId);

    boolean canAddToClient(Long clientId);

    boolean canReview(Long organizationId);

    long count(LabResearchOrderFilter filter);

    List<LabResearchOrderBulkReviewListItemDto> findPendingReview(Long organizationId, List<Long> communityIds);
}
