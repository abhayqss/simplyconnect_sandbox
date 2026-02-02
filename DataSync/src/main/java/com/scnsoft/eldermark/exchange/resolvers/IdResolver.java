package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface IdResolver <PK, LegacyId> {
    PK getId(LegacyId legacyId, DatabaseInfo database);
}



