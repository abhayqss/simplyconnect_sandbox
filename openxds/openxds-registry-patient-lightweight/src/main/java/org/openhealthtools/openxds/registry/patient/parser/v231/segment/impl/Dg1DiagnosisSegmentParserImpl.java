package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v231.segment.DG1;
import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable0052DiagnosisType;
import org.openhealthtools.openxds.entity.segment.AdtDG1DiagnosisSegment;
import org.openhealthtools.openxds.registry.patient.converter.XCNConverter;
import org.openhealthtools.openxds.registry.patient.helpers.Converters;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.Dg1DiagnosisSegmentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class Dg1DiagnosisSegmentParserImpl extends AbstractAdtSegmentParser<AdtDG1DiagnosisSegment, DG1> implements Dg1DiagnosisSegmentParser {

    @Autowired
    private DataTypeService dataTypeService;

    @Autowired
    private XCNConverter xcnConverter;

    @Autowired
    private EmptyHL7Field231Service emptyHL7Field231Service;

    @Override
    public AdtDG1DiagnosisSegment doParse(DG1 segment) throws HL7Exception, ApplicationException {
        final AdtDG1DiagnosisSegment result = new AdtDG1DiagnosisSegment();
        result.setSetId(segment.getSetIDDG1().getValue());
        result.setDiagnosisCodingMethod(segment.getDiagnosisCodingMethod().getValue());
        result.setDiagnosisCode(getDataTypeService().createCE(segment.getDiagnosisCodeDG1()));
        result.setDiagnosisDescription(segment.getDiagnosisDescription().getValue());
        result.setDiagnosisDateTime(getDataTypeService().convertTsToDate(segment.getDiagnosisDateTime()));
        result.setDiagnosisType(getDataTypeService().createIS(segment.getDiagnosisType(), HL7CodeTable0052DiagnosisType.class));
        result.setDiagnosingClinicianList(Converters.convertAll(Arrays.asList(segment.getDiagnosingClinician()), xcnConverter));
        return result;
    }

    @Override
    public boolean isHl7SegmentEmpty(final DG1 hl7Segment) {
        return hl7Segment == null || getEmptyHL7Field231Service().isAbstractPrimitiveEmpty(hl7Segment.getSetIDDG1());
    }

    public DataTypeService getDataTypeService() {
        return dataTypeService;
    }

    public void setDataTypeService(DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }

    public XCNConverter getXcnConverter() {
        return xcnConverter;
    }

    public void setXcnConverter(final XCNConverter xcnConverter) {
        this.xcnConverter = xcnConverter;
    }

    public EmptyHL7Field231Service getEmptyHL7Field231Service() {
        return emptyHL7Field231Service;
    }

    public void setEmptyHL7Field231Service(final EmptyHL7Field231Service emptyHL7Field231Service) {
        this.emptyHL7Field231Service = emptyHL7Field231Service;
    }
}
