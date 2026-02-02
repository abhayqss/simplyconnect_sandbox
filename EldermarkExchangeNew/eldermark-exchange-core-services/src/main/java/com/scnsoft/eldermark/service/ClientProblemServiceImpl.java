package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientProblemCount;
import com.scnsoft.eldermark.beans.ClientProblemFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientProblemSecurityAwareEntity;
import com.scnsoft.eldermark.dao.ClientProblemDao;
import com.scnsoft.eldermark.dao.specification.ClientProblemSpecificationGenerator;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientProblemServiceImpl implements ClientProblemService {

    @Autowired
    private ClientProblemDao clientProblemDao;

    @Autowired
    private ClientProblemSpecificationGenerator clientProblemSpecificationGenerator;

    @Override
    public Page<ClientProblem> find(ClientProblemFilter filter, PermissionFilter permissionFilter, Pageable pageRequest) {
        var byFilterAndHasAccessWithoutDuplicates = clientProblemSpecificationGenerator.byFilterAndHasAccessWithoutDuplicates(permissionFilter, filter);
        return clientProblemDao.findAll(byFilterAndHasAccessWithoutDuplicates, pageRequest);
    }

    @Override
    public List<ClientProblem> find(ClientProblemFilter filter, PermissionFilter permissionFilter) {
        var byFilterAndHasAccessWithoutDuplicates = clientProblemSpecificationGenerator.byFilterAndHasAccessWithoutDuplicates(permissionFilter, filter);
        return clientProblemDao.findAll(byFilterAndHasAccessWithoutDuplicates);
    }

    @Override
    public <P> List<P> find(ClientProblemFilter filter, PermissionFilter permissionFilter, Class<P> projection) {
        var byFilterAndHasAccessWithoutDuplicates = clientProblemSpecificationGenerator.byFilterAndHasAccessWithoutDuplicates(permissionFilter, filter);
        return clientProblemDao.findAll(byFilterAndHasAccessWithoutDuplicates, projection);
    }

    @Override
    public ClientProblem findById(Long medicationId) {
        return clientProblemDao.findById(medicationId).orElseThrow();
    }

    @Override
    public List<ClientProblemCount> countGroupedByStatus(ClientProblemFilter filter, PermissionFilter permissionFilter) {
        var byFilterAndHasAccessWithoutDuplicates = clientProblemSpecificationGenerator.byFilterAndHasAccessWithoutDuplicates(permissionFilter, filter);
        return clientProblemDao.countGroupedByStatus(byFilterAndHasAccessWithoutDuplicates);
    }

    @Override
    public ClientProblemSecurityAwareEntity findSecurityAwareEntity(Long aLong) {
        return clientProblemDao.findById(aLong, ClientProblemSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    public List<ClientProblemSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> longs) {
        return clientProblemDao.findByIdIn(longs, ClientProblemSecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return clientProblemDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientProblemDao.findByIdIn(ids, projection);
    }
}
