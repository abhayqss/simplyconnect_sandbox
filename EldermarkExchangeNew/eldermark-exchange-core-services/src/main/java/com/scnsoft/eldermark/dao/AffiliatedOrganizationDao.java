package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AffiliatedOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffiliatedOrganizationDao extends JpaRepository<AffiliatedOrganization, Long>, JpaSpecificationExecutor<AffiliatedOrganization> {

    List<AffiliatedOrganization> getAllByPrimaryOrganizationId(Long organizationId);

    List<AffiliatedOrganization> getAllByAffiliatedOrganizationId(Long organizationId);

    void deleteAllByPrimaryOrganizationId(Long organizationId);
}
