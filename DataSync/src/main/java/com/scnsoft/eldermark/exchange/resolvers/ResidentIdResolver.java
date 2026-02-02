package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface ResidentIdResolver {
    long getId(long legacyId, DatabaseInfo database);
}
