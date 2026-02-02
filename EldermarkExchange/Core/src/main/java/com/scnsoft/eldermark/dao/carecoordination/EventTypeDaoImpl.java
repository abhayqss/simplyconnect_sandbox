package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.EventGroup;
import com.scnsoft.eldermark.entity.EventType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class EventTypeDaoImpl extends BaseDaoImpl<EventType> implements EventTypeDao {
    public EventTypeDaoImpl() {
        super(EventType.class);
    }

    @Override
    public EventType getByCode(String code) {
        TypedQuery<EventType> query = entityManager.createQuery("Select o from EventType o where o.code = :code", EventType.class);
        query.setParameter("code", code);
        return query.getSingleResult();
    }

    @Override
    public List<EventGroup> getGroupList(String order) {
        String sql = "Select g from EventGroup g";
        sql = appendSorting(sql, order);

        TypedQuery<EventGroup> query = entityManager.createQuery(sql, EventGroup.class);
        return query.getResultList();
    }

    @Override
    public List<EventGroup> getGroupListForView(String order) {
        String sql = "SELECT g FROM EventGroup g WHERE g.service = 0";
        sql = appendSorting(sql, order);

        TypedQuery<EventGroup> query = entityManager.createQuery(sql, EventGroup.class);
        return query.getResultList();
    }

    @Override
    public List<EventType> listForView(String orderBy) {
        String sql = "SELECT t FROM EventType t WHERE t.service = 0";
        if (StringUtils.isNotBlank(orderBy)) {
            sql += " ODRER BY t." + orderBy + " asc";
        }

        TypedQuery<EventType> query = entityManager.createQuery(sql, EventType.class);
        return query.getResultList();
    }

    private String appendSorting(String sql, String order) {
        if (order != null) {
            if ("priority".equals(order)) {
                sql += " order by g.priority asc";
            } else if ("name".equals(order)) {
                sql += " order by g.name asc";
            }
        }
        return sql;
    }
}
