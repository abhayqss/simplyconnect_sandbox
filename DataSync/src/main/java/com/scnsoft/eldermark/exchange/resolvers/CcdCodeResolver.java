package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.exchange.model.IcdCodeSet;
import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface CcdCodeResolver {

	Long getOrCreateCcdCodeFor(DatabaseInfo database, String codeOid, String displayName, IcdCodeSet codeSet);

}
