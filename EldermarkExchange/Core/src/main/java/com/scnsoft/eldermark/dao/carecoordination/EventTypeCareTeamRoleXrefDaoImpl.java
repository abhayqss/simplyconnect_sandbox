package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.EventTypeCareTeamRoleXref;
import com.scnsoft.eldermark.entity.EventTypeCareTeamRoleXrefPK;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class EventTypeCareTeamRoleXrefDaoImpl extends BaseDaoImpl<EventTypeCareTeamRoleXref> implements EventTypeCareTeamRoleXrefDao {
    public EventTypeCareTeamRoleXrefDaoImpl() {
        super(EventTypeCareTeamRoleXref.class);
    }

    @Override
    public List<EventTypeCareTeamRoleXref> getResponsibilityForRole(final Long roleId) {
        final TypedQuery<EventTypeCareTeamRoleXref> query = entityManager.createQuery("Select o from EventTypeCareTeamRoleXref o WHERE o.id.careTeamRoleId = :roleId ORDER BY o.eventType.description", entityClass);
        query.setParameter("roleId", roleId);
        return query.getResultList();
    }

    @Override
    public EventTypeCareTeamRoleXref get( long eventTypeId, long roleId) {
        return entityManager.find(entityClass, new EventTypeCareTeamRoleXrefPK(eventTypeId, roleId));
    }
}
