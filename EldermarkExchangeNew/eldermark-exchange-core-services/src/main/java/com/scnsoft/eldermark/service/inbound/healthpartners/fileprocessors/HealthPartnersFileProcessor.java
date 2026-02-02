package com.scnsoft.eldermark.service.inbound.healthpartners.fileprocessors;

import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;

import java.io.File;

public interface HealthPartnersFileProcessor {

    HpFileProcessingSummary<?> process(Long fileLogId, File file, Long communityId);

    HpFileType supportedFileType();

}
