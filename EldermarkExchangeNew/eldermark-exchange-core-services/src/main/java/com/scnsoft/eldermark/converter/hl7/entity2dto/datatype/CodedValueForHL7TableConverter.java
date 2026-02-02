package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.CodedValueForHL7Table;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly =  true)
public class CodedValueForHL7TableConverter implements Converter<CodedValueForHL7Table, String> {

    @Override
    public String convert(CodedValueForHL7Table codedValueForHL7Table) {
        if (codedValueForHL7Table == null) {
            return null;
        }

        var codeTable = codedValueForHL7Table.getHl7CodeTable();
        return Optional.ofNullable(codeTable).map(HL7CodeTable::getValue).orElse(codedValueForHL7Table.getRawCode());
    }

}
