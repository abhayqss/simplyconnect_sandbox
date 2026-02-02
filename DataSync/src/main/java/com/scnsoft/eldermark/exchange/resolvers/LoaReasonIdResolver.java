package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface LoaReasonIdResolver {
	
	long getId(long legacyId, DatabaseInfo database);

}
