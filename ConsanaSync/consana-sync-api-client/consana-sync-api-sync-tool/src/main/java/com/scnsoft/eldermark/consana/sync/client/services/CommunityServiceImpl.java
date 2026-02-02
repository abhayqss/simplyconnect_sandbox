package com.scnsoft.eldermark.consana.sync.client.services;

import com.scnsoft.eldermark.consana.sync.client.dao.OrganizationDao;
import com.scnsoft.eldermark.consana.sync.client.entities.IdAware;
import com.scnsoft.eldermark.consana.sync.client.entities.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {

    private final OrganizationDao organizationDao;

    @Autowired
    public CommunityServiceImpl(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    @Override
    public List<Long> getInitialSyncEnabledCommunityIds() {
        var orgs = organizationDao.findAllByIsConsanaInitialSyncIsTrue();

        return orgs.stream()
                .map(IdAware::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Organization> findAllByIds(Collection<Long> ids) {
        return organizationDao.findAllById(ids);
    }
}
