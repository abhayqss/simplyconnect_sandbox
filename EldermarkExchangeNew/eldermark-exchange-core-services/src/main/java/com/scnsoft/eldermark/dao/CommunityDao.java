package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.projection.OidAware;
import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.community.Community;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityDao extends AppJpaRepository<Community, Long>, CustomCommunityDao {

    List<Community> findByOrganizationId(Long organizationId);

    Community findFirstByOrganizationId(Long organizationId);

    List<Community> findByOrganizationIdIn(Collection<Long> organizationId);

    Community findByOrganizationIdAndOid(Long organizationId, String oid);

    Community findByOrganizationIdAndName(Long organizationId, String name);

    Community findFirstByOrganizationIdAndXdsDefaultTrue(Long organizationId);

    OidAware findFirstByOrganizationOidAndXdsDefaultTrue(String organizationOid);

    Boolean existsByOrganizationIdAndOid(Long organizationId, String oid);

    Boolean existsByOrganizationIdAndOidAndIdNot(Long organizationId, String oid, Long id);

    Boolean existsByOrganizationIdAndName(Long organizationId, String name);

    Boolean existsByOrganizationIdAndNameAndIdNot(Long organizationId, String name, Long id);

    <T> Optional<T> findByOrganization_AlternativeIdAndOid(String orgAlternativeId, String oid, Class<T> projection);

    @Modifying
    @Query("update Community set pccFacilityCountry = :country, pccFacilityTimezone=:timezone where id = :id")
    void updatePccFields(@Param("id") Long id, @Param("country") String pccCountry,@Param("timezone") String pccFacTimezone);
}
