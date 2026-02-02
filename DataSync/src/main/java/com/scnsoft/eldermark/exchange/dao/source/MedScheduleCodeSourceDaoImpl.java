package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.MedScheduleCodeData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "medScheduleCodeSourceDao")
public class MedScheduleCodeSourceDaoImpl extends StandardSourceDaoImpl<MedScheduleCodeData, Long> {

	protected MedScheduleCodeSourceDaoImpl() {
		super(MedScheduleCodeData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
