package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResImmunizationData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("resImmunizationSourceDao")
public class ResImmunizationSourceDaoImpl extends StandardSourceDaoImpl<ResImmunizationData, String> {
    public ResImmunizationSourceDaoImpl() {
        super(ResImmunizationData.class, String.class, Constants.SYNC_STATUS_COLUMN);
    }
}
