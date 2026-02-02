package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.service.inbound.healthpartners.fileprocessors.BaseHpFileProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Service
@ConditionalOnProperty(
        value = "healthPartners.integration.enabled",
        havingValue = "true"
)
public class HpAcknowledgeReportNameGeneratorImpl implements HpAcknowledgeReportNameGenerator {
    private static final DateTimeFormatter FILE_NAME_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(BaseHpFileProcessor.CT_ZONE);

    @Override
    public String generate(HpFileType type, ProcessingSummary.ProcessingStatus status) {
        return type.name().toLowerCase() + "_" + status.name().toLowerCase() + "_report_" +
                FILE_NAME_DATE_TIME_FORMATTER.format(Instant.now()) + ".json";
    }

    @Override
    public boolean isReportFile(String fileName) {
        return fileName.toLowerCase().contains("report");
    }
}
