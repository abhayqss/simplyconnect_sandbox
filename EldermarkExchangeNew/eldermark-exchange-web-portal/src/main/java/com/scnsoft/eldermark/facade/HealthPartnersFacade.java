package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.healthpartners.HealthPartnersTestOutcome;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;

public interface HealthPartnersFacade {

    HealthPartnersTestOutcome submitTestCSV(String csv, HpFileType fileType);
}
