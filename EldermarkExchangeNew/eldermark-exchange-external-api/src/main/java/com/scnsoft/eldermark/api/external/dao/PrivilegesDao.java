package com.scnsoft.eldermark.api.external.dao;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.api.external.entity.Privilege;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.community.Community;
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
            "AND (:organizationId IS NULL OR ap.organization.id = :organizationId)))")
    Boolean hasRight(@Param("userAppId") Long userAppId,
                     @Param("privilegeName") Privilege.Name privilegeName,
                     @Param("organizationId") Long organizationId);

    @Query("SELECT CASE WHEN count(p.id) > 0 THEN true ELSE false END " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND (p.name = 'ADMINISTRATIVE' OR " +
            "(p.name = :privilegeNameOrg AND ap.organization.id = (SELECT c.organizationId FROM Community c WHERE c.id = :communityId)) OR " +
            "(p.name = :privilegeNameComm AND ap.community.id = :communityId))")
    Boolean hasRight(@Param("userAppId") Long userAppId,
                     @Param("privilegeNameOrg") Privilege.Name privilegeNameOrg,
                     @Param("privilegeNameComm") Privilege.Name privilegeNameComm,
                     @Param("communityId") Long communityId);

    @Query("SELECT ap.organization.id " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND (p.name = 'ADMINISTRATIVE' OR p.name = :privilegeName) AND ap.organization IS NOT NULL")
    List<Long> listOrganizationIdsByPrivilege(@Param("userAppId") Long userAppId,
                                              @Param("privilegeName") Privilege.Name privilegeName);

    @Query("SELECT ap.organization " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND (p.name = 'ADMINISTRATIVE' OR p.name = :privilegeName) AND ap.organization IS NOT NULL")
    List<Organization> listOrganizationsByPrivilege(@Param("userAppId") Long userAppId,
                                                @Param("privilegeName") Privilege.Name privilegeName);

    @Query("SELECT ap.community " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND (p.name = 'ADMINISTRATIVE' OR p.name = :privilegeName) AND ap.community IS NOT NULL")
    List<Community> listCommunitiesByPrivilege(@Param("userAppId") Long userAppId,
                                               @Param("privilegeName") Privilege.Name privilegeName);


    @Query("SELECT CASE WHEN count(p.id) > 0 THEN true ELSE false END " +
            "FROM Privilege p INNER JOIN p.applicationPrivileges ap " +
            "WHERE ap.application.id = :userAppId AND p.name = :privilegeName")
    Boolean hasUserRight(@Param("userAppId") Long userAppId,
                         @Param("privilegeName") Privilege.Name privilegeName);


}
