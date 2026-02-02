package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.FuneralHomeData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "funeralHomeSourceDao")
public class FuneralHomeSourceDaoImpl extends StandardSourceDaoImpl<FuneralHomeData, Long>  {

	protected FuneralHomeSourceDaoImpl() {
		super(FuneralHomeData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}

}
