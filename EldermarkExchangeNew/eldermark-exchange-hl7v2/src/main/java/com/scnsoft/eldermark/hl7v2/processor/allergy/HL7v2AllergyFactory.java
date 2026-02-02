package com.scnsoft.eldermark.hl7v2.processor.allergy;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.segment.AdtAL1AllergySegment;

import java.util.Optional;

interface HL7v2AllergyFactory {

    Optional<Allergy> createAllergy(Client client, AdtAL1AllergySegment al1, AdtMessage adtMessage);

}
