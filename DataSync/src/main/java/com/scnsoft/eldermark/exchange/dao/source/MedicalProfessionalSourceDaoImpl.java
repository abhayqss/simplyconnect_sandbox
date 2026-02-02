package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.MedicalProfessionalData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("medicalProfessionalSourceDao")
public class MedicalProfessionalSourceDaoImpl extends StandardSourceDaoImpl<MedicalProfessionalData, Long> {
    public MedicalProfessionalSourceDaoImpl() {
        super(MedicalProfessionalData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
