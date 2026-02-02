package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResLeaveOfAbsenceData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "resLeaveOfAbsenceSourceDao")
public class ResLeaveOfAbsenceSourceDaoImpl extends StandardSourceDaoImpl<ResLeaveOfAbsenceData, Long> {

	protected ResLeaveOfAbsenceSourceDaoImpl() {
		super(ResLeaveOfAbsenceData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
