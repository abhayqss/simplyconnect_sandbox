package com.scnsoft.eldermark.converter.dto2entity.signature;

import com.scnsoft.eldermark.dto.signature.DocumentSignatureTemplatePreviewRequestDto;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.document.DocumentService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateService;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureTemplateFieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DocumentSignatureTemplatePreviewRequestDtoConverter
        implements Converter<DocumentSignatureTemplatePreviewRequestDto, DocumentSignatureTemplateContext> {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DocumentSignatureTemplateService signatureTemplateService;

    @Override
    public DocumentSignatureTemplateContext convert(DocumentSignatureTemplatePreviewRequestDto source) {
        var context = new DocumentSignatureTemplateContext();

        if (source.getDocumentId() != null) {
            var document = documentService.findDocumentById(source.getDocumentId());

            if (document.getSignatureRequestId() != null) {
                context.setDocument(document);
                context.setClient(source.getClientId() == null ? null : document.getSignatureRequest().getClient());
                context.setTemplate(document.getSignatureRequest().getSignatureTemplate());
            } else {
                throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
            }
        } else {
            context.setClient(source.getClientId() == null ? null : clientService.findById(source.getClientId()));
            context.setTemplate(signatureTemplateService.findById(source.getTemplateId()));
        }

        context.setCommunity(source.getClientId() == null ? null : context.getClient().getCommunity());

        context.setTimezoneOffset(source.getTimezoneOffset());

        context.setFieldValues(DocumentSignatureTemplateFieldUtils.flattenFieldValues(source.getTemplateFieldValues()));
        context.setRequestFromMultipleClients(source.getClientId() == null);

        return context;
    }
}
