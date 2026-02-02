package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.HDHierarchicDesignatorDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XCNExtendedCompositeIdNumberAndNameForPersonsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class XCNExtendedCompositeIdNumberAndNameForPersonsTransformer extends ListAndItemTransformer<XCNExtendedCompositeIdNumberAndNameForPersons, XCNExtendedCompositeIdNumberAndNameForPersonsDto> {

    @Autowired
    private Converter<HDHierarchicDesignator, HDHierarchicDesignatorDto> hdHierarchicDesignatorTransformer;

    @Override
    public XCNExtendedCompositeIdNumberAndNameForPersonsDto convert(XCNExtendedCompositeIdNumberAndNameForPersons xcnExtendedCompositeIdNumberAndNameForPersons) {
        if (xcnExtendedCompositeIdNumberAndNameForPersons == null) {
            return null;
        }
        XCNExtendedCompositeIdNumberAndNameForPersonsDto target = new XCNExtendedCompositeIdNumberAndNameForPersonsDto();
        target.setLastName(xcnExtendedCompositeIdNumberAndNameForPersons.getLastName());
        target.setFirstName(xcnExtendedCompositeIdNumberAndNameForPersons.getFirstName());
        target.setMiddleName(xcnExtendedCompositeIdNumberAndNameForPersons.getMiddleName());
        target.setSuffix(xcnExtendedCompositeIdNumberAndNameForPersons.getSuffix());
        target.setPrefix(xcnExtendedCompositeIdNumberAndNameForPersons.getPrefix());
        target.setDegree(xcnExtendedCompositeIdNumberAndNameForPersons.getDegree());
        target.setSourceTable(xcnExtendedCompositeIdNumberAndNameForPersons.getSourceTable());
        target.setAssigningAuthority(hdHierarchicDesignatorTransformer.convert(xcnExtendedCompositeIdNumberAndNameForPersons.getAssigningAuthority()));
        target.setAssigningFacility(hdHierarchicDesignatorTransformer.convert(xcnExtendedCompositeIdNumberAndNameForPersons.getAssigningFacility()));
        target.setNameTypeCode(xcnExtendedCompositeIdNumberAndNameForPersons.getNameTypeCode());
        target.setIdentifierTypeCode(xcnExtendedCompositeIdNumberAndNameForPersons.getIdentifierTypeCode());
        target.setNameRepresentationCode(xcnExtendedCompositeIdNumberAndNameForPersons.getNameRepresentationCode());
        return target;
    }
}
