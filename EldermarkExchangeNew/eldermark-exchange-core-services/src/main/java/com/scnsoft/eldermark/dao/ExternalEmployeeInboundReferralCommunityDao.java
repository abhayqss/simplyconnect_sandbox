package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.ExternalEmployeeInboundReferralCommunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExternalEmployeeInboundReferralCommunityDao extends JpaRepository<ExternalEmployeeInboundReferralCommunity, Long> {

    Optional<ExternalEmployeeInboundReferralCommunity> findByCommunityId(long communityId);

    List<ExternalEmployeeInboundReferralCommunity> findByEmployeeId(Long employeeId);

    List<ExternalEmployeeInboundReferralCommunity> findAllByCommunityId(long communityId);

    boolean existsByEmployeeIdInAndCommunityId(Collection<Long> employeeIds, Long communityId);
}
