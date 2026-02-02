package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface InquiryIdResolver {
    long getId(long legacyId, DatabaseInfo database);
}
