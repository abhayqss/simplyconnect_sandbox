package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.EventGroup;
import com.scnsoft.eldermark.entity.EventType;

import java.util.List;

/**
 * Created by pzhurba on 24-Sep-15.
 */
public interface EventTypeDao extends BaseDao<EventType> {
    EventType getByCode(String code);

    List<EventGroup> getGroupList(String order);

    List<EventGroup> getGroupListForView(String order);

    List<EventType> listForView(String orderBy);
}
