package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerNetworkCommunityDao extends AppJpaRepository<PartnerNetworkCommunity, PartnerNetworkCommunity.Id>{
}
