package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.exchange.model.IcdCodeSet;
import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface DiagnosisSetupIdResolver {
	
	long getId(long legacyId, DatabaseInfo database);

	IcdCodeSet getCodeSetFor(String icdCodeValue, DatabaseInfo database);

}
