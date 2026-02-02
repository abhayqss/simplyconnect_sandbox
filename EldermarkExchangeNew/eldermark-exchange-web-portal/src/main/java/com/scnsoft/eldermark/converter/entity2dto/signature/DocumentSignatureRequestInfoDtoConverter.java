package com.scnsoft.eldermark.converter.entity2dto.signature;

import com.scnsoft.eldermark.dto.signature.DocumentSignatureRequestInfoDto;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.SignatureRequestRecipientType;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureRequestUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DocumentSignatureRequestInfoDtoConverter implements Converter<DocumentSignatureRequest, DocumentSignatureRequestInfoDto> {

    @Override
    public DocumentSignatureRequestInfoDto convert(DocumentSignatureRequest source) {
        var target = new DocumentSignatureRequestInfoDto();
        target.setId(source.getId());

        var status = DocumentSignatureRequestUtils.resolveCorrectSignatureStatus(source);

        target.setStatusName(status.name());
        target.setStatusTitle(status.getTitle());

        target.setAuthorFullName(source.getRequestedBy().getFullName());
        target.setAuthorEmail(
                PersonTelecomUtils.find(source.getRequestedBy().getPerson(), PersonTelecomCode.EMAIL)
                        .map(PersonTelecom::getNormalized)
                        .orElse(null)
        );
        if (source.getStatus().isSignatureRequestSentStatus()) {
            target.setPdcFlowLink(source.getPdcflowSignatureUrl());
            target.setPinCode(source.getPdcflowPinCode());
        }
        target.setTemplateName(source.getSignatureTemplate().getTitle());

        var type = DocumentSignatureRequestUtils.resolveRecipientType(source);
        target.setRecipientType(type);
        target.setNotificationMethod(source.getNotificationMethod());
        if (type == SignatureRequestRecipientType.CLIENT) {
            target.setRecipientId(source.getRequestedFromClientId());
            target.setRecipientFullName(source.getRequestedFromClient().getFullName());
        } else {
            target.setRecipientId(source.getRequestedFromEmployeeId());
            target.setRecipientFullName(source.getRequestedFromEmployee().getFullName());
        }

        target.setMessage(source.getMessage());

        return target;
    }
}
