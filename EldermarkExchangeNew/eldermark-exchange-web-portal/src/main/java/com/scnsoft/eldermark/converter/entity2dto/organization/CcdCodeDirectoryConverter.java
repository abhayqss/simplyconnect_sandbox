package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.KeyValueDto;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Component
public class CcdCodeDirectoryConverter implements ListAndItemConverter<CcdCode, KeyValueDto<Long>> {

    @Override
    public KeyValueDto<Long> convert(CcdCode source) {
        return new KeyValueDto<Long>(source.getId(), source.getDisplayName());
    }
}
