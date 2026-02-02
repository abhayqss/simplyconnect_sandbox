package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResMedDupData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "resMedDupSourceDao")
public class ResMedDupSourceDaoImpl extends StandardSourceDaoImpl<ResMedDupData, Long> {

	protected ResMedDupSourceDaoImpl() {
		super(ResMedDupData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
