package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;

public interface HpTestFileNameGenerator {

    String generatePrefix(HpFileType fileType);

    String generate(HpFileType fileType);

}

