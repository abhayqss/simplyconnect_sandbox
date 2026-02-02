package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.adt.datatype.CXExtendedCompositeIdDto;
import com.scnsoft.eldermark.entity.xds.datatype.CXExtendedCompositeId;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly =  true)
public class CXExtendedCompositeIdConverter implements ListAndItemConverter<CXExtendedCompositeId, CXExtendedCompositeIdDto> {

    @Override
    public CXExtendedCompositeIdDto convert(CXExtendedCompositeId source) {
        if (source == null) {
            return null;
        }
        var target = new CXExtendedCompositeIdDto();
        target.setpId(source.getpId());
        target.setIdentifierTypeCode(source.getIdentifierTypeCode());
        return target;
    }
}
