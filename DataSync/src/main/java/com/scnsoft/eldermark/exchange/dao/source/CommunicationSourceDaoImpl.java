package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.CommunicationData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("communicationSourceDao")
public class CommunicationSourceDaoImpl extends StandardSourceDaoImpl<CommunicationData, Long> {
    public CommunicationSourceDaoImpl() {
        super(CommunicationData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
