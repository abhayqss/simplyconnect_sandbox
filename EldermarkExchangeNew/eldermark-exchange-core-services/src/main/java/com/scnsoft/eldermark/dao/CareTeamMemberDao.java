package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;

public interface CareTeamMemberDao extends AppJpaRepository<CareTeamMember, Long> {

    Employee findEmployeeById(Long id);
}
