package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResMedicationData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("resMedicationSourceDao")
public class ResMedicationSourceDaoImpl extends StandardSourceDaoImpl<ResMedicationData, Long> {
    public ResMedicationSourceDaoImpl() {
        super(ResMedicationData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
