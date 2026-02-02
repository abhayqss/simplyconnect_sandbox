package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.KeyValueDto;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DisplayableEntityListDtoConverter<SOURCE extends DisplayableNamedEntity> implements ListAndItemConverter<SOURCE, KeyValueDto<Long>>{

    @Override
    public KeyValueDto<Long> convert(SOURCE source) {
        return new KeyValueDto<Long>(source.getId(), source.getDisplayName());
    }

}