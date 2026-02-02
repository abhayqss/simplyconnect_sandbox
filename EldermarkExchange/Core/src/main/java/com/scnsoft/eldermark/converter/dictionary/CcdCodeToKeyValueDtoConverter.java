package com.scnsoft.eldermark.converter.dictionary;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.stereotype.Component;

@Component
public class CcdCodeToKeyValueDtoConverter extends ListAndItemTransformer<CcdCode, KeyValueDto> {
    @Override
    public KeyValueDto convert(CcdCode ccdCode) {
        return new KeyValueDto(ccdCode.getId(), ccdCode.getDisplayName());
    }
}
