package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.LoaReasonData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository("loaReasonSourceDao")
public class LoaReasonSourceDaoImpl extends StandardSourceDaoImpl<LoaReasonData, Long> {

	public LoaReasonSourceDaoImpl() {
        super(LoaReasonData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
	
}
