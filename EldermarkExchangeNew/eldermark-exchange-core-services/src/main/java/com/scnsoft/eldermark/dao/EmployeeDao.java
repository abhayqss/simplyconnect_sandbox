package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EmployeeDao extends AppJpaRepository<Employee, Long> {

    List<Employee> findByLoginNameAndOrganization_SystemSetup_LoginCompanyIdAndStatusIn(String loginName,
                                                                                        String loginCompanyId, EmployeeStatus... statuses);

    Optional<Employee> findFirstByLoginNameAndOrganization_SystemSetup_LoginCompanyIdAndStatusIn(String loginName, String loginCompanyId, EmployeeStatus... statuses);

    List<Employee> findAllByLoginNameInAndOrganization_SystemSetup_LoginCompanyIdAndStatusIn(Collection<String> loginNames, String loginCompanyId, EmployeeStatus... statuses);

    @Query("select e.organizationId from Employee e where e.id = :id")
    Long findEmployeeOrganizationId(@Param("id") Long id);

    @Query("select e.communityId from Employee e where e.id = :id")
    Long findEmployeeCommunityId(@Param("id") Long id);

    boolean existsByLoginNameAndOrganizationIdAndStatusNot(String loginName, Long organizationId, EmployeeStatus status);

    List<Employee> findByCommunityIdAndStatusAndCareTeamRoleCodeIn(Long communityId, EmployeeStatus status, Set<CareTeamRoleCode> roleCodes);

    List<Employee> findByOrganizationIdAndStatusAndCareTeamRoleCodeIn(Long communityId, EmployeeStatus status, Set<CareTeamRoleCode> roleCodes);

    boolean existsByIdAndAssociatedClientsId(Long id, Long associatedClientsId);

    boolean existsByIdAndAssociatedClientsCommunityId(Long id, Long communityId);

    @Modifying
    @Query("update Employee set status = :status where id = :id")
    void updateStatus(@Param("status") EmployeeStatus status, @Param("id") Long id);

    @Modifying
    @Query("update Employee set status = :status, isAutoStatusChanged = :isAutoStatusChanged, deactivateDatetime = :deactivateDatetime where id in :ids")
    void updateStatus(@Param("status") EmployeeStatus status, @Param("ids") Iterable<Long> ids, @Param("isAutoStatusChanged") boolean isAutoStatusChanged, @Param("deactivateDatetime")Instant deactivateDatetime);

    @Modifying
    @Query("UPDATE Employee set twilioUserSid = :twilioUserSid where id = :id")
    void updateTwilioUserSid(@Param("id") Long id, @Param("twilioUserSid") String twilioUserSid);

    @Modifying
    @Query("UPDATE Employee set twilioServiceConversationSid = :twilioServiceConversationSid where id = :id")
    void updateTwilioServiceConversationSid(@Param("id") Long id, @Param("twilioServiceConversationSid") String twilioServiceConversationSid);

}
