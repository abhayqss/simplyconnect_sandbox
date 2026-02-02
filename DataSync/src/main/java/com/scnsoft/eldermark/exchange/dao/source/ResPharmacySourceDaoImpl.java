package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResPharmacyData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "resPharmacySourceDao")
public class ResPharmacySourceDaoImpl extends StandardSourceDaoImpl<ResPharmacyData, Long> {

	protected ResPharmacySourceDaoImpl() {
		super(ResPharmacyData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
