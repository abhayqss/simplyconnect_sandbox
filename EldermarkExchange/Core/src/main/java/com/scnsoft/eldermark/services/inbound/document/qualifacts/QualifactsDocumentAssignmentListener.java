package com.scnsoft.eldermark.services.inbound.document.qualifacts;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.services.inbound.document.DocumentAssignmentListener;
import com.scnsoft.eldermark.services.jms.producer.QualifactsDocumentUploadQueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Conditional(QualifactsDocumentAssignmentListenerRunCondition.class)
public class QualifactsDocumentAssignmentListener implements DocumentAssignmentListener {

    @Autowired
    private QualifactsDocumentUploadQueueProducer queueProducer;

    @Override
    public void postSuccessfulProcessing(Document document) {
        queueProducer.putToQueue(document.getId());
    }
}
