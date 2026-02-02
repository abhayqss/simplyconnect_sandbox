package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.AllergyData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(value = "allergySourceDao")
public class AllergySourceDaoImpl extends StandardSourceDaoImpl<AllergyData, Long> {
    public AllergySourceDaoImpl() {
        super(AllergyData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
