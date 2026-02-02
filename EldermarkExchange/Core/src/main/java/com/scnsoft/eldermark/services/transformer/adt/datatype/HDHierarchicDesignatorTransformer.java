package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.HDHierarchicDesignatorDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class HDHierarchicDesignatorTransformer implements Converter<HDHierarchicDesignator, HDHierarchicDesignatorDto> {
    @Override
    public HDHierarchicDesignatorDto convert(HDHierarchicDesignator hdHierarchicDesignator) {
        if (hdHierarchicDesignator == null) {
            return null;
        }
        HDHierarchicDesignatorDto target = new HDHierarchicDesignatorDto();
        target.setNamespaceID(hdHierarchicDesignator.getNamespaceID());
        target.setUniversalID(hdHierarchicDesignator.getUniversalID());
        target.setUniversalIDType(hdHierarchicDesignator.getUniversalIDType());
        return target;
    }
}
