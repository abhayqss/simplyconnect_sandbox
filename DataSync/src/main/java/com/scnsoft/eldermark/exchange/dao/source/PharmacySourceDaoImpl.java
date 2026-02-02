package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.PharmacyData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("pharmacySourceDao")
public class PharmacySourceDaoImpl extends StandardSourceDaoImpl<PharmacyData, Long> {
    public PharmacySourceDaoImpl() {
        super(PharmacyData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
