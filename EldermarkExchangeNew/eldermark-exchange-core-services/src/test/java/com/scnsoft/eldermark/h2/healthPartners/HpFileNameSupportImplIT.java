package com.scnsoft.eldermark.h2.healthPartners;

import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.h2.BaseH2IT;
import com.scnsoft.eldermark.service.inbound.healthpartners.HpAcknowledgeReportNameGenerator;
import com.scnsoft.eldermark.service.inbound.healthpartners.HpFileNameSupport;
import com.scnsoft.eldermark.service.inbound.healthpartners.HpFileSource;
import com.scnsoft.eldermark.service.inbound.healthpartners.HpTestFileNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class HpFileNameSupportImplIT extends BaseH2IT {

    @Autowired
    private HpAcknowledgeReportNameGenerator hpAcknowledgeReportNameGenerator;

    @Autowired
    private HpTestFileNameGenerator hpTestFileNameGenerator;

    @Autowired
    private HpFileNameSupport instance;

    @ParameterizedTest
    @EnumSource(HpFileType.class)
    void isHealthPartnersSftpInputFile_validFileName_shouldReturnTrue(HpFileType hpFileType) {
        var filename = generateValidSftpFileName(hpFileType);

        var result = instance.isHealthPartnersSftpInputFile(filename);

        assertTrue(result, () -> "Valid filename " + filename + " is not identified as health partners SFTP input");
    }

    @ParameterizedTest
    @MethodSource("provideReportFileNameParameters")
    void isHealthPartnersSftpInputFile_reportFileName_shouldReturnFalse(HpFileType hpFileType,
                                                                        ProcessingSummary.ProcessingStatus status) {
        var reportFileName = hpAcknowledgeReportNameGenerator.generate(hpFileType, status);

        var result = instance.isHealthPartnersSftpInputFile(reportFileName);

        assertFalse(result, () -> "Report filename " + reportFileName + " is identified as health partners SFTP input");
    }

    private static Stream<Arguments> provideReportFileNameParameters() {
        return Stream.of(HpFileType.values())
                .flatMap(type ->
                        Stream.of(ProcessingSummary.ProcessingStatus.values())
                                .map(status -> Arguments.of(type, status)));
    }

    @Test
    void isHealthPartnersSftpInputFile_invalidFileName_shouldReturnFalse() {
        var invalidFilename = "invalidFileType.txt";

        var result = instance.isHealthPartnersSftpInputFile(invalidFilename);

        assertFalse(result, () -> "Invalid filename " + invalidFilename + " is identified as health partners SFTP input");
    }

    @ParameterizedTest
    @EnumSource(HpFileType.class)
    void typeFromFileName_validType_shouldResolve(HpFileType hpFileType) {
        var filename = generateValidSftpFileName(hpFileType);

        var result = instance.typeFromFileName(filename);

        assertThat(result)
                .withFailMessage("Valid filename %s is not resolved as type %s", filename, hpFileType.name())
                .contains(hpFileType);
    }

    @ParameterizedTest
    @EnumSource(HpFileType.class)
    void describe_validSftpFileName_shouldDescribeAsTypeAndSftp(HpFileType hpFileType) {
        var fileName = generateValidSftpFileName(hpFileType);

        var result = instance.describe(fileName);

        assertTrue(result.isPresent());
        assertEquals(hpFileType, result.get().getType());
        assertEquals(HpFileSource.SFTP, result.get().getSource());
    }

    @ParameterizedTest
    @EnumSource(HpFileType.class)
    void describe_validTestFileName_shouldDescribeAsTypeAndTest(HpFileType hpFileType) {
        var fileName = hpTestFileNameGenerator.generate(hpFileType);

        var result = instance.describe(fileName);

        assertTrue(result.isPresent());
        assertEquals(hpFileType, result.get().getType());
        assertEquals(HpFileSource.TESTING, result.get().getSource());
    }

    @Test
    void describe_invalidFileName_shouldNotDescribe() {
        var invalidFilename = "invalidFileType.txt";

        var result = instance.describe(invalidFilename);

        assertFalse(result.isPresent());
    }

    private String generateValidSftpFileName(HpFileType hpFileType) {
        return hpFileType.name() + "_20210123_142512.txt";
    }
}
