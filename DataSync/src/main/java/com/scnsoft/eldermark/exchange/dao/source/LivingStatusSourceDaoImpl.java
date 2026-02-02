package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.LivingStatusData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("livingStatusSourceDao")
public class LivingStatusSourceDaoImpl extends StandardSourceDaoImpl<LivingStatusData, Long> {
    public LivingStatusSourceDaoImpl() {
        super(LivingStatusData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
