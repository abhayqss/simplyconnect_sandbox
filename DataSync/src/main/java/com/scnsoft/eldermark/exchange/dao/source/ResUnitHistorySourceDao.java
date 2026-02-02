package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResUnitHistoryData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("resUnitHistorySourceDao")
public class ResUnitHistorySourceDao extends StandardSourceDaoImpl<ResUnitHistoryData, Long> {
    public ResUnitHistorySourceDao() {
        super(ResUnitHistoryData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
