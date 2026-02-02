package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.IDCodedValueForHL7Tables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7DefinedCodeTable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IDCodedValueForHL7TablesTransformer implements Converter<IDCodedValueForHL7Tables<? extends HL7DefinedCodeTable>, String> {

    private static final String TRUE_CODE = "Y";

    @Override
    public String convert(IDCodedValueForHL7Tables<? extends HL7DefinedCodeTable> idCodedValueForHL7Tables) {
        if (idCodedValueForHL7Tables == null) {
            return null;
        }
        if (idCodedValueForHL7Tables.getHl7CodeTable() != null) {
            return idCodedValueForHL7Tables.getHl7CodeTable().getValue();
        }
        return idCodedValueForHL7Tables.getRawCode();
    }
}
