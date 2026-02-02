package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.OccupancyGoalData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("occupancyGoalSourceDao")
public class OccupancyGoalSourceDaoImpl extends StandardSourceDaoImpl<OccupancyGoalData, Long> {
    public OccupancyGoalSourceDaoImpl() {
        super(OccupancyGoalData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
