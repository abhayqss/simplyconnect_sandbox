package com.scnsoft.eldermark.services.transformer.adt.toCcd;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import com.scnsoft.eldermark.services.transformer.ResidentAwareConverter;

import java.util.Date;

public interface AdtToCcdDataConverter extends ResidentAwareConverter<AdtMessage, ClinicalDocumentVO> {
    AdtToCcdDataConverter withResident(Resident resident);

    AdtToCcdDataConverter withEventDate(Date eventDate);
}
