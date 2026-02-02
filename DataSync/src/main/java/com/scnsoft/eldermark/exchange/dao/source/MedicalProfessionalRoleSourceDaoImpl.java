package com.scnsoft.eldermark.exchange.dao.source;

import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.MedicalProfessionalRoleData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;

@Repository("medicalProfessionalRoleSourceDao")
public class MedicalProfessionalRoleSourceDaoImpl extends StandardSourceDaoImpl<MedicalProfessionalRoleData, Long> {

	protected MedicalProfessionalRoleSourceDaoImpl() {
		super(MedicalProfessionalRoleData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
	}

}
