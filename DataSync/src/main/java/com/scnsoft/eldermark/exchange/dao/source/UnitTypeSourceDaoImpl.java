package com.scnsoft.eldermark.exchange.dao.source;


import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.UnitTypeData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("unitTypeSourceDao")
public class UnitTypeSourceDaoImpl extends StandardSourceDaoImpl<UnitTypeData, String> {
    public UnitTypeSourceDaoImpl() {
        super(UnitTypeData.class, String.class, Constants.SYNC_STATUS_COLUMN);
    }
}
