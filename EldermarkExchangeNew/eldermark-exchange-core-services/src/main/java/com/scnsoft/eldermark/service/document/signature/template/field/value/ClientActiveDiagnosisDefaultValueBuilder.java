package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.beans.ClientProblemFilter;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.service.ClientProblemService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClientActiveDiagnosisDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ClientProblemService clientProblemService;

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_ACTIVE_DIAGNOSES;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        var result = Optional.ofNullable(context.getClient())
                .map(this::findClientProblems)
                .stream()
                .flatMap(List::stream)
                .map(ClientProblem::getProblemObservation)
                .map(ProblemObservation::getProblemName)
                .collect(Collectors.joining("; "));
        return result.isEmpty() ? null : result;
    }

    private List<ClientProblem> findClientProblems(Client client) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var problemFilter = new ClientProblemFilter();
        problemFilter.setClientId(client.getId());
        problemFilter.setIncludeActive(true);
        return clientProblemService.find(problemFilter, permissionFilter);
    }
}
