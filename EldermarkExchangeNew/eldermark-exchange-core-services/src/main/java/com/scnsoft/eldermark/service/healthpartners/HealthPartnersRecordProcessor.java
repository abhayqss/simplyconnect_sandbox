package com.scnsoft.eldermark.service.healthpartners;

import com.scnsoft.eldermark.entity.healthpartner.BaseHealthPartnersRecord;

public interface HealthPartnersRecordProcessor<T extends BaseHealthPartnersRecord> {

    void process(T record, Long communityId);

}
