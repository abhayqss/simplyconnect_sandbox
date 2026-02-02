package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.MedicationTreatmentSetupData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository(value = "medicationTreatmentSetupSourceDao")
public class MedicationTreatmentSetupSourceDaoImpl extends StandardSourceDaoImpl<MedicationTreatmentSetupData, Long> {

	protected MedicationTreatmentSetupSourceDaoImpl() {
		super(MedicationTreatmentSetupData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}
	
}
