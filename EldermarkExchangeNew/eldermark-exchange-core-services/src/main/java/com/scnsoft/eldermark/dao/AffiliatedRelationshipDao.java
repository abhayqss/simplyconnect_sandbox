package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.AffiliatedRelationship;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AffiliatedRelationshipDao extends AppJpaRepository<AffiliatedRelationship, Long> {

    boolean existsByAffiliatedOrganizationIdInAndPrimaryOrganizationId(Iterable<Long> affiliatedOrganizationIds, Long primaryOrganizationId);

    boolean existsByAffiliatedOrganizationIdInAndPrimaryCommunityId(Iterable<Long> affiliatedOrganizationIds, Long primaryCommunityId);

    boolean existsByPrimaryOrganizationIdInAndAffiliatedCommunityId(Iterable<Long> primaryOrganizationIds, Long affiliatedCommunityId);

    boolean existsByAffiliatedOrganizationIdInAndPrimaryCommunityIdIn(Iterable<Long> affiliatedOrganizationIds, Iterable<Long> primaryCommunityIds);

    boolean existsByAffiliatedOrganizationIdAndPrimaryCommunityIdIn(Long affiliatedOrganizationId, Iterable<Long> primaryCommunityIds);

    boolean existsByAffiliatedCommunityIdInAndPrimaryOrganizationId(Iterable<Long> affiliatedCommunityIds, Long primaryOrganizationId);

    @Query("SELECT ar.primaryCommunityId from AffiliatedRelationship ar " +
            "   where ar.affiliatedCommunityId = :affiliatedCommunityId and " +
            "         ar.primaryCommunityId in (:amongPrimaryCommunityIds)")
    List<Long> findPrimaryCommunityIdsForAffiliated(@Param("affiliatedCommunityId") Long affiliatedCommunityId,
                                                   @Param("amongPrimaryCommunityIds") Collection<Long> amongPrimaryCommunityIds);

    @Query("SELECT ar.primaryCommunityId from AffiliatedRelationship ar " +
            "   where ar.affiliatedOrganizationId = :affiliatedOrganizationId and " +
            "         ar.primaryCommunityId in (:amongPrimaryCommunityIds)")
    List<Long> findPrimaryCommunityIdsForAffiliatedOrganization(@Param("affiliatedOrganizationId") Long affiliatedOrganizationId,
                                                    @Param("amongPrimaryCommunityIds") Collection<Long> amongPrimaryCommunityIds);

    @Query("SELECT distinct ar.primaryCommunityId from AffiliatedRelationship ar " +
            "   where ar.affiliatedOrganizationId in (:affiliatedOrganizationIds)")
    List<Long> findPrimaryCommunityIdsForAffiliatedOrganizations(@Param("affiliatedOrganizationIds") Collection<Long> affiliatedOrganizationIds);

    boolean existsByPrimaryCommunityIdInAndAffiliatedCommunityId(Iterable<Long> primaryCommunityIds, Long affiliatedCommunityId);

    boolean existsByAffiliatedCommunityIdInAndPrimaryCommunityId(Iterable<Long> affiliatedCommunityIds, Long primaryCommunityId);

    boolean existsByAffiliatedCommunityIdInAndPrimaryCommunityIdIn(Iterable<Long> affiliatedCommunityIds, Iterable<Long> primaryCommunityIds);

    boolean existsByAffiliatedCommunityIdAndPrimaryCommunityIdIn(Long affiliatedCommunityId, Iterable<Long> primaryCommunityIds);

    boolean existsByPrimaryCommunityIdIn(Collection<Long> primaryCommunityIds);
}
