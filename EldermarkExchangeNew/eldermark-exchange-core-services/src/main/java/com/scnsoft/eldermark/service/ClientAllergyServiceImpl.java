package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientAllergyFilter;
import com.scnsoft.eldermark.beans.projection.ClientAllergyAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientAllergySecurityAwareEntity;
import com.scnsoft.eldermark.dao.ClientAllergyDao;
import com.scnsoft.eldermark.dao.specification.ClientAllergySpecificationGenerator;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

@Service
@Transactional
public class ClientAllergyServiceImpl implements ClientAllergyService {

    @Autowired
    private ClientAllergyDao clientAllergyDao;

    @Autowired
    private ClientAllergySpecificationGenerator allergySpecificationGenerator;

    @Override
    public ClientAllergy findById(Long id) {
        return clientAllergyDao.findById(id).orElseThrow();
    }

    @Override
    public Page<ClientAllergy> find(ClientAllergyFilter filter, PermissionFilter permissionFilter, Pageable pageable) {
        var byFilterNotEmptyAndHasAccessWithoutDuplicates = allergySpecificationGenerator.byFilterAndNotEmptyAndHasAccessWithoutDuplicates(permissionFilter, filter);
        return clientAllergyDao.findAll(byFilterNotEmptyAndHasAccessWithoutDuplicates, pageable);
    }

    @Override
    public Long count(ClientAllergyFilter filter, PermissionFilter permissionFilter) {
        var byFilterNotEmptyAndHasAccessWithoutDuplicates = allergySpecificationGenerator.byFilterAndNotEmptyAndHasAccessWithoutDuplicates(permissionFilter, filter);
        return clientAllergyDao.count(byFilterNotEmptyAndHasAccessWithoutDuplicates);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientAllergyAware> findAllByClientId(Long clientId, PermissionFilter permissionFilter) {
        var clientFilter = new ClientAllergyFilter();
        clientFilter.setClientId(clientId);
        clientFilter.setStatuses(EnumSet.of(ClientAllergyStatus.ACTIVE));
        var specification =
            allergySpecificationGenerator.byFilterAndNotEmptyAndHasAccessWithoutDuplicates(permissionFilter, clientFilter);
        return clientAllergyDao.findAll(specification, ClientAllergyAware.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientAllergySecurityAwareEntity findSecurityAwareEntity(Long aLong) {
        return clientAllergyDao.findById(aLong, ClientAllergySecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientAllergySecurityAwareEntity> findSecurityAwareEntities(Collection<Long> longs) {
        return clientAllergyDao.findByIdIn(longs, ClientAllergySecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return clientAllergyDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientAllergyDao.findByIdIn(ids, projection);
    }
}
