package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@ConditionalOnProperty(
        value = "healthPartners.integration.enabled",
        havingValue = "true"
)
public class HpFileNameSupportImpl implements HpFileNameSupport {

    private final Map<HpFileType, String> testNamesPrefixCache;

    private final HpAcknowledgeReportNameGenerator hpAcknowledgeReportNameGenerator;

    @Autowired
    public HpFileNameSupportImpl(HpTestFileNameGenerator hpTestFileNameGenerator,
                                 HpAcknowledgeReportNameGenerator hpAcknowledgeReportNameGenerator) {
        testNamesPrefixCache = Stream.of(HpFileType.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        hpTestFileNameGenerator::generatePrefix
                ));
        this.hpAcknowledgeReportNameGenerator = hpAcknowledgeReportNameGenerator;
    }

    @Override
    public boolean isHealthPartnersSftpInputFile(String fileName) {
        if (fileName == null) {
            return false;
        }
        return !hpAcknowledgeReportNameGenerator.isReportFile(fileName) && startsWithHpFileType(fileName);
    }

    private boolean startsWithHpFileType(String fileName) {
        return Stream.of(HpFileType.values()).anyMatch(t -> fileName.startsWith(t.name()));
    }

    @Override
    public Optional<HpFileType> typeFromFileName(String fileName) {
        if (fileName == null) {
            return Optional.empty();
        }
        return Stream.of(HpFileType.values())
                .filter(t -> containsTypeName(t, fileName))
                .findFirst();
    }

    private boolean containsTypeName(HpFileType type, String fileName) {
        return fileName.toLowerCase().contains(type.name().toLowerCase());
    }

    @Override
    public Optional<HpFileDescription> describe(String fileName) {
        return typeFromFileName(fileName)
                .map(type -> {
                    HpFileSource source;
                    if (fileName.startsWith(testNamesPrefixCache.get(type))) {
                        source = HpFileSource.TESTING;
                    } else {
                        source = HpFileSource.SFTP;
                    }

                    return new HpFileDescription(type, source);
                });
    }

}
