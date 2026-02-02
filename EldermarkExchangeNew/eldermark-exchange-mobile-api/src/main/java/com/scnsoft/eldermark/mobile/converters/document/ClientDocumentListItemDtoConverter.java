package com.scnsoft.eldermark.mobile.converters.document;

import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.mobile.dto.document.DocumentListItemDto;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestSecurityService;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientDocumentListItemDtoConverter extends BaseClientDocumentDtoConverter<DocumentListItemDto> {

    @Autowired
    private DocumentSignatureRequestSecurityService signatureRequestSecurityService;

    @Override
    public DocumentListItemDto convert(ClientDocument source) {
        var target = new DocumentListItemDto();
        fillBase(source, target);
        target.setDocumentType(source.getDocumentType());
        if (source.getSignatureRequestId() != null) {
            target.setCanSign(
                    source.getSignatureRequest().getStatus().isSignatureRequestSentStatus()
                            && signatureRequestSecurityService.canSign(source.getSignatureRequest())
            );
            target.setSignatureStatus(DocumentSignatureRequestUtils.resolveCorrectSignatureStatus(source.getSignatureRequest()));
        }
        return target;
    }
}
