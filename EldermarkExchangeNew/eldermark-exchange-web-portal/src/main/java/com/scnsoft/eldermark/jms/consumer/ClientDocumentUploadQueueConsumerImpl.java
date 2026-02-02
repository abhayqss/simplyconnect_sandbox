package com.scnsoft.eldermark.jms.consumer;

import com.scnsoft.eldermark.config.WebJmsReceiveConfig;
import com.scnsoft.eldermark.entity.document.ClientDocumentUploadData;
import com.scnsoft.eldermark.jms.dto.DocumentUploadQueueDto;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.document.UploadClientDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
@Transactional
@ConditionalOnBean(WebJmsReceiveConfig.class)
public class ClientDocumentUploadQueueConsumerImpl implements ClientDocumentUploadQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ClientDocumentUploadQueueConsumerImpl.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UploadClientDocumentService uploadClientDocumentService;

    @JmsListener(
            destination = "${jms.queue.documentUpload.destination}",
            concurrency = "${jms.queue.documentUpload.concurrency}",
            containerFactory = "documentUploadJmsListenerContainerFactory"
    )
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void consume(DocumentUploadQueueDto dto) {
        try {
            var client = clientService.findById(dto.getClientId());
            var employee = employeeService.getEmployeeById(dto.getAuthorId());
            var uploadData = new ClientDocumentUploadData(dto.getTitle(), dto.getOriginalFileName(), dto.getMimeType(),
                    new ByteArrayInputStream(dto.getData()), client, employee, dto.getSharingOption())
                    .withConsanaMapId(dto.getConsanaMapId());

            uploadClientDocumentService.upload(uploadData);
        } catch (Exception e) {
            logger.warn("Failed to save document from JMS queue");
        }
    }
}
