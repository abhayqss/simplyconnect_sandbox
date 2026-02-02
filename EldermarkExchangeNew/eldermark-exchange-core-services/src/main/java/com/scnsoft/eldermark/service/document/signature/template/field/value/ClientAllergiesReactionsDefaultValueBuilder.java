package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.beans.projection.ClientAllergyAware;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ClientAllergiesReactionsDefaultValueBuilder extends ClientAllergiesDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_ALLERGIES_REACTIONS;
    }

    @Override
    protected String convertClientAllergiesToString(Stream<ClientAllergyAware> allergies) {
        return allergies.map(allergy -> {
                    if (StringUtils.isNotEmpty(allergy.getProductText())
                            && StringUtils.isNotEmpty(allergy.getCombinedReactionTexts())) {
                        return allergy.getProductText() + " - " + allergy.getCombinedReactionTexts();
                    }
                    return StringUtils.isNotEmpty(allergy.getProductText())
                            ? allergy.getProductText()
                            : allergy.getCombinedReactionTexts();
                })
                .collect(Collectors.joining("; "));
    }
}
