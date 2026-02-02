package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface UnitTypeIdResolver {
    long getId(String legacyId, DatabaseInfo database);
}
