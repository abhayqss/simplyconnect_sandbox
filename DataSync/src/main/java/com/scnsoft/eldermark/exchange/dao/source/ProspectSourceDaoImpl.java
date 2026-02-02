package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ProspectData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("prospectSourceDao")
public class ProspectSourceDaoImpl extends StandardSourceDaoImpl<ProspectData, Long> {
    public ProspectSourceDaoImpl() {
        super(ProspectData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
