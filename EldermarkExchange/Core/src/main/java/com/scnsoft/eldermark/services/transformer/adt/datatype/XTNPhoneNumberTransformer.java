package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.XTNPhoneNumber;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XTNPhoneNumberDto;
import org.springframework.stereotype.Component;

@Component
public class XTNPhoneNumberTransformer extends ListAndItemTransformer<XTNPhoneNumber, XTNPhoneNumberDto> {
    @Override
    public XTNPhoneNumberDto convert(XTNPhoneNumber xtnPhoneNumber) {
        if (xtnPhoneNumber == null) {
            return null;
        }
        XTNPhoneNumberDto target = new XTNPhoneNumberDto();
        target.setTelephoneNumber(xtnPhoneNumber.getTelephoneNumber());
        target.setPhoneNumber(xtnPhoneNumber.getPhoneNumber());
        target.setCountryCode(xtnPhoneNumber.getCountryCode());
        target.setAreaCode(xtnPhoneNumber.getAreaCode());
        target.setExtension(xtnPhoneNumber.getExtension());
        target.setEmail(xtnPhoneNumber.getEmail());
        target.setAnyText(xtnPhoneNumber.getAnyText());
        return target;
    }
}
