package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.CareTeamMember;
import com.scnsoft.eldermark.entity.Employee;

/**
 * Created by pzhurba on 24-Sep-15.
 */
public interface CareTeamMemberDao extends BaseDao<CareTeamMember> {
    void deleteCareTeamMembersForEmployee(final Employee employee);
    Long getEmployeeId(Long id);
    Employee getEmployee(Long id);
}
