package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;

import java.util.Optional;

public interface HpFileNameSupport {

    boolean isHealthPartnersSftpInputFile(String fileName);

    Optional<HpFileType> typeFromFileName(String fileName);

    Optional<HpFileDescription> describe(String fileName);
}
