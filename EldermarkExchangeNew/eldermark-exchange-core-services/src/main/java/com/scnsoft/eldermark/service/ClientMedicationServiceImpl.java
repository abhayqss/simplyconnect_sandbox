package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientMedicationCount;
import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientMedicationSecurityAwareEntity;
import com.scnsoft.eldermark.dao.ClientMedicationDao;
import com.scnsoft.eldermark.dao.specification.ClientMedicationSpecificationGenerator;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class ClientMedicationServiceImpl implements ClientMedicationService {

    @Autowired
    private ClientMedicationDao clientMedicationDao;

    @Autowired
    private ClientMedicationSpecificationGenerator clientMedicationSpecificationGenerator;

    @Override
    public Page<ClientMedication> find(ClientMedicationFilter filter, PermissionFilter permissionFilter, Pageable pageRequest) {
        var byFilterAndHasAccessWithoutDuplicates = clientMedicationSpecificationGenerator.byFilterAndHasAccessWithoutDuplicates(permissionFilter, filter);
        return clientMedicationDao.findAll(byFilterAndHasAccessWithoutDuplicates, pageRequest);
    }

    @Override
    public ClientMedication findById(Long medicationId) {
        return clientMedicationDao.findById(medicationId).orElseThrow();
    }

    @Override
    public List<ClientMedicationCount> countGroupedByStatus(ClientMedicationFilter filter, PermissionFilter permissionFilter) {
        var byFilterAndHasAccessWithoutDuplicates = clientMedicationSpecificationGenerator.byFilterAndHasAccessWithoutDuplicates(permissionFilter, filter);
        return clientMedicationDao.countGroupedByStatus(byFilterAndHasAccessWithoutDuplicates);
    }

    @Override
    public ClientMedicationSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return clientMedicationDao.findById(id, ClientMedicationSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    public List<ClientMedicationSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return clientMedicationDao.findByIdIn(ids, ClientMedicationSecurityAwareEntity.class);
    }

    @Override
    public boolean existsInOrganization(PermissionFilter permissionFilter, Long organizationId) {
        var hasAccess = clientMedicationSpecificationGenerator.hasAccess(permissionFilter);
        var byOrganizationId = clientMedicationSpecificationGenerator.byOrganizationId(organizationId);

        return clientMedicationDao.exists(hasAccess.and(byOrganizationId));
    }

    @Override
    public boolean existsInCommunity(PermissionFilter permissionFilter, Long communityId) {
        var hasAccess = clientMedicationSpecificationGenerator.hasAccess(permissionFilter);
        var byCommunityId = clientMedicationSpecificationGenerator.byCommunityId(communityId);

        return clientMedicationDao.exists(hasAccess.and(byCommunityId));
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return clientMedicationDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientMedicationDao.findByIdIn(ids, projection);
    }
}
