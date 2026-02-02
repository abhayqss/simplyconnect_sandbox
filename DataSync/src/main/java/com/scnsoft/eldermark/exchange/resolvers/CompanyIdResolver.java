package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface CompanyIdResolver {
    long getId(String legacyId, DatabaseInfo database);
}
