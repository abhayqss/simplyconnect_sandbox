package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.adt.datatype.XTNPhoneNumberDto;
import com.scnsoft.eldermark.entity.xds.datatype.XTNPhoneNumber;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly =  true)
public class XTNPhoneNumberDtoConverter implements ListAndItemConverter<XTNPhoneNumber, XTNPhoneNumberDto> {

    @Override
    public XTNPhoneNumberDto convert(XTNPhoneNumber source) {
        if (source == null) {
            return null;
        }
        var target = new XTNPhoneNumberDto();
        target.setTelephoneNumber(source.getTelephoneNumber());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setCountryCode(source.getCountryCode());
        target.setAreaCode(source.getAreaCode());
        target.setExtension(source.getExtension());
        target.setEmail(source.getEmail());
        target.setAnyText(source.getAnyText());
        return target;
    }
}
