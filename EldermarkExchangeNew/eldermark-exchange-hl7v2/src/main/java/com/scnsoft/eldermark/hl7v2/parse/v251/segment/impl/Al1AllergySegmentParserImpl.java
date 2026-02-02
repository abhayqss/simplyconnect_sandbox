package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.model.v251.segment.AL1;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0127AllergenType;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0128AllergySeverity;
import com.scnsoft.eldermark.entity.xds.segment.AdtAL1AllergySegment;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.Al1AllergySegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class Al1AllergySegmentParserImpl extends AbstractAdtSegmentParser<AdtAL1AllergySegment, AL1> implements Al1AllergySegmentParser {

    @Override
    protected AdtAL1AllergySegment doParse(final AL1 segment, MessageSource messageSource) {
        final AdtAL1AllergySegment result = new AdtAL1AllergySegment();
        result.setSetId(segment.getSetIDAL1().getValue());

        result.setAllergenType(dataTypeService.createCEWithCodedValues(segment.getAl12_AllergenTypeCode(), HL7CodeTable0127AllergenType.class));

        result.setAllergyCode(dataTypeService.createCE(segment.getAl13_AllergenCodeMnemonicDescription()));

        //todo this is CE now
        result.setAllergySeverity(dataTypeService.createISFromCE(segment.getAl14_AllergySeverityCode(), HL7CodeTable0128AllergySeverity.class));

        result.setAllergyReactions(dataTypeService.createStringList(segment.getAl15_AllergyReactionCode()));
        result.setIdentificationDate(dataTypeService.convertDtToInstant(segment.getAl16_IdentificationDate()));
        return result;
    }
}
