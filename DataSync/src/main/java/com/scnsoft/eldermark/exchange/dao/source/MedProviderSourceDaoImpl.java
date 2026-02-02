package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.MedProviderData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "medProviderSourceDao")
public class MedProviderSourceDaoImpl extends StandardSourceDaoImpl<MedProviderData, Long> {

	protected MedProviderSourceDaoImpl() {
		super(MedProviderData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
