package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResIncidentData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "resIncidentSourceDao")
public class ResIncidentSourceDaoImpl extends StandardSourceDaoImpl<ResIncidentData, Long> {

	protected ResIncidentSourceDaoImpl() {
		super(ResIncidentData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
