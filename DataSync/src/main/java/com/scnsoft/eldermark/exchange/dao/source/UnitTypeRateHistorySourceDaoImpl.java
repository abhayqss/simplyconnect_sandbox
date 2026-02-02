package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.UnitTypesRateHistData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository("unitTypeRateHistorySourceDao")
public class UnitTypeRateHistorySourceDaoImpl extends StandardSourceDaoImpl<UnitTypesRateHistData, Long> {

	protected UnitTypeRateHistorySourceDaoImpl() {
		super(UnitTypesRateHistData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}

}
