package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface MedicalProfessionalIdResolver {
    long getId(long legacyId, DatabaseInfo database);
}
