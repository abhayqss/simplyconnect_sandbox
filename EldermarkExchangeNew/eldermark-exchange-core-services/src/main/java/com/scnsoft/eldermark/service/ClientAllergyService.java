package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientAllergyFilter;
import com.scnsoft.eldermark.beans.projection.ClientAllergyAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientAllergySecurityAwareEntity;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientAllergyService extends SecurityAwareEntityService<ClientAllergySecurityAwareEntity, Long>, ProjectingService<Long> {

    ClientAllergy findById(Long id);

    Page<ClientAllergy> find(ClientAllergyFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    Long count(ClientAllergyFilter filter, PermissionFilter permissionFilter);

    List<ClientAllergyAware> findAllByClientId(Long clientId, PermissionFilter permissionFilter);
}
