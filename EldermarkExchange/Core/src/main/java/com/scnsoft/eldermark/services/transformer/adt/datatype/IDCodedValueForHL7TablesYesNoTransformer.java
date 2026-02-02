package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.IDCodedValueForHL7Tables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0136YesNoIndicator;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IDCodedValueForHL7TablesYesNoTransformer implements Converter<IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator>, Boolean> {

    private static final String TRUE_CODE = "Y";

    @Override
    public Boolean convert(IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator> hl7Id) {
        return hl7Id != null && hl7Id.getHl7CodeTable() != null && TRUE_CODE.equals(hl7Id.getHl7CodeTable().getCode());
    }
}
