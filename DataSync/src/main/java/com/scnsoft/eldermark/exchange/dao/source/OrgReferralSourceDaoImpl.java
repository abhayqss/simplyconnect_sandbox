package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.OrgReferralSourceData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository("orgReferralSourceDao")
public class OrgReferralSourceDaoImpl extends StandardSourceDaoImpl<OrgReferralSourceData, Long>  {

	public OrgReferralSourceDaoImpl() {
        super(OrgReferralSourceData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
	
}
