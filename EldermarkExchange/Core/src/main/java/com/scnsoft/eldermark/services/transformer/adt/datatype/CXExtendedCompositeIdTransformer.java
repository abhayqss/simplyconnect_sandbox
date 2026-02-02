package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.CXExtendedCompositeId;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CXExtendedCompositeIdDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CXExtendedCompositeIdTransformer extends ListAndItemTransformer<CXExtendedCompositeId, CXExtendedCompositeIdDto> {
    @Override
    public CXExtendedCompositeIdDto convert(CXExtendedCompositeId cxExtendedCompositeId) {
        if (cxExtendedCompositeId == null) {
            return null;
        }
        CXExtendedCompositeIdDto target = new CXExtendedCompositeIdDto();
        target.setpId(cxExtendedCompositeId.getpId());
        target.setIdentifierTypeCode(cxExtendedCompositeId.getIdentifierTypeCode());
        return target;
    }
}
