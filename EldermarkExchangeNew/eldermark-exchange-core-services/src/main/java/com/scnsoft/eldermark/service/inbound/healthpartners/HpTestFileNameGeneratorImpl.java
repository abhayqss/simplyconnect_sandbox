package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class HpTestFileNameGeneratorImpl implements HpTestFileNameGenerator {
    private static final DateTimeFormatter FILE_NAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyy_HHmmss").withZone(ZoneId.of("UTC"));


    @Override
    public String generatePrefix(HpFileType fileType) {
        return "TEST_" + fileType.name() + "_";
    }

    @Override
    public String generate(HpFileType fileType) {
        return generatePrefix(fileType) + FILE_NAME_DATE_TIME_FORMATTER.format(Instant.now()) + ".txt";
    }
}
