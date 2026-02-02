package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.EventsLogEntity;
import com.scnsoft.eldermark.entity.EventsProvider;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class EventsLogDaoImpl extends BaseDaoImpl<EventsLogEntity> implements EventsLogDao {
    public EventsLogDaoImpl() {
        super(EventsLogEntity.class);
    }


}
