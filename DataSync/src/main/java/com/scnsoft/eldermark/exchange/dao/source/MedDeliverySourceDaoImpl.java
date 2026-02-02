package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.MedDeliveryData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "medDeliverySourceDao")
public class MedDeliverySourceDaoImpl extends StandardSourceDaoImpl<MedDeliveryData, Long> {

	protected MedDeliverySourceDaoImpl() {
		super(MedDeliveryData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
