package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ProfessionalContactData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("professionalContactSourceDao")
public class ProfessionalContactSourceDaoImpl extends StandardSourceDaoImpl<ProfessionalContactData, Long> {
    public ProfessionalContactSourceDaoImpl() {
        super(ProfessionalContactData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
