package com.scnsoft.eldermark.converter.dictionary;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.entity.incident.ClassMemberType;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

@Component
public class ClassMemberTypeEntityToDtoConverter extends ListAndItemTransformer<ClassMemberType, KeyValueDto>{

    @Override
    public KeyValueDto convert(ClassMemberType source) {
        return new KeyValueDto(source.getId(),source.getName());
    }

}
