package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.UnitStationData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "unitStationSourceDao")
public class UnitStationSourceDaoImpl extends StandardSourceDaoImpl<UnitStationData, Long> {

	protected UnitStationSourceDaoImpl() {
		super(UnitStationData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
