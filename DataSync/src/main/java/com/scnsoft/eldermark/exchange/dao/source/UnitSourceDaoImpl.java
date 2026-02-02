package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.UnitData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("unitSourceDao")
public class UnitSourceDaoImpl extends StandardSourceDaoImpl<UnitData, Long> {
    public UnitSourceDaoImpl() {
        super(UnitData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
