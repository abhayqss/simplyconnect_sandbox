package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.CareTeamRole;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author averazub
 * @author mradzivonenka
 * @author pzhurba
 *
 * Created on 24-Sep-15.
 */
@Repository
public interface CareTeamRoleDao extends JpaRepository<CareTeamRole, Long>, JpaSpecificationExecutor<CareTeamRole> {

    Sort.Order ORDER_BY_POSITION = new Sort.Order(Sort.Direction.ASC, "position");

    @Query("Select o from CareTeamRole o WHERE name =:name")
    CareTeamRole getByName(@Param("name") String name);

    @Query("Select o from CareTeamRole o WHERE code =:code")
    CareTeamRole getByCode(@Param("code") CareTeamRoleCode code);

    @Query("Select e FROM Employee e JOIN FETCH e.careTeamRole ct WHERE ct.code='ROLE_SUPER_ADMINISTRATOR'")
    List<Employee> getSuperAdministrators();

    @Query("Select e FROM Employee e JOIN FETCH e.careTeamRole ct JOIN FETCH e.database d WHERE ct.code='ROLE_ADMINISTRATOR' and d.id=:databaseId")
    List<Employee> getAdministratorsForCompany(@Param("databaseId") Long databaseId);

    @Query("Select e FROM Employee e JOIN FETCH e.careTeamRole ct JOIN FETCH e.database d WHERE ct.code=:roleCode and d.id=:databaseId and e.id <> :employeeId")
    List<Employee> getEmployeesForCompanyByRoleNotEqualToEmployee(@Param("databaseId") Long databaseId, @Param("roleCode") CareTeamRoleCode roleCode, @Param("employeeId") Long employeeId);

}
