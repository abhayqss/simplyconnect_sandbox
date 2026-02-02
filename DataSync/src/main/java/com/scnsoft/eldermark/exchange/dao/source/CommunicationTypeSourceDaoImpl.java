package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.CommunicationTypeData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(value = "communicationTypeSourceDao")
public class CommunicationTypeSourceDaoImpl extends StandardSourceDaoImpl<CommunicationTypeData, String> {
    public CommunicationTypeSourceDaoImpl() {
        super(CommunicationTypeData.class, String.class, Constants.SYNC_STATUS_COLUMN);
    }
}
