package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.MedProviderSchedLogData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "medProviderSchedLogSourceDao")
public class MedProviderSchedLogSourceDaoImpl extends StandardSourceDaoImpl<MedProviderSchedLogData, String> {

	protected MedProviderSchedLogSourceDaoImpl() {
		super(MedProviderSchedLogData.class, String.class, Constants.SYNC_STATUS_COLUMN);
	}

}
