package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Created by averazub on 3/21/2016.
 */
@Repository
public interface CareCoordinationCommunityDao extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {

    @Query("select o From Organization o where o.oid=:oid and databaseId=:databaseId")
    List<Organization> searchCommunityByOid(@Param("oid") String oid,@Param("databaseId") Long databaseId);

    @Query("select count(o) FROM Organization o where o.id IN (:communityIds) and (not o.databaseId = :databaseId)")
    long countCommunitiesNotBelongingToOrganization(@Param("communityIds") List<Long> communityIds,@Param("databaseId")  Long databaseId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Organization o SET o.mainLogoPath=:mainLogoPath WHERE o.id=:communityId")
    void updateMainLogoPath(@Param("communityId") Long communityId,@Param("mainLogoPath")  String mainLogoPath);

    @Transactional
    @Query("select o.id from Organization o LEFT JOIN o.database d WHERE d.oid=:orgOid AND o.oid=:communityOid")
    Long getCommunityIdByOrgAndCommunityOid(@Param("orgOid") String orgOid, @Param("communityOid") String communityOid);

    @Query("select o.name From Organization o where o.id=:id")
    String getCommunityName(@Param("id") Long id);

    @Query("select o.id, o.name, ao.affiliatedDatabaseId from Organization o, AffiliatedOrganizations ao where o.id = ao.affiliatedOrganizationId and " +
            "(ao.primaryOrganizationId=:communityId or (ao.primaryDatabaseId = :databaseId and ao.primaryOrganizationId is null))")
    List<Object[]> getAffiliatedOrganizations(@Param("communityId") long communityId, @Param("databaseId") long databaseId);

    @Query("select o.id,o.name,ao.primaryDatabaseId from Organization o, AffiliatedOrganizations ao where o.id = ao.primaryOrganizationId and " +
            "(ao.affiliatedOrganizationId=:communityId or (ao.affiliatedDatabaseId = :databaseId and ao.affiliatedOrganizationId is null))")
    List<Object[]> getInitialOrganizations(@Param("communityId") long communityId, @Param("databaseId") long databaseId);

    @Query("select d.id, d.name from Database d, AffiliatedOrganizations ao where d.id = ao.affiliatedDatabaseId and ao.affiliatedOrganizationId is null and " +
            "(ao.primaryOrganizationId=:communityId or (ao.primaryDatabaseId = :databaseId and ao.primaryOrganizationId is null))")
    List<Object[]> getAffiliatedDatabases(@Param("communityId") long communityId, @Param("databaseId") long databaseId);

    @Query("select d.id, d.name  from Database d, AffiliatedOrganizations ao where d.id = ao.primaryDatabaseId and ao.primaryOrganizationId is null and " +
            "(ao.affiliatedOrganizationId=:communityId or (ao.affiliatedDatabaseId = :databaseId and ao.affiliatedOrganizationId is null))")
    List<Object[]> getInitialDatabases(@Param("communityId") long communityId, @Param("databaseId") long databaseId);

    @Query("select count (*) from AffiliatedOrganizations where primaryOrganizationId = (select facility.id from Resident where id=:residentId) " +
            "or (primaryOrganizationId is null and primaryDatabaseId=(select database.id from Resident where id=:residentId))")
    Long getAffiliatedCommunitiesForResidentCount(@Param("residentId") long residentId);

    @Query("select distinct ao.primaryOrganizationId from AffiliatedOrganizations ao  where ao.primaryDatabaseId = :primaryDatabaseId and " +
            "(ao.affiliatedOrganizationId=:communityId or (ao.affiliatedDatabaseId = :affiliatedDatabaseId and ao.affiliatedOrganizationId is null))")
    List<Long> getInitialOrganizationIds(@Param("communityId") long communityId, @Param("primaryDatabaseId") long primaryDatabaseId, @Param("affiliatedDatabaseId") long affiliatedDatabaseId);

    @Query("select distinct ao.primaryOrganizationId from AffiliatedOrganizations ao where ao.primaryDatabaseId = :primaryDatabaseId and ao.affiliatedDatabaseId = :affiliatedDatabaseId")
    List<Long> getInitialOrganizationIds(@Param("primaryDatabaseId") long primaryDatabaseId, @Param("affiliatedDatabaseId") long affiliatedDatabaseId);

    @Query("select o.databaseId From Organization o where o.id=:id")
    Long getDatabaseId(@Param("id") Long id);

    List<Organization> findByDatabaseId(Long orgId);

    List<Organization> findByDatabaseIdIn(Collection<Long> orgIds);

//    @Query("select count (*) from AffiliatedOrganizations where affiliatedOrganizationId = (select facility.id from Resident where id=:residentId) " +
//            "or (affiliatedOrganizationId is null and affiliatedDatabaseId=(select database.id from Resident where id=:residentId)) " +
//            "and primaryCommunityId = :communityId")
//    boolean isInitialCommunity();
}
