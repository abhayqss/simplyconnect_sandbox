package com.scnsoft.eldermark.converter.signature;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureBulkRequest;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureRequest;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContactNotificationMethod;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateService;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureTemplateFieldUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class SubmitDocumentSignatureBulkRequestConverter implements Converter<SubmitTemplateSignatureBulkRequest, List<SubmitTemplateSignatureRequest>> {

    @Autowired
    private DocumentSignatureTemplateService templateService;

    @Autowired
    private ClientService clientService;

    @Override
    public List<SubmitTemplateSignatureRequest> convert(SubmitTemplateSignatureBulkRequest source) {
        return source.getClientIds().stream()
                .map(clientId -> {
                    clientService.validateActive(clientId);
                    return source.getData().stream()
                            .map(dto -> {
                                var target = new SubmitTemplateSignatureRequest();

                                var templateContext = new DocumentSignatureTemplateContext();
                                target.setTemplateContext(templateContext);

                                templateContext.setTemplate(templateService.findById(dto.getTemplateId()));

                                var client = clientService.findById(clientId);
                                templateContext.setClient(client);
                                templateContext.setCommunity(client.getCommunity());
                                target.setRequestedBy(source.getRequestedBy());
                                var primaryContact = client.getPrimaryContact();
                                if (primaryContact != null) {
                                    target.setNotificationMethod(
                                            client.getPrimaryContact().getNotificationMethod().getMethod()
                                    );
                                    switch (primaryContact.getType()) {
                                        case SELF:
                                            fillClientRecipient(client, target);
                                            break;
                                        case CARE_TEAM_MEMBER:
                                            fillEmployeeRecipient(client, target);
                                            break;
                                        default:
                                            throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
                                    }
                                } else {
                                    throw new BusinessException(BusinessExceptionType.NO_PRIMARY_CONTACT);
                                }

                                target.setExpirationDate(Instant.ofEpochMilli(source.getExpirationDate()));

                                var availableSignatureAreaIds = templateService.getAvailableSignatureAreas(templateContext).stream()
                                        .map(IdAware::getId)
                                        .collect(Collectors.toSet());

                                if (!availableSignatureAreaIds.containsAll(dto.getSignatureAreaIds())) {
                                    throw new ValidationException("Invalid signature area ids");
                                }
                                templateContext.setSignatureAreaIds(dto.getSignatureAreaIds());
                                templateContext.setFieldValues(DocumentSignatureTemplateFieldUtils.flattenFieldValues(dto.getTemplateFieldValues()));
                                templateContext.setTimezoneOffset(source.getTimezoneOffset());

                                return target;
                            })
                            .collect(Collectors.toList());
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private void fillEmployeeRecipient(Client client, SubmitTemplateSignatureRequest target) {
        var primaryContact = client.getPrimaryContact();

        var clientCareTeamMember = primaryContact.getClientCareTeamMember();

        if (clientCareTeamMember != null) {
            target.setEmployeeRecipient(clientCareTeamMember.getEmployee());

            var careTeamMemberEmployee = clientCareTeamMember.getEmployee();

            if (careTeamMemberEmployee != null) {
                if (primaryContact.getNotificationMethod() == ClientPrimaryContactNotificationMethod.EMAIL) {
                    target.setEmail(
                            PersonTelecomUtils.findValue(careTeamMemberEmployee.getPerson(), PersonTelecomCode.EMAIL)
                                    .orElse(null)
                    );
                }
                if (primaryContact.getNotificationMethod() == ClientPrimaryContactNotificationMethod.PHONE) {
                    target.setPhone(
                            PersonTelecomUtils.findValue(careTeamMemberEmployee.getPerson(), PersonTelecomCode.MC)
                                    .orElse(null)
                    );
                }
            }
        }
    }

    private void fillClientRecipient(Client client, SubmitTemplateSignatureRequest target) {
        var primaryContact = client.getPrimaryContact();

        if (primaryContact != null) {
            target.setClientRecipient(client);
            if (primaryContact.getNotificationMethod() == ClientPrimaryContactNotificationMethod.EMAIL) {
                target.setEmail(
                        PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.EMAIL)
                                .orElse(null)
                );
            }
            if (primaryContact.getNotificationMethod() == ClientPrimaryContactNotificationMethod.PHONE || CollectionUtils.isEmpty(client.getAssociatedEmployeeIds())) {
                target.setPhone(
                        PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.MC)
                                .orElse(null)
                );
            }
        }
    }
}
