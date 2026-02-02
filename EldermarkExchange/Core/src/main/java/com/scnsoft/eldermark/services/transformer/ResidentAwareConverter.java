package com.scnsoft.eldermark.services.transformer;

import com.scnsoft.eldermark.entity.Resident;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public interface ResidentAwareConverter<S,T> extends Converter<S,T> {

    ResidentAwareConverter<S,T> withResident(Resident resident);

}
