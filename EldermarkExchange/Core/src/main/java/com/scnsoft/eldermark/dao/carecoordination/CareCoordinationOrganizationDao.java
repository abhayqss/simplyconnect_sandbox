package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.AffiliatedOrganizations;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.DatabaseOrgCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author averazub
 * @author Netkachev
 * Created by averazub on 3/21/2016.
 */
@Repository
public interface CareCoordinationOrganizationDao extends JpaRepository<Database, Long>, JpaSpecificationExecutor<Database> {

    @Query("select d From Database d where d.oid=?1")
    List<Database> searchOrganizationByOid(String oid);

    @Query("select count(d) From SystemSetup d where d.loginCompanyId=?1")
    Long countOrganizationsByLoginCompanyId(String loginCompanyId);
//
    @Query("select d.name From Database d where d.id=:id")
    String getOrganizationName(@Param("id") Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Database d SET d.mainLogoPath=:mainLogoPath WHERE d.id=:organizationId")
    void updateMainLogoPath(@Param("organizationId") Long organizationId,@Param("mainLogoPath")  String mainLogoPath);

    @Query("from DatabaseOrgCountEntity where databaseId=:databaseId")
    DatabaseOrgCountEntity getDatabaseOrgCount(@Param("databaseId") long databaseId);

    @Transactional
    @Modifying
    @Query("delete from AffiliatedOrganizations where primaryDatabaseId=:databaseId")
    void clearAffiliatedDetails(@Param("databaseId") long databaseId);

    @Query("from AffiliatedOrganizations where primaryDatabaseId=:databaseId")
    List<AffiliatedOrganizations> getAffiliatedOrganizations(@Param("databaseId") long databaseId);

//    @Query("select distinct affiliated_database_id from AffiliatedOrganizations where primaryDatabaseId=:databaseId")
//    List<Long> getAffiliatedDatabaseIds(@Param("databaseId") long databaseId);
//
//    @Query("select distinct affiliated_organization_id from AffiliatedOrganizations where primaryDatabaseId=:databaseId")
//    List<Long> getAffiliatedOrganizationIds(@Param("databaseId") long databaseId);

//    @Query("select distinct affiliated_organization_id from AffiliatedOrganizations where primaryDatabaseId=:databaseId")
//    List<Long> getAffiliatedOrganizationIds(@Param("databaseId") long databaseId);

//    @Query("count (*) from AffiliatedOrganizations where primaryDatabaseId=:databaseId")
//    Long getAffiliatedOrganizationsCount(@Param("databaseId") long databaseId);
}
