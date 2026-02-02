package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResPaySourceHistoryData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("resPaySourceHistorySourceDao")
public class ResPaySourceHistorySourceDaoImpl extends StandardSourceDaoImpl<ResPaySourceHistoryData, Long> {
    public ResPaySourceHistorySourceDaoImpl() {
        super(ResPaySourceHistoryData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
