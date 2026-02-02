package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface MedProviderIdResolver {

	long getId(long legacyId, DatabaseInfo database);
	
}
