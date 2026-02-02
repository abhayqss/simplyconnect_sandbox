package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import com.scnsoft.eldermark.dto.adt.datatype.HDHierarchicDesignatorDto;
import com.scnsoft.eldermark.dto.adt.datatype.XCNDto;
import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class XCNDtoConverter implements Converter<XCNExtendedCompositeIdNumberAndNameForPersons, XCNDto> {

    @Autowired
    private Converter<HDHierarchicDesignator, HDHierarchicDesignatorDto> hdConverter;

    @Override
    public XCNDto convert(XCNExtendedCompositeIdNumberAndNameForPersons xcn) {
        if (xcn == null) {
            return null;
        }

        var xcnDto = new XCNDto();

        xcnDto.setIdNumber(xcn.getIdNumber());
        xcnDto.setLastName(xcn.getLastName());
        xcnDto.setFirstName(xcn.getFirstName());
        xcnDto.setMiddleName(xcn.getMiddleName());
        xcnDto.setDegree(xcn.getDegree());
        xcnDto.setAssigningAuthority(hdConverter.convert(xcn.getAssigningAuthority()));
        xcnDto.setAssigningFacility(hdConverter.convert(xcn.getAssigningFacility()));

        return xcnDto;
    }
}
