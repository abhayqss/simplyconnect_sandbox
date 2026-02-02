package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.CXExtendedCompositeId;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import org.springframework.stereotype.Component;

@Component
public class CXExtendedCompositeIdStringTransformer extends ListAndItemTransformer<CXExtendedCompositeId, String> {

    @Override
    public String convert(CXExtendedCompositeId cxExtendedCompositeId) {
        if (cxExtendedCompositeId == null) {
            return null;
        }
        return cxExtendedCompositeId.getpId();
    }
}
