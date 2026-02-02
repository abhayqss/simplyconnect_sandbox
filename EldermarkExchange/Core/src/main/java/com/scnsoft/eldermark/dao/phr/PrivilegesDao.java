package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.CareTeamRole;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.phr.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author phomal
 * Created on 11/1/2017.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY, readOnly = true)
public interface PrivilegesDao extends JpaRepository<Privilege, Long> {

    @Query("SELECT CASE WHEN count(p.id) > 0 THEN true ELSE false END FROM Privilege p INNER JOIN p.roles r " +
            "WHERE p.name = :privilegeName AND r.ctmRole IS NULL " +
            "AND r.role IN (SELECT e.careTeamRole FROM Employee e WHERE e.id = :employeeId)")
    Boolean hasRight(@Param("employeeId") Long employeeId, @Param("privilegeName") Privilege.Name privilegeName);

    @Query("SELECT CASE WHEN count(p.id) > 0 THEN true ELSE false END FROM Privilege p INNER JOIN p.roles r " +
            "WHERE p.name = :privilegeName AND r.ctmRole = :ctmRole " +
            "AND r.role IN (SELECT e.careTeamRole FROM Employee e WHERE e.id = :employeeId)")
    Boolean hasRight(@Param("employeeId") Long employeeId,
                     @Param("privilegeName") Privilege.Name privilegeName,
                     @Param("ctmRole") CareTeamRole careTeamMemberRole);

    @Query("SELECT CASE WHEN count(p.id) > 0 THEN true ELSE false END " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND (p.name = 'ADMINISTRATIVE' OR (p.name = :privilegeName " +
            "AND (:databaseId IS NULL OR ap.database.id = :databaseId)))")
    Boolean hasRight(@Param("userAppId") Long userAppId,
                     @Param("privilegeName") Privilege.Name privilegeName,
                     @Param("databaseId") Long databaseId);

    @Query("SELECT CASE WHEN count(p.id) > 0 THEN true ELSE false END " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND (p.name = 'ADMINISTRATIVE' OR " +
            "(p.name = :privilegeNameDb AND ap.database.id = (SELECT o.databaseId FROM Organization o WHERE o.id = :organizationId)) OR " +
            "(p.name = :privilegeNameOrg AND ap.organization.id = :organizationId))")
    Boolean hasRight(@Param("userAppId") Long userAppId,
                     @Param("privilegeNameDb") Privilege.Name privilegeName,
                     @Param("privilegeNameOrg") Privilege.Name privilegeName2,
                     @Param("organizationId") Long organizationId);

    @Query("SELECT ap.database.id " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND (p.name = 'ADMINISTRATIVE' OR p.name = :privilegeName) AND ap.database IS NOT NULL")
    List<Long> listOrganizationIdsByPrivilege(@Param("userAppId") Long userAppId,
                                              @Param("privilegeName") Privilege.Name privilegeName);

    @Query("SELECT ap.database " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND (p.name = 'ADMINISTRATIVE' OR p.name = :privilegeName) AND ap.database IS NOT NULL")
    List<Database> listOrganizationsByPrivilege(@Param("userAppId") Long userAppId,
                                                @Param("privilegeName") Privilege.Name privilegeName);

    @Query("SELECT ap.organization " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND (p.name = 'ADMINISTRATIVE' OR p.name = :privilegeName) AND ap.organization IS NOT NULL")
    List<Organization> listCommunitiesByPrivilege(@Param("userAppId") Long userAppId,
                                                  @Param("privilegeName") Privilege.Name privilegeName);

    @Query("SELECT CASE WHEN count(p.id) > 0 THEN true ELSE false END " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND p.name = :privilegeName")
    Boolean hasUserRight(@Param("userAppId") Long userAppId,
                         @Param("privilegeName") Privilege.Name privilegeName);

}
