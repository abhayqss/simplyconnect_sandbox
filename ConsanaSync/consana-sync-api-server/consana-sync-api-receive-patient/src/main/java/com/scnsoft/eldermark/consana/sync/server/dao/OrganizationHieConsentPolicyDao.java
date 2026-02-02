package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.OrganizationHieConsentPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationHieConsentPolicyDao extends JpaRepository<OrganizationHieConsentPolicy, Long> {
    <T> Optional<T> findByCommunityIdAndArchived(Long communityId, Boolean isArchived, Class<T> projectionClass);
}
