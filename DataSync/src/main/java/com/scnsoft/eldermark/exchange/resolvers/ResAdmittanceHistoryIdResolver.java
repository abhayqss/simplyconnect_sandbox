package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface ResAdmittanceHistoryIdResolver {
    long getId(long legacyId, DatabaseInfo database);
}
