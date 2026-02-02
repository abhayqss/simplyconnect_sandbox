package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.XPNPersonName;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.springframework.stereotype.Component;

@Component
public class XPNPersonNameStringTransformer extends ListAndItemTransformer<XPNPersonName, String> {

    @Override
    public String convert(XPNPersonName xpnPersonName) {
        if (xpnPersonName == null) {
            return null;
        }
        return CareCoordinationUtils.getFullName(xpnPersonName.getFirstName(), xpnPersonName.getLastName());
    }
}
