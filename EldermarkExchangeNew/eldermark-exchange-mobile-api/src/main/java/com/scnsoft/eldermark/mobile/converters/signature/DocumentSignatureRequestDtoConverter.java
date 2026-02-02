package com.scnsoft.eldermark.mobile.converters.signature;

import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureRequestDto;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureRequestUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DocumentSignatureRequestDtoConverter implements Converter<DocumentSignatureRequest, DocumentSignatureRequestDto> {

    @Override
    public DocumentSignatureRequestDto convert(DocumentSignatureRequest source) {
        var dto = new DocumentSignatureRequestDto();

        dto.setId(source.getId());
        dto.setAuthorFullName(source.getRequestedBy().getFullName());
        dto.setAuthorEmail(
                PersonTelecomUtils.findValue(source.getRequestedBy().getPerson(), PersonTelecomCode.EMAIL)
                        .orElse(null)
        );
        dto.setTemplateName(source.getSignatureTemplate().getTitle());
        dto.setExpirationDate(DateTimeUtils.toEpochMilli(source.getDateExpires()));
        dto.setStatus(DocumentSignatureRequestUtils.resolveCorrectSignatureStatus(source));

        if (source.getStatus().isSignatureRequestSentStatus()) {
            dto.setPinCode(source.getPdcflowPinCode());
            dto.setPdcFlowLink(source.getPdcflowSignatureUrl());
        }

        return dto;
    }
}
