package org.openhealthtools.openxds.dao;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ResidentUpdateQueueDaoImpl extends HibernateTemplate implements ResidentUpdateQueueDao {

    private static final String VALUES_FORMAT = "(?, '%s', GETDATE())";
    private static final String INSERT_QUERY_BASE = "INSERT INTO ResidentUpdateQueue (resident_id, update_type, update_time) VALUES ";


    private static final String RESIDENT_UPDATE = INSERT_QUERY_BASE + String.format(VALUES_FORMAT, "RESIDENT");
    private static final String RESIDENT_MERGE = INSERT_QUERY_BASE +
            String.format(VALUES_FORMAT, "RESIDENT_MERGE") + ", " +
            String.format(VALUES_FORMAT, "RESIDENT_MERGE");

    @Override
    public void pushResidentUpdate(Long residentId) {
        push(RESIDENT_UPDATE, residentId);
    }

    @Override
    public void pushResidentMerge(Long residentId1, Long residentId2) {
        push(RESIDENT_MERGE, residentId1, residentId2);
    }

    private void push(String sql, Long... residentIds) {
        final Session session = getSessionFactory().getCurrentSession();
        final SQLQuery query = session.createSQLQuery(sql);
        for (int i = 0; i < residentIds.length; ++i) {
            query.setParameter(i, residentIds[i]);
        }
        query.executeUpdate();
    }
}
