package com.scnsoft.eldermark.services.jms.producer;

public interface QualifactsDocumentUploadQueueProducer {

    void putToQueue(Long documentId);

}
