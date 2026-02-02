package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.EventTypeCareTeamRoleXref;

import java.util.List;

/**
 * Created by pzhurba on 24-Sep-15.
 */
public interface EventTypeCareTeamRoleXrefDao extends BaseDao<EventTypeCareTeamRoleXref> {
    List<EventTypeCareTeamRoleXref> getResponsibilityForRole(Long roleId);

    EventTypeCareTeamRoleXref get(long eventTypeId, long roleId);

}
