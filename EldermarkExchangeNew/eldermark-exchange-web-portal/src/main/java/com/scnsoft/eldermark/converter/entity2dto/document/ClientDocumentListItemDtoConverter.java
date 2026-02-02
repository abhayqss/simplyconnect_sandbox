package com.scnsoft.eldermark.converter.entity2dto.document;

import com.scnsoft.eldermark.dto.document.ClientDocumentListItemDto;
import com.scnsoft.eldermark.dto.document.DocumentSignatureDto;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureBulkRequestSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDocumentListItemDtoConverter extends BaseClientDocumentDtoConverter<ClientDocumentListItemDto> {

    @Autowired
    private DocumentSignatureBulkRequestSecurityService documentSignatureBulkRequestSecurityService;

    @Override
    public ClientDocumentListItemDto convert(ClientDocument source) {
        var target = new ClientDocumentListItemDto();
        fillData(source, target);
        fillSignatureData(source, target);
        return target;
    }

    private void fillSignatureData(ClientDocument source, ClientDocumentListItemDto target) {
        var request = source.getSignatureRequest();
        if (request != null) {
            var status = DocumentSignatureRequestUtils.resolveCorrectSignatureStatus(request);
            var dto = new DocumentSignatureDto();
            dto.setSignedDate(DateTimeUtils.toEpochMilli(request.getDateSigned()));
            dto.setRequestedDate(DateTimeUtils.toEpochMilli(request.getDateCreated()));
            dto.setStatusName(status.name());
            dto.setStatusTitle(status.getTitle());

            var bulkRequestId = request.getBulkRequestId();
            if (bulkRequestId != null) {
                boolean canView = documentSignatureBulkRequestSecurityService.canView(bulkRequestId);
                dto.setBulkRequestId(canView ? bulkRequestId : null);
            }

            target.setSignature(dto);
        }
    }
}
