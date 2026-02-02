package com.scnsoft.eldermark.facades;

import java.util.Date;

public interface AdtToCcdDataConversionFacade {

    void convertAndSave(Long residentId, Long adtMessageId, Date eventDate);
}
