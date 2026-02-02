package com.scnsoft.eldermark.converter.hl7.entity2dto.datatype;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly =  true)
public class CECodedElementToStringConverter implements ListAndItemConverter<CECodedElement, String>{

    @Override
    public String convert(CECodedElement source) {
        if (source == null) {
            return null;
        }
        final String text = source.getText();
        if (source.getHl7CodeTable() != null) {
            if (source.getHl7CodeTable().getValue().equals(text)) {
                return text;
            } else if (StringUtils.isEmpty(text)) {
                return source.getHl7CodeTable().getValue();
            } else {
                return source.getHl7CodeTable().getValue() + ", " + text;
            }
        }
        if (text == null && source.getIdentifier() != null) {
            return source.getIdentifier();
        }
        return text;
    }

}
