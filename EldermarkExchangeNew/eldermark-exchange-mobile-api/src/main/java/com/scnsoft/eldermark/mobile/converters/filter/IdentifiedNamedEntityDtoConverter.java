package com.scnsoft.eldermark.mobile.converters.filter;

import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class IdentifiedNamedEntityDtoConverter implements ListAndItemConverter<IdNameAware, IdentifiedNamedEntityDto> {

    @Override
    public IdentifiedNamedEntityDto convert(IdNameAware source) {
        var dto = new IdentifiedNamedEntityDto();
        dto.setId(source.getId());
        dto.setName(source.getName());
        return dto;
    }
}
