package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v251.segment.DG1;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0052DiagnosisType;
import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.Dg1DiagnosisSegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class Dg1DiagnosisSegmentParserImpl extends AbstractAdtSegmentParser<AdtDG1DiagnosisSegment, DG1> implements Dg1DiagnosisSegmentParser {

    @Override
    public AdtDG1DiagnosisSegment doParse(DG1 segment, MessageSource messageSource) throws HL7Exception, ApplicationException {
        final AdtDG1DiagnosisSegment result = new AdtDG1DiagnosisSegment();
        result.setSetId(segment.getDg11_SetIDDG1().getValue());
        result.setDiagnosisCodingMethod(segment.getDg12_DiagnosisCodingMethod().getValue());
        result.setDiagnosisCode(dataTypeService.createCE(segment.getDg13_DiagnosisCodeDG1()));
        result.setDiagnosisDescription(segment.getDg14_DiagnosisDescription().getValue());
        result.setDiagnosisDateTime(dataTypeService.convertTS(segment.getDg15_DiagnosisDateTime()));
        result.setDiagnosisType(dataTypeService.createIS(segment.getDg16_DiagnosisType(), HL7CodeTable0052DiagnosisType.class));
        result.setDiagnosingClinicianList(Stream.of(segment.getDg116_DiagnosingClinician()).map(dataTypeService::createXCN).collect(Collectors.toList()));
        return result;
    }
}
