package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientMedicationCount;
import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientMedicationSecurityAwareEntity;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientMedicationService extends SecurityAwareEntityService<ClientMedicationSecurityAwareEntity, Long>,
        ProjectingService<Long> {

    Page<ClientMedication> find(ClientMedicationFilter filter, PermissionFilter permissionFilter, Pageable pageRequest);

    ClientMedication findById(Long medicationId);

    List<ClientMedicationCount> countGroupedByStatus(ClientMedicationFilter filter, PermissionFilter permissionFilter);

    boolean existsInOrganization(PermissionFilter permissionFilter, Long organizationId);

    boolean existsInCommunity(PermissionFilter permissionFilter, Long communityId);
}
