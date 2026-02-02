package com.scnsoft.eldermark.converter.entity2dto.document;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.entity.document.ClientDocumentReferralAware;
import com.scnsoft.eldermark.entity.referral.ReferralAttachment;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.document.ClientDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ClientDocumentDtoReferralAttachmentConverter
        implements ListAndItemConverter<ClientDocumentReferralAware, ReferralAttachment> {

    @Autowired
    private ClientDocumentService clientDocumentService;

    @Override
    public ReferralAttachment convert(ClientDocumentReferralAware clientDocument) {
        var target = new ReferralAttachment();
        var content = clientDocumentService.readDocument(clientDocument);
        try {
            target.setContent(content.readAllBytes());
        } catch (IOException e) {
            throw new BusinessException(BusinessExceptionType.FILE_IO_ERROR, e);
        }
        target.setFileName(clientDocument.getDocumentTitle());
        target.setOriginalFileName(clientDocument.getOriginalFileName());
        target.setMimeType(clientDocument.getMimeType());

        return target;
    }
}

