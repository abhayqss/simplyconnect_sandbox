package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.UnitHistoryData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("unitHistorySourceDao")
public class UnitHistorySourceDaoImpl extends StandardSourceDaoImpl<UnitHistoryData, Long> {
    public UnitHistorySourceDaoImpl() {
        super(UnitHistoryData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
