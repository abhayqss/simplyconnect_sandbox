package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.model.v251.segment.PR1;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0230ProcedureFunctionalType;
import com.scnsoft.eldermark.entity.xds.segment.PR1ProceduresSegment;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.Pr1SegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class Pr1SegmentParserImpl extends AbstractAdtSegmentParser<PR1ProceduresSegment, PR1>
        implements Pr1SegmentParser {

    @Override
    public PR1ProceduresSegment doParse(PR1 segment, MessageSource messageSource) {
        final PR1ProceduresSegment pr1 = new PR1ProceduresSegment();
        pr1.setSetId(segment.getSetIDPR1().getValue());
        pr1.setProcedureCodingMethod(dataTypeService.getValue(segment.getPr12_ProcedureCodingMethod()));
        pr1.setProcedureCode(dataTypeService.createCE(segment.getPr13_ProcedureCode()));
        pr1.setProcedureDescription(dataTypeService.getValue(segment.getPr14_ProcedureDescription()));
        pr1.setProcedureDatetime(dataTypeService.convertTS(segment.getPr15_ProcedureDateTime()));
        pr1.setProcedureFunctionalType(dataTypeService.createIS(segment.getPr16_ProcedureFunctionalType(), HL7CodeTable0230ProcedureFunctionalType.class));
        pr1.setAssociatedDiagnosisCode(dataTypeService.createCE(segment.getPr115_AssociatedDiagnosisCode()));
        return pr1;
    }
}
