package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientInsurancePhoneDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Autowired
    private ClientAssessmentResultService clientAssessmentResultService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CLIENT_INSURANCE_PHONE;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        return Optional.ofNullable(context.getClient())
                .map(this::findLastComprehensive)
                .map(ComprehensiveAssessment::getPharmacyPhoneNumber)
                .orElse(null);
    }

    private ComprehensiveAssessment<?> findLastComprehensive(Client client) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientAssessmentResultService.findLatestNotEmptyInProgressOrCompletedComprehensiveByClientIdWithMerged(
                client.getId(),
                permissionFilter,
                clientAssessmentResultService::hasPharmacyData
        ).orElse(null);
    }
}
