package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResCareHistoryData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("resCareHistorySourceDao")
public class ResCareHistorySourceDaoImpl extends StandardSourceDaoImpl<ResCareHistoryData, Long> {
    public ResCareHistorySourceDaoImpl() {
        super(ResCareHistoryData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
