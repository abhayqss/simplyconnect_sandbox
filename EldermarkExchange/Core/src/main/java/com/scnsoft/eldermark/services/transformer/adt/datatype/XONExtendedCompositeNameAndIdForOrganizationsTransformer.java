package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import com.scnsoft.eldermark.entity.xds.datatype.XONExtendedCompositeNameAndIdForOrganizations;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.HDHierarchicDesignatorDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XONExtendedCompositeNameAndIdForOrganizationsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class XONExtendedCompositeNameAndIdForOrganizationsTransformer extends ListAndItemTransformer<XONExtendedCompositeNameAndIdForOrganizations, XONExtendedCompositeNameAndIdForOrganizationsDto> {

    @Autowired
    private Converter<HDHierarchicDesignator, HDHierarchicDesignatorDto> hdHierarchicDesignatorTransformer;

    @Override
    public XONExtendedCompositeNameAndIdForOrganizationsDto convert(XONExtendedCompositeNameAndIdForOrganizations xonExtendedCompositeNameAndIdForOrganizations) {
        if (xonExtendedCompositeNameAndIdForOrganizations == null) {
            return null;
        }
        XONExtendedCompositeNameAndIdForOrganizationsDto target = new XONExtendedCompositeNameAndIdForOrganizationsDto();
        target.setIdNumber(xonExtendedCompositeNameAndIdForOrganizations.getIdNumber());
        target.setOrganizationName(xonExtendedCompositeNameAndIdForOrganizations.getOrganizationName());
        target.setOrganizationNameTypeCode(xonExtendedCompositeNameAndIdForOrganizations.getOrganizationNameTypeCode());
        target.setAssigningAuthority(hdHierarchicDesignatorTransformer.convert(xonExtendedCompositeNameAndIdForOrganizations.getAssigningAuthority()));
        target.setAssigningFacility(hdHierarchicDesignatorTransformer.convert(xonExtendedCompositeNameAndIdForOrganizations.getAssigningFacility()));
        target.setIdentifierTypeCode(xonExtendedCompositeNameAndIdForOrganizations.getIdentifierTypeCode());
        target.setNameRepresentationCode(xonExtendedCompositeNameAndIdForOrganizations.getNameRepresentationCode());
        return target;
    }
}
