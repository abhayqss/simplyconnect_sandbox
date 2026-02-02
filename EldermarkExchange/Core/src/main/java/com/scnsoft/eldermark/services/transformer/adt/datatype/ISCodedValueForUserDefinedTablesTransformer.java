package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ISCodedValueForUserDefinedTablesTransformer
        implements Converter<ISCodedValueForUserDefinedTables<? extends HL7UserDefinedCodeTable>, String> {

    @Override
    public String convert(ISCodedValueForUserDefinedTables<? extends HL7UserDefinedCodeTable> isCodedValueForUserDefinedTables) {
        if (isCodedValueForUserDefinedTables == null) {
            return null;
        }
        if (isCodedValueForUserDefinedTables.getHl7CodeTable() != null) {
            return isCodedValueForUserDefinedTables.getHl7CodeTable().getValue();
        }
        return isCodedValueForUserDefinedTables.getRawCode();
    }

}
