package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedCodedEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DisplayableNamedCodedEntityConverter implements Converter<DisplayableNamedCodedEntity, IdentifiedNamedTitledEntityDto> {

    @Override
    public IdentifiedNamedTitledEntityDto convert(DisplayableNamedCodedEntity source) {
        return new IdentifiedNamedTitledEntityDto(source.getId(), source.getCode(), source.getDisplayName());
    }
}
