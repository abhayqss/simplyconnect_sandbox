package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.adt.datatype.XONExtendedCompositeNameAndIdForOrganizationsDto;
import com.scnsoft.eldermark.dto.adt.datatype.HDHierarchicDesignatorDto;
import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import com.scnsoft.eldermark.entity.xds.datatype.XONExtendedCompositeNameAndIdForOrganizations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly =  true)
public class XONExtendedCompositeNameAndIdForOrganizationsConverter implements
        ListAndItemConverter<XONExtendedCompositeNameAndIdForOrganizations, XONExtendedCompositeNameAndIdForOrganizationsDto> {

    @Autowired
    private Converter<HDHierarchicDesignator, HDHierarchicDesignatorDto> hdHierarchicDesignatorConverter;

    @Override
    public XONExtendedCompositeNameAndIdForOrganizationsDto convert(
            XONExtendedCompositeNameAndIdForOrganizations source) {
        if (source == null) {
            return null;
        }
        var target = new XONExtendedCompositeNameAndIdForOrganizationsDto();
        target.setIdNumber(source.getIdNumber());
        target.setOrganizationName(source.getOrganizationName());
        target.setOrganizationNameTypeCode(source.getOrganizationNameTypeCode());
        target.setAssigningAuthority(hdHierarchicDesignatorConverter.convert(source.getAssigningAuthority()));
        target.setAssigningFacility(hdHierarchicDesignatorConverter.convert(source.getAssigningFacility()));
        target.setIdentifierTypeCode(source.getIdentifierTypeCode());
        target.setNameRepresentationCode(source.getNameRepresentationCode());
        return target;
    }

}
