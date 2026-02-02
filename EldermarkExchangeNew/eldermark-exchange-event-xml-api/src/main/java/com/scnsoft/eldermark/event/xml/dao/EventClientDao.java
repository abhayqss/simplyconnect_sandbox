package com.scnsoft.eldermark.event.xml.dao;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.CustomClientDao;
import com.scnsoft.eldermark.entity.Client;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventClientDao extends ClientDao, CustomClientDao {

    Optional<Client> findByLegacyIdAndOrganizationId(String clientLegacyId, Long organizationId);

    Optional<Client> findByLegacyIdAndCommunityId(String clientLegacyId, Long communityId);
}
