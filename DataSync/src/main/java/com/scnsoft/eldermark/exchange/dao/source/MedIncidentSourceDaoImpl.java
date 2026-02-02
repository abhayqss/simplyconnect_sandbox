package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.MedIncidentData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "medIncidentSourceDao")
public class MedIncidentSourceDaoImpl extends StandardSourceDaoImpl<MedIncidentData, Long> {

	protected MedIncidentSourceDaoImpl() {
		super(MedIncidentData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}

}
