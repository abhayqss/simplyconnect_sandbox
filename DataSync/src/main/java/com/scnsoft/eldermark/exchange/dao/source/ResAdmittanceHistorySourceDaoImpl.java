package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResAdmittanceHistoryData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("resAdmittanceHistorySourceDao")
public class ResAdmittanceHistorySourceDaoImpl extends StandardSourceDaoImpl<ResAdmittanceHistoryData, Long> {
    public ResAdmittanceHistorySourceDaoImpl() {
        super(ResAdmittanceHistoryData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
