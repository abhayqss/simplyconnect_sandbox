package com.scnsoft.eldermark.converter.hl7.entity2dto.segment;

import java.util.*;
import java.util.stream.Collectors;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.dto.adt.datatype.XCNDto;
import com.scnsoft.eldermark.dto.adt.segment.AdtDiagnosisDto;
import com.scnsoft.eldermark.entity.xds.datatype.CodedValueForHL7Table;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DiagnosisDtoConverter implements ListAndItemConverter<AdtDG1DiagnosisSegment, AdtDiagnosisDto> {

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementDtoConverter;

    @Autowired
    private Converter<CodedValueForHL7Table, String> codedValueStringConverter;

    @Autowired
    private Converter<XCNExtendedCompositeIdNumberAndNameForPersons, XCNDto> xcnConverter;

    @Override
    public AdtDiagnosisDto convert(AdtDG1DiagnosisSegment source) {
        if (source == null) {
            return null;
        }
        var target = new AdtDiagnosisDto();
        target.setSetId(source.getSetId());
        target.setDiagnosisCodingMethod(source.getDiagnosisCodingMethod());
        if (source.getDiagnosisCode() != null) {
            target.setDiagnosisCode(ceCodedElementDtoConverter.convert(source.getDiagnosisCode()));
        }
        target.setDiagnosisDescription(source.getDiagnosisDescription());
        target.setDiagnosisDateTime(DateTimeUtils.toEpochMilli(source.getDiagnosisDateTime()));
        target.setDiagnosisType(codedValueStringConverter.convert(source.getDiagnosisType()));
        List<XCNExtendedCompositeIdNumberAndNameForPersons> diagnosisClinicianList = source.getDiagnosingClinicianList();
        if (CollectionUtils.isNotEmpty(diagnosisClinicianList)) {
            var diagnosisClinicalListDto = diagnosisClinicianList
                    .stream()
                    .map(xcnConverter::convert)
                    .sorted(Comparator.nullsFirst(Comparator.comparing(XCNDto::getLastName).thenComparing(XCNDto::getFirstName)))
                    .collect(Collectors.toList());

            target.setDiagnosingClinicians(diagnosisClinicalListDto);
        }
        return target;
    }

}
