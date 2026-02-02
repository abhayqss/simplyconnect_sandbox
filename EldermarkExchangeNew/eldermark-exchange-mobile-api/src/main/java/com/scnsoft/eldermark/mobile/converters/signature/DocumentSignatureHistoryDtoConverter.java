package com.scnsoft.eldermark.mobile.converters.signature;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureHistoryDto;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DocumentSignatureHistoryDtoConverter implements Converter<DocumentSignatureHistory, DocumentSignatureHistoryDto> {

    @Override
    public DocumentSignatureHistoryDto convert(DocumentSignatureHistory source) {

        var target = new DocumentSignatureHistoryDto();
        target.setRequestId(source.getRequestId());
        target.setActionName(source.getAction().name());
        target.setActionTitle(source.getActionTitle());
        target.setDate(DateTimeUtils.toEpochMilli(source.getDate()));
        if (source.getActorRoleId() != null) {
            target.setSource(CareCoordinationUtils.getFullName(source.getActorFirstName(), source.getActorLastName()));
            target.setRoleName(source.getActorRole().getCode().getCode());
            target.setRoleTitle(source.getActorRole().getName());
        }
        return target;
    }
}
