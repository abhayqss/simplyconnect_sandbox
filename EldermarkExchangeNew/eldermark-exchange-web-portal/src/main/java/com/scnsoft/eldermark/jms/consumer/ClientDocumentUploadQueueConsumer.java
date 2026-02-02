package com.scnsoft.eldermark.jms.consumer;

import com.scnsoft.eldermark.jms.dto.DocumentUploadQueueDto;

public interface ClientDocumentUploadQueueConsumer {

    void consume(DocumentUploadQueueDto dto);

}
