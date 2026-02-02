package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(value = "residentSourceDao")
public class ResidentSourceDaoImpl extends StandardSourceDaoImpl<ResidentData, Long> {
    public ResidentSourceDaoImpl() {
        super(ResidentData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
