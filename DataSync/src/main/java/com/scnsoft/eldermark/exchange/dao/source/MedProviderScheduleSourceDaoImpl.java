package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.MedProviderScheduleData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "medProviderScheduleSourceDao")
public class MedProviderScheduleSourceDaoImpl extends StandardSourceDaoImpl<MedProviderScheduleData, Long> {

	protected MedProviderScheduleSourceDaoImpl() {
		super(MedProviderScheduleData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}

}
