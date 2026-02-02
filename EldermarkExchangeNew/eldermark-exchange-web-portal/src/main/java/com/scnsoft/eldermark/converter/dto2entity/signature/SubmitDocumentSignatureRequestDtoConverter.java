package com.scnsoft.eldermark.converter.dto2entity.signature;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dto.signature.SubmitTemplateSignatureRequestsDto;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureRequest;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.document.DocumentService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateService;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureTemplateFieldUtils;
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
public class SubmitDocumentSignatureRequestDtoConverter implements Converter<SubmitTemplateSignatureRequestsDto, List<SubmitTemplateSignatureRequest>> {

    @Autowired
    private DocumentSignatureTemplateService templateService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DocumentService documentService;

    @Override
    public List<SubmitTemplateSignatureRequest> convert(SubmitTemplateSignatureRequestsDto source) {
        return source.getData().stream()
                .map(dto -> {
                    var target = new SubmitTemplateSignatureRequest();

                    var templateContext = new DocumentSignatureTemplateContext();
                    target.setTemplateContext(templateContext);

                    var document = dto.getDocumentId() != null
                            ? documentService.findDocumentById(dto.getDocumentId())
                            : null;

                    if (document != null) {
                        if (document.getSignatureRequestId() != null) {
                            var request = document.getSignatureRequest();
                            templateContext.setDocument(document);
                            templateContext.setTemplate(request.getSignatureTemplate());
                            templateContext.setClient(request.getClient());
                            templateContext.setCommunity(request.getClient().getCommunity());
                        } else {
                            throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
                        }
                    } else {
                        templateContext.setTemplate(templateService.findById(dto.getTemplateId()));
                        var client = clientService.findById(source.getClientId());
                        templateContext.setClient(client);
                        templateContext.setCommunity(client.getCommunity());
                    }

                    target.setExpirationDate(Instant.ofEpochMilli(source.getExpirationDate()));

                    target.setNotificationMethod(source.getNotificationMethod());
                    target.setEmail(source.getEmail());
                    target.setPhone(source.getPhone());
                    target.setMessage(source.getMessage());

                    switch (source.getRecipientType()) {
                        case SELF:
                        case STAFF:
                            target.setEmployeeRecipient(employeeService.getEmployeeById(source.getRecipientId()));
                            break;
                        case CLIENT:
                            target.setClientRecipient(clientService.findById(source.getRecipientId()));
                            break;
                        default:
                            throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
                    }

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
    }
}
