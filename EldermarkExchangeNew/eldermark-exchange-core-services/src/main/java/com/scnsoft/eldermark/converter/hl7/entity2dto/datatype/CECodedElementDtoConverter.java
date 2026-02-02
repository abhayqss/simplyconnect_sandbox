package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly =  true)
public class CECodedElementDtoConverter implements Converter<CECodedElement, CECodedElementDto>{

    @Autowired
    private Converter<CECodedElement, String> ceCodedElementToStringConverter;

    @Override
    public CECodedElementDto convert(CECodedElement source) {
        if (source == null) {
            return null;
        }
        
        var target = new CECodedElementDto();
        target.setIdentifier(source.getIdentifier());
        target.setText(ceCodedElementToStringConverter.convert(source));
        target.setNameOfCodingSystem(source.getNameOfCodingSystem());
        target.setAlternateIdentifier(source.getAlternateIdentifier());
        target.setAlternateText(source.getAlternateText());
        target.setNameOfAlternateCodingSystem(source.getNameOfAlternateCodingSystem());
        return target;
    }

}
