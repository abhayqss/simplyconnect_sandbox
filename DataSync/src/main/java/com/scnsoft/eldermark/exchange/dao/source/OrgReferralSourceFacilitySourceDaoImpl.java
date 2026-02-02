package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.OrgReferralSourceFacilityData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository("orgReferralSourceFacilitySourceDao")
public class OrgReferralSourceFacilitySourceDaoImpl extends StandardSourceDaoImpl<OrgReferralSourceFacilityData, Long> {
	public OrgReferralSourceFacilitySourceDaoImpl() {
        super(OrgReferralSourceFacilityData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
