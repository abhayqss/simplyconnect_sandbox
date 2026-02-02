package com.scnsoft.eldermark.services.jms.consumer.impl;

import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.exception.integration.qualifacts.DocumentWithoutResidentException;
import com.scnsoft.eldermark.exception.integration.qualifacts.MissingClientIdException;
import com.scnsoft.eldermark.services.integration.qualifacts.QualifactsDocumentsGateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QualifactsDocumentUploadQueueConsumerImplTest {

    @Mock
    QualifactsDocumentsGateway qualifactsDocumentsGateway;

    @Mock
    DocumentDao documentDao;

    @InjectMocks
    QualifactsDocumentUploadQueueConsumerImpl instance;

    private final Long documentId = 5L;
    final Document document = new Document();

    @Before
    public void init() {
        document.setId(documentId);
    }

    @Test
    public void consumeDocumentId_WhenCalled_ShouldSendToGateway() {
        when(documentDao.findDocument(documentId)).thenReturn(document);

        instance.consumeDocumentId(documentId);

        verify(qualifactsDocumentsGateway).sendDocumentToQualifacts(document);
    }

    @Test
    public void consumeDocumentId_WhenGatewayThrowsMissingClient_ShouldCatch() {
        when(documentDao.findDocument(documentId)).thenReturn(document);
        doThrow(MissingClientIdException.class).when(qualifactsDocumentsGateway).sendDocumentToQualifacts(document);

        instance.consumeDocumentId(documentId);

        verify(qualifactsDocumentsGateway).sendDocumentToQualifacts(document);

    }

    @Test
    public void consumeDocumentId_WhenGatewayThrowsDocumentWithoutResident_ShouldCatch() {
        when(documentDao.findDocument(documentId)).thenReturn(document);
        doThrow(DocumentWithoutResidentException.class).when(qualifactsDocumentsGateway).sendDocumentToQualifacts(document);

        instance.consumeDocumentId(documentId);

        verify(qualifactsDocumentsGateway).sendDocumentToQualifacts(document);

    }
}