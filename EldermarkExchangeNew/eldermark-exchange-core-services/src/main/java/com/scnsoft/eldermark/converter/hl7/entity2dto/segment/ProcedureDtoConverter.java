package com.scnsoft.eldermark.converter.hl7.entity2dto.segment;

import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.entity.xds.datatype.CodedValueForHL7Table;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.adt.segment.AdtProcedureDto;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.segment.PR1ProceduresSegment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ProcedureDtoConverter implements ListAndItemConverter<PR1ProceduresSegment, AdtProcedureDto> {
    
    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementDtoConverter;
    
    @Autowired
    private Converter<CodedValueForHL7Table, String> codedValueStringConverter;

    @Override
    public AdtProcedureDto convert(PR1ProceduresSegment source) {
        if (source == null) {
            return null;
        }
        var target = new AdtProcedureDto();
        target.setSetId(source.getSetId());
        target.setProcedureCodingMethod(source.getProcedureCodingMethod());
        target.setProcedureCode(ceCodedElementDtoConverter.convert(source.getProcedureCode()));
        target.setProcedureDescription(source.getProcedureDescription());
        target.setProcedureDatetime(DateTimeUtils.toEpochMilli(source.getProcedureDatetime()));
        target.setProcedureFunctionalType(codedValueStringConverter.convert(source.getProcedureFunctionalType()));
        target.setAssociatedDiagnosisCode(ceCodedElementDtoConverter.convert(source.getAssociatedDiagnosisCode()));
        return target;
    }

}
