package com.scnsoft.eldermark.services.transformer.adt.dto;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.DG1DiagnosisSegmentDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DG1SegmentTransformer extends ListAndItemTransformer<AdtDG1DiagnosisSegment, DG1DiagnosisSegmentDto> {

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementTransformer;

    @Autowired
    private Converter<ISCodedValueForUserDefinedTables<? extends HL7UserDefinedCodeTable>, String> isCodedValueForUserDefinedTablesStringConverter;

    @Override
    public DG1DiagnosisSegmentDto convert(AdtDG1DiagnosisSegment adtDG1DiagnosisSegment) {
        if (adtDG1DiagnosisSegment == null) {
            return null;
        }
        DG1DiagnosisSegmentDto target = new DG1DiagnosisSegmentDto();
        target.setSetId(adtDG1DiagnosisSegment.getSetId());
        target.setDiagnosisCodingMethod(adtDG1DiagnosisSegment.getDiagnosisCodingMethod());
        if (adtDG1DiagnosisSegment.getDiagnosisCode() != null) {
            target.setDiagnosisCode(ceCodedElementTransformer.convert(adtDG1DiagnosisSegment.getDiagnosisCode()));
        }
        target.setDiagnosisDescription(adtDG1DiagnosisSegment.getDiagnosisDescription());
        target.setDiagnosisDateTime(adtDG1DiagnosisSegment.getDiagnosisDateTime());
        target.setDiagnosisType(isCodedValueForUserDefinedTablesStringConverter.convert(adtDG1DiagnosisSegment.getDiagnosisType()));
        List<XCNExtendedCompositeIdNumberAndNameForPersons> diagnosisClinicanList = adtDG1DiagnosisSegment.getDiagnosingClinicianList();
        if (CollectionUtils.isNotEmpty(diagnosisClinicanList)) {
            List<String> diagnosisClinicalListDto = new ArrayList<>();
            for (XCNExtendedCompositeIdNumberAndNameForPersons diagnosisClinician : diagnosisClinicanList) {
                diagnosisClinicalListDto.add(CareCoordinationUtils.getFullName(diagnosisClinician.getFirstName(), diagnosisClinician.getLastName()));
            }
            target.setDiagnosingClinicianList(diagnosisClinicalListDto);
        }
        return target;
    }
}
