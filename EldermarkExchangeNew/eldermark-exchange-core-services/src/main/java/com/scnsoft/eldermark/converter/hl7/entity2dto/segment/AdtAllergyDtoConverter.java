package com.scnsoft.eldermark.converter.hl7.entity2dto.segment;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.dto.adt.segment.AdtAllergyDto;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.CodedValueForHL7Table;
import com.scnsoft.eldermark.entity.xds.segment.AdtAL1AllergySegment;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AdtAllergyDtoConverter implements ListAndItemConverter<AdtAL1AllergySegment, AdtAllergyDto> {

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementDtoConverter;

    @Autowired
    private ListAndItemConverter<CECodedElement, String> ceCodedElementToStringConverter;

    @Autowired
    private Converter<CodedValueForHL7Table, String> codedValueStringConverter;

    @Override
    public AdtAllergyDto convert(AdtAL1AllergySegment source) {
        if (source == null) {
            return null;
        }
        var target = new AdtAllergyDto();
        target.setSetId(source.getSetId());
        target.setAllergyType(ceCodedElementToStringConverter.convert(source.getAllergenType()));
        target.setAllergyCode(ceCodedElementDtoConverter.convert(source.getAllergyCode()));
        target.setAllergySeverity(codedValueStringConverter.convert(source.getAllergySeverity()));
        target.setAllergyReactions(new ArrayList<>(source.getAllergyReactions()));
        target.setIdentificationDate(DateTimeUtils.toEpochMilli(source.getIdentificationDate()));
        return target;
    }

}
