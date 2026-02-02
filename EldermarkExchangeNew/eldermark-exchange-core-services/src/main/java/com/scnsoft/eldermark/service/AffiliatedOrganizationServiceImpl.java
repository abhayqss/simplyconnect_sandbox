package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.AffiliatedOrganizationDao;
import com.scnsoft.eldermark.dao.specification.AffiliatedOrganizationSpecificationGenerator;
import com.scnsoft.eldermark.entity.AffiliatedOrganization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AffiliatedOrganizationServiceImpl implements AffiliatedOrganizationService {

    @Autowired
    private AffiliatedOrganizationDao affiliatedOrganizationDao;

    @Autowired
    private AffiliatedOrganizationSpecificationGenerator affiliatedOrganizationSpecifications;

    @Override
    public List<AffiliatedOrganization> update(List<AffiliatedOrganization> affiliatedOrganizations, Long organizationId) {
        affiliatedOrganizationDao.deleteAllByPrimaryOrganizationId(organizationId);
        return affiliatedOrganizationDao.saveAll(affiliatedOrganizations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AffiliatedOrganization> getAllByPrimaryOrganizationId(Long organizationId) {
        return affiliatedOrganizationDao.getAllByPrimaryOrganizationId(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AffiliatedOrganization> getAllByAffiliatedOrganizationId(Long organizationId) {
        return affiliatedOrganizationDao.getAllByAffiliatedOrganizationId(organizationId);
    }

    @Override
    public List<AffiliatedOrganization> getAllForPrimaryCommunityId(Long communityId) {
        return affiliatedOrganizationDao.findAll(
                affiliatedOrganizationSpecifications.forPrimaryCommunityId(communityId)
        );
    }

    @Override
    public List<AffiliatedOrganization> getAllForAffiliatedCommunityId(Long communityId) {
        return affiliatedOrganizationDao.findAll(
                affiliatedOrganizationSpecifications.forAffiliatedCommunityId(communityId)
        );
    }
}
