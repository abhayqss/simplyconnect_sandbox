package com.scnsoft.eldermark.consana.sync.server.services.producers;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.DocumentUploadQueueDto;

public interface DocumentUploadQueueProducer {

    void send(DocumentUploadQueueDto uploadDocumentData);
}
