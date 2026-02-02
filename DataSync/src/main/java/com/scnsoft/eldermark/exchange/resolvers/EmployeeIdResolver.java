package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface EmployeeIdResolver {
    long getId(String legacyId, DatabaseInfo database);
}
