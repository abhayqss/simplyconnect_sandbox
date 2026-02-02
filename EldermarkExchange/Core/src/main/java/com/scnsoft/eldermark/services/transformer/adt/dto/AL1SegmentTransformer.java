package com.scnsoft.eldermark.services.transformer.adt.dto;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.segment.AdtAL1AllergySegment;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.AL1AllergySegmentDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AL1SegmentTransformer extends ListAndItemTransformer<AdtAL1AllergySegment, AL1AllergySegmentDto> {

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementTransformer;

    @Autowired
    private Converter<CECodedElement, String> ceCodedElementStringConverter;
    @Autowired
    private Converter<ISCodedValueForUserDefinedTables<? extends HL7UserDefinedCodeTable>, String> isCodedValueForUserDefinedTablesStringConverter;

    @Override
    public AL1AllergySegmentDto convert(AdtAL1AllergySegment adtAL1AllergySegment) {
        if (adtAL1AllergySegment == null) {
            return null;
        }
        AL1AllergySegmentDto target = new AL1AllergySegmentDto();
        target.setSetId(adtAL1AllergySegment.getSetId());
        target.setAllergyType(ceCodedElementStringConverter.convert(adtAL1AllergySegment.getAllergenType()));
        target.setAllergyCode(ceCodedElementTransformer.convert(adtAL1AllergySegment.getAllergyCode()));
        target.setAllergySeverity(isCodedValueForUserDefinedTablesStringConverter.convert(adtAL1AllergySegment.getAllergySeverity()));
        target.setAllergyReactionList(new ArrayList<>(adtAL1AllergySegment.getAllergyReactionList()));
        return target;
    }
}
