package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface GroupIdResolver {
    long getId(Long legacyId, DatabaseInfo database);
}
