package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CommunityCareTeamMemberDao extends AppJpaRepository<CommunityCareTeamMember, Long> {

    boolean existsByEmployee_IdAndCareTeamRole_IdAndCommunity_Id(Long employeeId, Long roleId, Long communityid);

    boolean existsByEmployee_IdAndCareTeamRole_IdAndCommunity_IdAndIdNot(Long employeeId, Long roleId, Long communityid, Long id);

    List<CommunityCareTeamMember> findByEmployeeId(Long employeeId);

    List<CommunityCareTeamMember> findAllByEmployeeInAndCommunityIdIn(Iterable<Employee> employees, Iterable<Long> communityIds);

    List<CommunityCareTeamMember> findAllByCommunityIdAndEmployeeStatus(Long communityId, EmployeeStatus status);

    List<CommunityCareTeamMember> findAllByCommunityIdAndEmployeeId(Long communityId, Long employeeId);

    void deleteAllByIdIn(Collection<Long> ids);
}
