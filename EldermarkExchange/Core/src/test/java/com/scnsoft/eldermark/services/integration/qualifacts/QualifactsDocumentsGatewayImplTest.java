package com.scnsoft.eldermark.services.integration.qualifacts;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.exception.integration.qualifacts.DocumentWithoutResidentException;
import com.scnsoft.eldermark.exception.integration.qualifacts.MissingClientIdException;
import com.scnsoft.eldermark.services.DocumentService;
import com.scnsoft.eldermark.services.merging.MPIService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QualifactsDocumentsGatewayImplTest {

    private static final String BASE_FOLDER = "base/";
    private static final String LSSI_OID = "1111";

    private Long documentId = 1L;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DocumentService documentService;

    @Mock
    private MPIService mpiService;

    @InjectMocks
    QualifactsDocumentsGatewayImpl instance;

    @Before
    public void injectProperties() {
        ReflectionTestUtils.setField(instance, "isSendingOutDisabled", false);
        ReflectionTestUtils.setField(instance, "baseQsiSftpFolder", BASE_FOLDER);
        ReflectionTestUtils.setField(instance, "lssiDatabaseOid", LSSI_OID);
    }

    @Test
    public void sendDocumentToQualifacts_SendoutDisabled_shouldReturn() {
        ReflectionTestUtils.setField(instance, "isSendingOutDisabled", true);
        Document document = new Document();
        document.setId(documentId);

        instance.sendDocumentToQualifacts(document);

        verifyZeroInteractions(sessionManager);
        verifyZeroInteractions(documentService);
        verifyZeroInteractions(mpiService);

    }


    @Test(expected = DocumentWithoutResidentException.class)
    public void sendDocumentToQualifacts_NoOwnerResident_shouldThrow() throws ParseException {
        Document document = new Document();
        document.setId(documentId);

        final String creationDateStr = "03142019_161112";
        document.setCreationTime(new SimpleDateFormat("MMddyyyy_HHmmss").parse(creationDateStr));

        when(documentService.getResident(document)).thenReturn(null);

        instance.sendDocumentToQualifacts(document);
    }

    @Test(expected = MissingClientIdException.class)
    public void sendDocumentToQualifacts_NoMpi_shouldThrow() throws ParseException {
        Document document = new Document();
        document.setId(documentId);

//        final String creationDateStr = "03142019_161112";
//        document.setCreationTime(new SimpleDateFormat("MMddyyyy_HHmmss").parse(creationDateStr));

        final Long residentId = 5L;
        final Resident resident = new Resident(residentId);

        when(documentService.getResident(document)).thenReturn(resident);
        when(mpiService.findMpiForResidentOrMergedAndDatabaseOid(residentId, LSSI_OID)).thenReturn(null);

        instance.sendDocumentToQualifacts(document);
    }

    @Test
    public void sendDocumentToQualifacts_HasMpi_shouldSendOutAndReturn() throws JSchException, SftpException, FileNotFoundException, ParseException {
        Document document = new Document();
        document.setId(documentId);
        final String extension = ".txt";
        final String documentTitle = "asdf" + extension;
        document.setDocumentTitle(documentTitle);

        final String creationDateStr = "03142019_161112";
        document.setCreationTime(new SimpleDateFormat("MMddyyyy_HHmmss").parse(creationDateStr));

        final Session session = mock(Session.class);
        final ChannelSftp channelSftp = mock(ChannelSftp.class);
        final InputStream inputStream = mock(InputStream.class);

        final Long residentId = 5L;
        final Resident resident = new Resident(residentId);

        final String patientId = "pidd";
        final MPI mpi = new MPI();
        mpi.setResidentId(residentId);
        mpi.setPatientId(patientId);
        mpi.setAssigningAuthorityUniversal(LSSI_OID);

        when(documentService.getResident(document)).thenReturn(resident);
        when(mpiService.findMpiForResidentOrMergedAndDatabaseOid(residentId, LSSI_OID)).thenReturn(mpi);
        when(sessionManager.getSession()).thenReturn(session);
        when(session.openChannel("sftp")).thenReturn(channelSftp);
        when(documentService.getDocumentInputStream(document)).thenReturn(inputStream);

        instance.sendDocumentToQualifacts(document);

        verify(channelSftp).connect();
        verify(channelSftp).cd(BASE_FOLDER);
        verify(channelSftp).put(inputStream, "LSSI_" + patientId + "_" + creationDateStr + extension);
        verify(channelSftp).disconnect();
    }

}