package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.shared.ccd.CcdCodeDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class CcdCodeDtoConverter implements ListAndItemConverter<CcdCode, CcdCodeDto> {

    @Override
    public CcdCodeDto convert(CcdCode source) {
        var target = new CcdCodeDto();

        target.setId(source.getId());
        target.setCode(source.getCode());
        target.setCodeSystemName(source.getCodeSystemName());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

}
