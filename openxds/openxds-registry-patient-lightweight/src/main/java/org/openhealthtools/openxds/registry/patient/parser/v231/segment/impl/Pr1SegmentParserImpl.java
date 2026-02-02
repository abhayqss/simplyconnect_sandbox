package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.model.v231.segment.PR1;
import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable0230ProcedureFunctionalType;
import org.openhealthtools.openxds.entity.segment.PR1ProceduresSegment;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.Pr1SegmentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Pr1SegmentParserImpl extends AbstractAdtSegmentParser<PR1ProceduresSegment, PR1>
        implements Pr1SegmentParser {

    @Autowired
    private DataTypeService dataTypeService;

    @Autowired
    private EmptyHL7Field231Service emptyHL7Field231Service;

    @Override
    public PR1ProceduresSegment doParse(PR1 segment) {
        final PR1ProceduresSegment pr1 = new PR1ProceduresSegment();
        pr1.setSetId(segment.getSetIDPR1().getValue());
        pr1.setProcedureCodingMethod(dataTypeService.getValue(segment.getProcedureCodingMethod()));
        pr1.setProcedureCode(dataTypeService.createCE(segment.getProcedureCode()));
        pr1.setProcedureDescription(dataTypeService.getValue(segment.getProcedureDescription()));
        pr1.setProcedureDatetime(dataTypeService.convertTsToDate(segment.getProcedureDateTime()));
        pr1.setProcedureFunctionalType(dataTypeService.createIS(segment.getProcedureFunctionalType(), HL7CodeTable0230ProcedureFunctionalType.class));
        pr1.setAssociatedDiagnosisCode(dataTypeService.createCE(segment.getAssociatedDiagnosisCode()));
        return pr1;
    }

    @Override
    public boolean isHl7SegmentEmpty(final PR1 hl7Segment) {
        return hl7Segment == null || emptyHL7Field231Service.isAbstractPrimitiveEmpty(hl7Segment.getSetIDPR1());
    }
}
