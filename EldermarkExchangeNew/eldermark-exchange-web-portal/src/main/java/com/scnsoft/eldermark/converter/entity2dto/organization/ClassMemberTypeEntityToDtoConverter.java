package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.entity.event.incident.ClassMemberType;
import org.springframework.stereotype.Component;

@Component
public class ClassMemberTypeEntityToDtoConverter implements ListAndItemConverter<ClassMemberType, IdentifiedNamedEntityDto> {

    @Override
    public IdentifiedNamedEntityDto convert(ClassMemberType source) {
        return new IdentifiedNamedEntityDto(source.getId(),source.getName());
    }
}
