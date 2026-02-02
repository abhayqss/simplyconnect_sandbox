package com.scnsoft.eldermark.shared.ccd.converters;

import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.shared.ccd.NameDto;
import org.dozer.DozerConverter;

public class PersonNameConverter extends DozerConverter<Name, NameDto> {

    public PersonNameConverter() throws Exception {
        this(Name.class, NameDto.class);
    }

    public PersonNameConverter(Class<Name> prototypeA, Class<NameDto> prototypeB) {
        super(prototypeA, prototypeB);
    }

    @Override
    public NameDto convertTo(Name source, NameDto destination) {
        if(source == null) {
            return null;
        }

        destination = new NameDto();
        destination.setUseCode(source.getNameUse());
        destination.setFullName(ConverterUtils.join(" ", "", source.getPrefix(), source.getGiven(), source.getFamily()));

        return destination;
    }

    @Override
    public Name convertFrom(NameDto source, Name destination) {
        throw new UnsupportedOperationException();
    }
}
