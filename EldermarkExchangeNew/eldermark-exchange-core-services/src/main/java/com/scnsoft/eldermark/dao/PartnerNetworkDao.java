package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.network.PartnerNetwork;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerNetworkDao extends AppJpaRepository<PartnerNetwork, Long>, CustomPartnerNetworkDao {
}
