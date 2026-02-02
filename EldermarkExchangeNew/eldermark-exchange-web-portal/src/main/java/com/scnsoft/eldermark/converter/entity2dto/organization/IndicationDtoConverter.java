package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.entity.document.ccd.Indication;

@Component
public class IndicationDtoConverter implements ListAndItemConverter<Indication, String> {

    @Override
    public String convert(Indication source) {
        return source.getValue() != null ? source.getValue().getDisplayName() : null;
    }

}
