package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.adt.datatype.XPNDto;
import com.scnsoft.eldermark.entity.xds.datatype.XPNPersonName;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class XPNDtoConverter implements ListAndItemConverter<XPNPersonName, XPNDto> {

    @Override
    public XPNDto convert(XPNPersonName xpn) {
        if (xpn == null) {
            return null;
        }

        var xpnDto = new XPNDto();

        xpnDto.setLastName(xpn.getLastName());
        xpnDto.setFirstName(xpn.getFirstName());

        return xpnDto;
    }
}
