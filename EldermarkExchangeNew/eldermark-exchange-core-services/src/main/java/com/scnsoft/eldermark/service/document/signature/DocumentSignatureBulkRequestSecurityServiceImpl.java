package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.projection.DocumentIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.TemplateSignatureBulkRequestSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;
import com.scnsoft.eldermark.service.document.ClientDocumentSecurityService;
import com.scnsoft.eldermark.service.security.BaseSecurityService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("documentSignatureBulkRequestSecurityService")
public class DocumentSignatureBulkRequestSecurityServiceImpl extends BaseSecurityService implements DocumentSignatureBulkRequestSecurityService {

    @Autowired
    private DocumentSignatureRequestSecurityService documentSignatureRequestSecurityService;

    @Autowired
    private DocumentSignatureRequestService documentSignatureRequestService;

    @Autowired
    private ClientDocumentSecurityService clientDocumentSecurityService;

    @Override
    public boolean canSubmit(TemplateSignatureBulkRequestSecurityFieldsAware bulkRequestAware) {
        return bulkRequestAware.getClientIds().stream()
                .flatMap(clientId ->
                        bulkRequestAware.getTemplateIds().stream()
                                .map(templateId -> Pair.of(clientId, templateId))
                )
                .allMatch(pair ->
                        documentSignatureRequestSecurityService.canAdd(
                                DocumentSignatureRequestSecurityFieldsAware.of(pair.getFirst(), pair.getSecond())
                        )
                );
    }

    @Override
    public boolean canRenew(Long bulkRequestId) {
        var requestIdsAware = documentSignatureRequestService.findAllByBulkRequestId(bulkRequestId, IdAware.class);
        return requestIdsAware.stream()
                .allMatch(request -> documentSignatureRequestSecurityService.canRenew(request.getId()));
    }

    @Override
    public boolean canCancel(Long bulkRequestId) {
        var requestIdsAware = documentSignatureRequestService.findAllByBulkRequestId(bulkRequestId, IdAware.class);
        return requestIdsAware.stream()
                .allMatch(request -> documentSignatureRequestSecurityService.canCancel(request.getId()));
    }

    @Override
    public boolean canView(Long bulkRequestId) {
        var requestAwares =
                documentSignatureRequestService.findAllByBulkRequestId(bulkRequestId, DocumentIdAware.class);

        return requestAwares.stream()
                .allMatch(requestAware -> clientDocumentSecurityService.canView(requestAware.getDocumentId()));
    }
}
