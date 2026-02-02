package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityAddressDao  extends AppJpaRepository<CommunityAddress, Long> {
    
    List<CommunityAddress> findByLocationUpToDateIsNullOrLocationUpToDateIsFalseAndOrganizationId(Long orgId);

    List<CommunityAddress> findByLocationUpToDateIsNullOrLocationUpToDateIsFalse();
}
