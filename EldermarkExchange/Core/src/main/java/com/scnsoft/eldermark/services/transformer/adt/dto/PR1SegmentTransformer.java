package com.scnsoft.eldermark.services.transformer.adt.dto;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.segment.PR1ProceduresSegment;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.PR1ProcedureSegmentDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PR1SegmentTransformer extends ListAndItemTransformer<PR1ProceduresSegment, PR1ProcedureSegmentDto> {

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementTransformer;

    @Autowired
    private Converter<ISCodedValueForUserDefinedTables<? extends HL7UserDefinedCodeTable>, String> isCodedValueForUserDefinedTablesStringConverter;

    @Override
    public PR1ProcedureSegmentDto convert(PR1ProceduresSegment pr1ProceduresSegment) {
        if (pr1ProceduresSegment == null) {
            return null;
        }
        PR1ProcedureSegmentDto target = new PR1ProcedureSegmentDto();
        target.setSetId(pr1ProceduresSegment.getSetId());
        target.setProcedureCodingMethod(pr1ProceduresSegment.getProcedureCodingMethod());
        target.setProcedureCode(ceCodedElementTransformer.convert(pr1ProceduresSegment.getProcedureCode()));
        target.setProcedureDescription(pr1ProceduresSegment.getProcedureDescription());
        target.setProcedureDatetime(pr1ProceduresSegment.getProcedureDatetime());
        target.setProcedureFunctionalType(isCodedValueForUserDefinedTablesStringConverter.convert(pr1ProceduresSegment.getProcedureFunctionalType()));
        target.setAssociatedDiagnosisCode(ceCodedElementTransformer.convert(pr1ProceduresSegment.getAssociatedDiagnosisCode()));
        return target;
    }
}
