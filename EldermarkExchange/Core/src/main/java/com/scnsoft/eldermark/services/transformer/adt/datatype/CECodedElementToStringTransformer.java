package com.scnsoft.eldermark.services.transformer.adt.datatype;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CECodedElementToStringTransformer extends ListAndItemTransformer<CECodedElement, String> {
    @Override
    public String convert(CECodedElement ceCodedElement) {
        if (ceCodedElement == null) {
            return null;
        }
        final String text = ceCodedElement.getText();
        if (ceCodedElement.getHl7CodeTable() != null) {
            if (ceCodedElement.getHl7CodeTable().getValue().equals(text)) {
                return text;
            } else if (StringUtils.isEmpty(text)) {
                return ceCodedElement.getHl7CodeTable().getValue();
            } else {
                return ceCodedElement.getHl7CodeTable().getValue() + ", " + text;
            }
        }
        if (text == null && ceCodedElement.getIdentifier() != null) {
            return ceCodedElement.getIdentifier();
        }
        return text;
    }
}
