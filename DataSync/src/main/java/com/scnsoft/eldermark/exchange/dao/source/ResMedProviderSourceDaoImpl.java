package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResMedProviderData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "resMedProviderSourceDao")
public class ResMedProviderSourceDaoImpl extends StandardSourceDaoImpl<ResMedProviderData, Long> {

	protected ResMedProviderSourceDaoImpl() {
		super(ResMedProviderData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}

}
