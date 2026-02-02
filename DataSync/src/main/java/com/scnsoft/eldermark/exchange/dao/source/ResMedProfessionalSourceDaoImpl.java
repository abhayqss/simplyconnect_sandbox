package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResMedProfessionalsData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "resMedProfessionalSourceDao")
public class ResMedProfessionalSourceDaoImpl extends StandardSourceDaoImpl<ResMedProfessionalsData, Long> {

	protected ResMedProfessionalSourceDaoImpl() {
		super(ResMedProfessionalsData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
