package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CECodedElementTransformer implements Converter<CECodedElement, CECodedElementDto> {

    @Autowired
    private Converter<CECodedElement, String> ceCodedElementStringConverter;

    @Override
    public CECodedElementDto convert(CECodedElement ceCodedElement) {
        if (ceCodedElement == null) {
            return null;
        }
        CECodedElementDto target = new CECodedElementDto();
        target.setIdentifier(ceCodedElement.getIdentifier());
        target.setText(ceCodedElementStringConverter.convert(ceCodedElement));
        target.setNameOfCodingSystem(ceCodedElement.getNameOfCodingSystem());
        target.setAlternateIdentifier(ceCodedElement.getAlternateIdentifier());
        target.setAlternateText(ceCodedElement.getAlternateText());
        target.setNameOfAlternateCodingSystem(ceCodedElement.getNameOfAlternateCodingSystem());
        return target;
    }
}
