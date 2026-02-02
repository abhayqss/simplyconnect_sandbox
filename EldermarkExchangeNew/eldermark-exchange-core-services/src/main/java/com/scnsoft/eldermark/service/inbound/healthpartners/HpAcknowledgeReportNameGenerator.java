package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;

public interface HpAcknowledgeReportNameGenerator {

    String generate(HpFileType Type, ProcessingSummary.ProcessingStatus status);

    boolean isReportFile(String fileName);
}
