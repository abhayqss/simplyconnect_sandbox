package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.model.ConsanaIdentifierDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ConsanaIdentifierDtoConverter implements Converter<String, List<ConsanaIdentifierDto>> {

    @Override
    public List<ConsanaIdentifierDto> convert(@NonNull String s) {
        return Collections.singletonList(new ConsanaIdentifierDto("http://xchangelabs.com/fhir-extensions/simply-connect-id", s));
    }
}
