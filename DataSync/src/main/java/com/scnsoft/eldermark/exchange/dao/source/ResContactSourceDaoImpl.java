package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResContactData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("resContactSourceDao")
public class ResContactSourceDaoImpl extends StandardSourceDaoImpl<ResContactData, Long> {
    public ResContactSourceDaoImpl() {
        super(ResContactData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
