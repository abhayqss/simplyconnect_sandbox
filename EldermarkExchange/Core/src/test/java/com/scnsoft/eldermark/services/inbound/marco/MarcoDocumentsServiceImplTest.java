package com.scnsoft.eldermark.services.inbound.marco;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.dao.externalapi.MarcoDocumentsDao;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoIntegrationDocument;
import com.scnsoft.eldermark.exception.integration.marco.MarcoInboundException;
import com.scnsoft.eldermark.exception.integration.marco.MarcoUnassignedReason;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.SaveDocumentCallback;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarcoDocumentsServiceImplTest {

    private static final String MARCO_EMPLOYEE_LOGIN = "marcochannel@eldermark.com";
    private static final String MARCO_EMPLOYEE_ORGANIZATION = "RBA";

    @Mock
    private MarcoCareCoordinationResidentService marcoResidentService;

    @Mock
    private MarcoDocumentsDao marcoDocumentsDao;

    @Mock
    private DocumentFacade documentFacade;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private DocumentDao documentDao;

    @Mock
    private File documentFile;

    @InjectMocks
    MarcoDocumentsServiceImpl instance;

    @Test
    public void uploadDocument_whenInvalidDocument_ShouldThrow() {
        final String documentName = "doc.pdf";
        final String fileTitle = "filetitle";
        MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "first",
                "last",
                "",
                "05/24/1919",
                "111-23-4444",
                fileTitle,
                null,
                documentName);

        when(documentFile.exists()).thenReturn(false);

        try {
            instance.uploadDocument(metadata, documentFile);
        } catch (MarcoInboundException e) {
            assertEquals(MarcoUnassignedReason.REQUIRED_PARAM_MISSING, e.getUnassignedReason());
        }
    }

    @Test
    public void uploadDocument_whenMissingLastAndFullName_ShouldThrow() {
        final String documentName = "doc.pdf";
        final String fileTitle = "filetitle";
        MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "first",
                "",
                "",
                "05/24/1919",
                "111-23-4444",
                fileTitle,
                null,
                documentName);

        when(documentFile.exists()).thenReturn(true);

        try {
            instance.uploadDocument(metadata, documentFile);
        } catch (MarcoInboundException e) {
            assertEquals(MarcoUnassignedReason.REQUIRED_PARAM_MISSING, e.getUnassignedReason());
        }
    }

    @Test
    public void uploadDocument_whenMissingAllNames_ShouldThrow() {
        final String documentName = "doc.pdf";
        final String fileTitle = "filetitle";
        MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "",
                "",
                "",
                "05/24/1919",
                "111-23-4444",
                fileTitle,
                null,
                documentName);

        when(documentFile.exists()).thenReturn(true);

        try {
            instance.uploadDocument(metadata, documentFile);
        } catch (MarcoInboundException e) {
            assertEquals(MarcoUnassignedReason.REQUIRED_PARAM_MISSING, e.getUnassignedReason());
        }
    }

    @Test
    public void uploadDocument_whenMissingFirstAndFullNames_ShouldThrow() {
        final String documentName = "doc.pdf";
        final String fileTitle = "filetitle";
        MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "",
                "last",
                "",
                "05/24/1919",
                "111-23-4444",
                fileTitle,
                null,
                documentName);

        when(documentFile.exists()).thenReturn(true);

        try {
            instance.uploadDocument(metadata, documentFile);
        } catch (MarcoInboundException e) {
            assertEquals(MarcoUnassignedReason.REQUIRED_PARAM_MISSING, e.getUnassignedReason());
        }
    }

    @Test()
    public void uploadDocument_whenResidentNotFound_ShouldThrow() {
        final String documentName = "doc.pdf";
        final String fileTitle = "filetitle";
        MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "first",
                "last",
                "",
                "05/24/1919",
                "111-23-4444",
                fileTitle,
                null,
                documentName);

        when(documentFile.exists()).thenReturn(true);
        when(marcoResidentService.getPatientDetailsByIdentityFields(metadata)).thenReturn(Collections.<CareCoordinationResident>emptyList());

        try {
            instance.uploadDocument(metadata, documentFile);
        } catch (MarcoInboundException e) {
            assertEquals(MarcoUnassignedReason.RESIDENT_NOT_FOUND, e.getUnassignedReason());
        }
    }


    @Test
    public void uploadDocument_whenMultipleResidentsFound_ShouldTakeWithMpiAssignDocument() {
        final String documentName = "doc.pdf";
        final String fileTitle = "filetitle";
        final String generatedFileTitle = "filetitle.pdf";
        MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "first",
                "last",
                "",
                "05/24/1919",
                "111-23-4444",
                fileTitle,
                null,
                documentName);
        final Long resident1Id = 123L;
        final Long resident2Id = 124L;

        final CareCoordinationResident resident1 = new CareCoordinationResident();
        resident1.setId(resident1Id);

        final CareCoordinationResident resident2 = new CareCoordinationResident();
        resident2.setId(resident2Id);
        final MPI mpi = new MPI();
        final String patientId = "patientId";
        mpi.setPatientId(patientId);
        resident2.setMpi(Sets.newHashSet(mpi));


        final Long employeeId = 1000L;
        final Employee authorEmployee = new Employee();
        authorEmployee.setId(employeeId);
        final DocumentMetadata documentMetadata = new DocumentMetadata.Builder()
                .setDocumentTitle(generatedFileTitle)
                .setFileName(documentName)
                .setMimeType(URLConnection.getFileNameMap().getContentTypeFor(documentName))
                .build();
        final Document document = new Document();

        when(documentFile.exists()).thenReturn(true);
        when(marcoResidentService.getPatientDetailsByIdentityFields(metadata)).thenReturn(Arrays.asList(resident1, resident2));
        when(employeeService.getActiveEmployee(MARCO_EMPLOYEE_LOGIN, MARCO_EMPLOYEE_ORGANIZATION)).thenReturn(authorEmployee);
        when(documentFile.getName()).thenReturn(documentName);
        when(documentFacade.saveDocument(refEq(documentMetadata), eq(resident2Id), eq(employeeId), eq(true),
                eq(Collections.<Long>emptyList()), any(SaveDocumentCallback.class))).thenReturn(document);
//
        final Document result = instance.uploadDocument(metadata, documentFile);

        assertEquals(document, result);
    }

    @Test
    public void uploadDocument_whenSingleResidentFound_ShouldAssignDocument() {
        final String documentName = "doc.pdf";
        final String fileTitle = "filetitle";
        final String generatedFileTitle = "filetitle.pdf";
        MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "first",
                "last",
                "",
                "05/24/1919",
                "111-23-4444",
                fileTitle,
                null,
                documentName);
        final Long residentId = 123L;
        final CareCoordinationResident resident = new CareCoordinationResident();
        resident.setId(residentId);
        final Long employeeId = 1000L;
        final Employee authorEmployee = new Employee();
        authorEmployee.setId(employeeId);
        final DocumentMetadata documentMetadata = new DocumentMetadata.Builder()
                .setDocumentTitle(generatedFileTitle)
                .setFileName(documentName)
                .setMimeType(URLConnection.getFileNameMap().getContentTypeFor(documentName))
                .build();
        final Document document = new Document();

        when(documentFile.exists()).thenReturn(true);
        when(marcoResidentService.getPatientDetailsByIdentityFields(metadata)).thenReturn(Collections.singletonList(resident));
        when(employeeService.getActiveEmployee(MARCO_EMPLOYEE_LOGIN, MARCO_EMPLOYEE_ORGANIZATION)).thenReturn(authorEmployee);
        when(documentFile.getName()).thenReturn(documentName);
        when(documentFacade.saveDocument(refEq(documentMetadata), eq(residentId), eq(employeeId), eq(true),
                eq(Collections.<Long>emptyList()), any(SaveDocumentCallback.class))).thenReturn(document);
//
        final Document result = instance.uploadDocument(metadata, documentFile);

        assertEquals(document, result);
    }

    @Test
    public void createNewMarcoIntegrationDocumentLogForAssignedDocument_WhenCalled_ShouldSave() {
        final MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "first",
                "last",
                "",
                "05/24/1919",
                "111-23-4444",
                "title",
                "author",
                "docName");
        final MarcoIntegrationDocument expected = new MarcoIntegrationDocument();
        final Document document = new Document();
        expected.setOrganizationName(metadata.getOrganizationName());
        expected.setFirstName(metadata.getFirstName());
        expected.setLastName(metadata.getLastName());
        expected.setFullName(metadata.getFullName());
        expected.setDateOfBirthStr(metadata.getDateOfBirthStr());
        expected.setSsn(metadata.getSsn());
        expected.setFileTitle(metadata.getFileTitle());
        expected.setAuthor(metadata.getAuthor());
        expected.setDocumentOriginalName(metadata.getDocumentOriginalName());
        expected.setDocument(document);
        expected.setId(1234L);

        when(marcoDocumentsDao.save(refEq(expected, "id", "receivedTime"))).thenReturn(expected);

        final MarcoIntegrationDocument result = instance.createNewMarcoIntegrationDocumentLog(metadata, document);

        assertEquals(expected, result);
        assertEquals(expected, document.getMarcoIntegrationDocument());
        verify(documentDao).updateDocument(document);
    }

    @Test
    public void createNewMarcoIntegrationDocumentLogForUnassignedDocument_WhenCalled_ShouldSave() {
        final MarcoUnassignedReason unassignedReason = MarcoUnassignedReason.REQUIRED_PARAM_MISSING;
        final MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "first",
                "last",
                "",
                "05/24/1919",
                "111-23-4444",
                "title",
                "author",
                "docName");
        final MarcoIntegrationDocument expected = new MarcoIntegrationDocument();
        expected.setOrganizationName(metadata.getOrganizationName());
        expected.setFirstName(metadata.getFirstName());
        expected.setLastName(metadata.getLastName());
        expected.setFullName(metadata.getFullName());
        expected.setDateOfBirthStr(metadata.getDateOfBirthStr());
        expected.setSsn(metadata.getSsn());
        expected.setFileTitle(metadata.getFileTitle());
        expected.setAuthor(metadata.getAuthor());
        expected.setDocumentOriginalName(metadata.getDocumentOriginalName());
        expected.setUnassignedReason(unassignedReason);

        when(marcoDocumentsDao.save(refEq(expected, "receivedTime"))).thenReturn(expected);

        final MarcoIntegrationDocument result = instance.createNewMarcoIntegrationDocumentLog(metadata, unassignedReason);

        assertEquals(expected, result);
    }

    @Test
    public void createNewMarcoIntegrationDocumentLogForUnassignedDocument_WhenCalledWithFullName_ShouldSave() {
        final MarcoUnassignedReason unassignedReason = MarcoUnassignedReason.REQUIRED_PARAM_MISSING;
        final MarcoDocumentMetadata metadata = new MarcoDocumentMetadata("test Org",
                "",
                "",
                "last, first",
                "05/24/1919",
                "111-23-4444",
                "title",
                "author",
                "docName");
        final MarcoIntegrationDocument expected = new MarcoIntegrationDocument();
        expected.setOrganizationName(metadata.getOrganizationName());
        expected.setFirstName(metadata.getFirstName());
        expected.setLastName(metadata.getLastName());
        expected.setFullName(metadata.getFullName());
        expected.setDateOfBirthStr(metadata.getDateOfBirthStr());
        expected.setSsn(metadata.getSsn());
        expected.setFileTitle(metadata.getFileTitle());
        expected.setAuthor(metadata.getAuthor());
        expected.setDocumentOriginalName(metadata.getDocumentOriginalName());
        expected.setUnassignedReason(unassignedReason);

        when(marcoDocumentsDao.save(refEq(expected, "receivedTime"))).thenReturn(expected);

        final MarcoIntegrationDocument result = instance.createNewMarcoIntegrationDocumentLog(metadata, unassignedReason);

        assertEquals(expected, result);
    }
}