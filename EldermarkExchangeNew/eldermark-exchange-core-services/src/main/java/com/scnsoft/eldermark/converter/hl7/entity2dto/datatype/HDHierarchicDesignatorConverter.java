package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.adt.datatype.HDHierarchicDesignatorDto;
import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly =  true)
public class HDHierarchicDesignatorConverter implements Converter<HDHierarchicDesignator, HDHierarchicDesignatorDto> {
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
