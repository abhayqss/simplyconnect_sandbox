package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.beans.projection.ClientAllergyAware;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.service.ClientAllergyService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ClientAllergiesDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Autowired
    private ClientAllergyService clientAllergyService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_ALLERGIES;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        var allergiesStream = Optional.ofNullable(context.getClient())
                .map(this::findClientAllergies)
                .stream()
                .flatMap(List::stream);
        var result = convertClientAllergiesToString(allergiesStream);
        return result.isEmpty() ? null : result;
    }

    protected List<ClientAllergyAware> findClientAllergies(Client client) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientAllergyService.findAllByClientId(client.getId(), permissionFilter);
    }

    protected String convertClientAllergiesToString(Stream<ClientAllergyAware> allergies) {
        return allergies.map(ClientAllergyAware::getProductText)
                .collect(Collectors.joining(", "));
    }
}
