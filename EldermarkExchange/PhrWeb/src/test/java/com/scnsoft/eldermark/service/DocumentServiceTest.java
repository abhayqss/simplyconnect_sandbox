package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.services.ReportGenerator;
import com.scnsoft.eldermark.services.ReportGeneratorFactory;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exceptions.DocumentNotFoundException;
import com.scnsoft.eldermark.shared.utils.MathUtils;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.DateDto;
import com.scnsoft.eldermark.web.entity.DocumentInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/28/2017.
 */
public class DocumentServiceTest extends BaseServiceTest {

    @Mock
    private DocumentDao documentDao;
    @Mock
    private DocumentFacade documentFacade;
    @Mock
    private ReportGeneratorFactory reportGeneratorFactory;
    @Mock
    private ResidentDao residentDao;
    @Mock
    private EmployeeDao employeeDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private UserDao userDao;
    @Mock
    private ReportGenerator ccdGenerator;
    @Mock
    private ReportGenerator facesheetGenerator;
    @Mock
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;
    @Mock
    private MPIService mpiService;

    @InjectMocks
    private DocumentService documentService;

    // Shared test data
    protected final Long documentId = TestDataGenerator.randomId();

    protected final Long careReceiverId = TestDataGenerator.randomId();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID (current): %d\nDocument ID: %d\n\n", userId, documentId);
    }

    @Before
    public void initGenerators() {
        when(reportGeneratorFactory.getGenerator("ccd")).thenReturn(ccdGenerator);
        when(reportGeneratorFactory.getGenerator("facesheet")).thenReturn(facesheetGenerator);
        when(ccdGenerator.metadata()).thenReturn(TestDataGenerator.ccdMetadata());
        when(facesheetGenerator.metadata()).thenReturn(TestDataGenerator.facesheetMetadata());
    }

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User user = super.createConsumer(consumerUserId);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        when(residentDao.getResidents(activeResidentIds)).thenReturn(activeResidents);
        when(residentDao.getResidents(allResidentIds)).thenReturn(allResidents);
        when(residentDao.get(user.getResidentId())).thenReturn(user.getResident());
        when(userDao.findOne(consumerUserId)).thenReturn(user);
        when(userDao.getOne(consumerUserId)).thenReturn(user);
        if (consumerUserId.equals(userId)) {
            when(careTeamSecurityUtils.getCurrentUser()).thenReturn(user);
        }

        return user;
    }

    private ResidentCareTeamMember setUpMockitoExpectationsForReceiver(Long careReceiverId, boolean isAllResidents) {
        final ResidentCareTeamMember careTeamMember = super.createCareTeamMember(careReceiverId);

        when(residentCareTeamMemberDao.get(careReceiverId)).thenReturn(careTeamMember);
        when(mpiService.listResidentWithMergedResidents(careTeamMember.getResidentId())).thenReturn(isAllResidents ? allResidentIds : activeResidentIds);
        return careTeamMember;

    }

    private User setUpMockitoExpectationsAsProvider(Long providerUserId) {
        final User user = super.createProvider(providerUserId);

        when(userDao.findOne(providerUserId)).thenReturn(user);
        when(userDao.getOne(providerUserId)).thenReturn(user);
        when(employeeDao.get(employeeId)).thenReturn(user.getEmployee());
        if (providerUserId.equals(userId)) {
            when(careTeamSecurityUtils.getCurrentUser()).thenReturn(user);
        }

        return user;
    }

    @Test
    public void testGetDocuments() {
        // Expected objects
        setUpMockitoExpectations(userId);

        final Date creationTime = TestDataGenerator.randomDate();

        final Document document = new Document();
        document.setId(documentId);
        document.setHash("hash");
        document.setSize(102400);
        document.setCreationTime(creationTime);
        document.setDocumentTitle("title.png");
        document.setOriginalFileName("title.png");
        document.setMimeType("application/png");
        document.setVisible(Boolean.TRUE);
        document.setResidentDatabaseAlternativeId("test alternative id");
        document.setIsCDA(false);

        final DateDto dateDto = new DateDto();
        dateDto.setDateTime(creationTime.getTime());
        final DocumentInfoDto expectedDocumentInfoDto = new DocumentInfoDto();
        expectedDocumentInfoDto.setId(documentId);
        expectedDocumentInfoDto.setHash("hash");
        expectedDocumentInfoDto.setType("Custom");
        expectedDocumentInfoDto.setTitle("title");
        expectedDocumentInfoDto.setExtension("png");
        expectedDocumentInfoDto.setMimeType("application/png");
        expectedDocumentInfoDto.setCreatedOn(dateDto);
        expectedDocumentInfoDto.setSizeKb(MathUtils.round(102400 / 1024.0, 2));
        expectedDocumentInfoDto.setDataSource("test alternative id");
        expectedDocumentInfoDto.setIsCdaViewable(false);
        final DocumentInfoDto expectedCcdDocumentInfoDto = new DocumentInfoDto();
        expectedCcdDocumentInfoDto.setId(null);
        expectedCcdDocumentInfoDto.setHash(null);
        expectedCcdDocumentInfoDto.setType("CCD");
        expectedCcdDocumentInfoDto.setTitle("CCD");
        expectedCcdDocumentInfoDto.setExtension("xml");
        expectedCcdDocumentInfoDto.setMimeType("text/xml");
        expectedCcdDocumentInfoDto.setCreatedOn(null);
        expectedCcdDocumentInfoDto.setSizeKb(null);
        expectedCcdDocumentInfoDto.setDataSource(resident.getFacility().getName());
        final DocumentInfoDto expectedFacesheetDocumentInfoDto = new DocumentInfoDto();
        expectedFacesheetDocumentInfoDto.setId(null);
        expectedFacesheetDocumentInfoDto.setHash(null);
        expectedFacesheetDocumentInfoDto.setType("Facesheet");
        expectedFacesheetDocumentInfoDto.setTitle("FACESHEET");
        expectedFacesheetDocumentInfoDto.setExtension("pdf");
        expectedFacesheetDocumentInfoDto.setMimeType("application/pdf");
        expectedFacesheetDocumentInfoDto.setCreatedOn(null);
        expectedFacesheetDocumentInfoDto.setSizeKb(null);
        expectedFacesheetDocumentInfoDto.setDataSource(resident.getFacility().getName());

        // Mockito expectations
        when(documentDao.queryForDocuments(activeResidents, null)).thenReturn(Collections.singletonList(document));
        when(documentDao.queryForDocumentsByResidentIdIn(activeResidentIds, null, null)).thenReturn(Collections.singletonList(document));

        // Execute the method being tested
        List<DocumentInfoDto> result = documentService.getDocumentsForUser(userId, null);

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedCcdDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(1), sameBeanAs(expectedFacesheetDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(2), sameBeanAs(expectedDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testGetDocumentsForReceiverCdaViewable() throws Exception {
        testGetDocumentsForReceiver(true);
    }

    @Test
    public void testGetDocumentsForReceiverNotCdaViewable() throws Exception {
        testGetDocumentsForReceiver(false);
    }

    private void testGetDocumentsForReceiver(boolean isCda) throws Exception {
        // Expected objects
        final ResidentCareTeamMember careTeamMember = setUpMockitoExpectationsForReceiver(careReceiverId, false);
        final User currentUser = super.createProvider(userId);
        when(careTeamSecurityUtils.getCurrentUser()).thenReturn(currentUser);
        when(residentDao.get(activeResidentIds.get(0))).thenReturn(careTeamMember.getResident());

        final Date creationTime = TestDataGenerator.randomDate();

        final Document document = new Document();
        document.setId(documentId);
        document.setHash("hash");
        document.setSize(102400);
        document.setCreationTime(creationTime);
        document.setDocumentTitle("title.png");
        document.setOriginalFileName("title.png");
        document.setMimeType("application/png");
        document.setVisible(Boolean.TRUE);
        document.setResidentDatabaseAlternativeId("test alternative id");
        document.setIsCDA(isCda);

        final DateDto dateDto = new DateDto();
        dateDto.setDateTime(creationTime.getTime());
        final DocumentInfoDto expectedDocumentInfoDto = new DocumentInfoDto();
        expectedDocumentInfoDto.setId(documentId);
        expectedDocumentInfoDto.setHash("hash");
        expectedDocumentInfoDto.setType(isCda ? "Ccd" : "Custom");
        expectedDocumentInfoDto.setTitle("title");
        expectedDocumentInfoDto.setExtension("png");
        expectedDocumentInfoDto.setMimeType("application/png");
        expectedDocumentInfoDto.setCreatedOn(dateDto);
        expectedDocumentInfoDto.setSizeKb(MathUtils.round(102400 / 1024.0, 2));
        expectedDocumentInfoDto.setDataSource("test alternative id");
        expectedDocumentInfoDto.setIsCdaViewable(isCda);
        final DocumentInfoDto expectedCcdDocumentInfoDto = new DocumentInfoDto();
        expectedCcdDocumentInfoDto.setId(null);
        expectedCcdDocumentInfoDto.setHash(null);
        expectedCcdDocumentInfoDto.setType("CCD");
        expectedCcdDocumentInfoDto.setTitle("CCD");
        expectedCcdDocumentInfoDto.setExtension("xml");
        expectedCcdDocumentInfoDto.setMimeType("text/xml");
        expectedCcdDocumentInfoDto.setCreatedOn(null);
        expectedCcdDocumentInfoDto.setSizeKb(null);
        expectedCcdDocumentInfoDto.setDataSource(careTeamMember.getResident().getFacility().getName());
        final DocumentInfoDto expectedFacesheetDocumentInfoDto = new DocumentInfoDto();
        expectedFacesheetDocumentInfoDto.setId(null);
        expectedFacesheetDocumentInfoDto.setHash(null);
        expectedFacesheetDocumentInfoDto.setType("Facesheet");
        expectedFacesheetDocumentInfoDto.setTitle("FACESHEET");
        expectedFacesheetDocumentInfoDto.setExtension("pdf");
        expectedFacesheetDocumentInfoDto.setMimeType("application/pdf");
        expectedFacesheetDocumentInfoDto.setCreatedOn(null);
        expectedFacesheetDocumentInfoDto.setSizeKb(null);
        expectedFacesheetDocumentInfoDto.setDataSource(careTeamMember.getResident().getFacility().getName());

        // Mockito expectations
        when(documentDao.queryForDocuments(activeResidents, currentUser.getEmployee())).thenReturn(Collections.singletonList(document));
        when(documentDao.queryForDocumentsByResidentIdIn(activeResidentIds, currentUser.getEmployee(), null)).thenReturn(Collections.singletonList(document));

        // Execute the method being tested
        List<DocumentInfoDto> result = documentService.getDocumentsForReceiver(careReceiverId, null);

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedCcdDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(1), sameBeanAs(expectedFacesheetDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(2), sameBeanAs(expectedDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        verify(careTeamSecurityUtils).checkAccessToCareTeamMemberOrThrow(careTeamMember, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testGetDocumentsPaged() {
        // Expected objects
        setUpMockitoExpectations(userId);

        final Date creationTime = TestDataGenerator.randomDate();

        final Document document = new Document();
        document.setId(documentId);
        document.setHash("hash");
        document.setSize(102400);
        document.setCreationTime(creationTime);
        document.setDocumentTitle("title.png");
        document.setOriginalFileName("title.png");
        document.setMimeType("application/png");
        document.setVisible(Boolean.TRUE);
        document.setResidentDatabaseAlternativeId("test alternative id");
        document.setIsCDA(false);

        final Document document2 = new Document();
        document2.setId(documentId + 1);
        document2.setHash("hash");
        document2.setSize(204800);
        document2.setCreationTime(creationTime);
        document2.setDocumentTitle("title 2.png");
        document2.setOriginalFileName("title 2.png");
        document2.setMimeType("application/png");
        document2.setVisible(Boolean.TRUE);
        document2.setResidentDatabaseAlternativeId("test alternative id");
        document2.setIsCDA(false);

        final DateDto dateDto = new DateDto();
        dateDto.setDateTime(creationTime.getTime());
        final DocumentInfoDto expectedDocumentInfoDto = new DocumentInfoDto();
        expectedDocumentInfoDto.setId(documentId);
        expectedDocumentInfoDto.setHash("hash");
        expectedDocumentInfoDto.setType("Custom");
        expectedDocumentInfoDto.setTitle("title");
        expectedDocumentInfoDto.setExtension("png");
        expectedDocumentInfoDto.setMimeType("application/png");
        expectedDocumentInfoDto.setCreatedOn(dateDto);
        expectedDocumentInfoDto.setSizeKb(MathUtils.round(102400 / 1024.0, 2));
        expectedDocumentInfoDto.setDataSource("test alternative id");
        expectedDocumentInfoDto.setIsCdaViewable(false);
        final DocumentInfoDto expectedCcdDocumentInfoDto = new DocumentInfoDto();
        expectedCcdDocumentInfoDto.setId(null);
        expectedCcdDocumentInfoDto.setHash(null);
        expectedCcdDocumentInfoDto.setType("CCD");
        expectedCcdDocumentInfoDto.setTitle("CCD");
        expectedCcdDocumentInfoDto.setExtension("xml");
        expectedCcdDocumentInfoDto.setMimeType("text/xml");
        expectedCcdDocumentInfoDto.setCreatedOn(null);
        expectedCcdDocumentInfoDto.setSizeKb(null);
        expectedCcdDocumentInfoDto.setDataSource(resident.getFacility().getName());
        final DocumentInfoDto expectedFacesheetDocumentInfoDto = new DocumentInfoDto();
        expectedFacesheetDocumentInfoDto.setId(null);
        expectedFacesheetDocumentInfoDto.setHash(null);
        expectedFacesheetDocumentInfoDto.setType("Facesheet");
        expectedFacesheetDocumentInfoDto.setTitle("FACESHEET");
        expectedFacesheetDocumentInfoDto.setExtension("pdf");
        expectedFacesheetDocumentInfoDto.setMimeType("application/pdf");
        expectedFacesheetDocumentInfoDto.setCreatedOn(null);
        expectedFacesheetDocumentInfoDto.setSizeKb(null);
        expectedFacesheetDocumentInfoDto.setDataSource(resident.getFacility().getName());

        final Pageable pageable = PaginationUtils.buildPageable(3, 0);

        // Mockito expectations
        when(documentDao.queryForDocuments(anyCollectionOf(Resident.class), isNull(Employee.class)))
                .thenReturn(Arrays.asList(document, document2));
        when(documentDao.queryForDocumentsByResidentIdIn(anyCollectionOf(Long.class), isNull(Employee.class), isNull(Pageable.class)))
                .thenReturn(Arrays.asList(document, document2));
        when(documentDao.queryForDocumentsByResidentIdIn(anyCollectionOf(Long.class), isNull(Employee.class), eq(0), gt(1)))
                .thenReturn(Arrays.asList(document, document2));
        when(documentDao.queryForDocumentsByResidentIdIn(activeResidentIds, null, 0, 1))
                .thenReturn(Arrays.asList(document));
        when(documentDao.queryForDocumentsByResidentIdIn(allResidentIds, null, 0, 1))
                .thenReturn(Arrays.asList(document));

        // Execute the method being tested
        List<DocumentInfoDto> result = documentService.getDocumentsForUser(userId, pageable);

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedCcdDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(1), sameBeanAs(expectedFacesheetDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(2), sameBeanAs(expectedDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testGetDocumentsPagedForReceiver() throws Exception {
        // Expected objects
        final ResidentCareTeamMember careTeamMember = setUpMockitoExpectationsForReceiver(careReceiverId, false);
        final User currentUser = super.createProvider(userId);
        when(careTeamSecurityUtils.getCurrentUser()).thenReturn(currentUser);
        when(residentDao.get(activeResidentIds.get(0))).thenReturn(careTeamMember.getResident());

        final Date creationTime = TestDataGenerator.randomDate();

        final Document document = new Document();
        document.setId(documentId);
        document.setHash("hash");
        document.setSize(102400);
        document.setCreationTime(creationTime);
        document.setDocumentTitle("title.png");
        document.setOriginalFileName("title.png");
        document.setMimeType("application/png");
        document.setVisible(Boolean.TRUE);
        document.setResidentDatabaseAlternativeId("test alternative id");
        document.setIsCDA(false);

        final Document document2 = new Document();
        document2.setId(documentId + 1);
        document2.setHash("hash");
        document2.setSize(204800);
        document2.setCreationTime(creationTime);
        document2.setDocumentTitle("title 2.png");
        document2.setOriginalFileName("title 2.png");
        document2.setMimeType("application/png");
        document2.setVisible(Boolean.TRUE);
        document2.setResidentDatabaseAlternativeId("test alternative id");
        document2.setIsCDA(false);

        final DateDto dateDto = new DateDto();
        dateDto.setDateTime(creationTime.getTime());
        final DocumentInfoDto expectedDocumentInfoDto = new DocumentInfoDto();
        expectedDocumentInfoDto.setId(documentId);
        expectedDocumentInfoDto.setHash("hash");
        expectedDocumentInfoDto.setType("Custom");
        expectedDocumentInfoDto.setTitle("title");
        expectedDocumentInfoDto.setExtension("png");
        expectedDocumentInfoDto.setMimeType("application/png");
        expectedDocumentInfoDto.setCreatedOn(dateDto);
        expectedDocumentInfoDto.setSizeKb(MathUtils.round(102400 / 1024.0, 2));
        expectedDocumentInfoDto.setDataSource("test alternative id");
        expectedDocumentInfoDto.setIsCdaViewable(false);
        final DocumentInfoDto expectedCcdDocumentInfoDto = new DocumentInfoDto();
        expectedCcdDocumentInfoDto.setId(null);
        expectedCcdDocumentInfoDto.setHash(null);
        expectedCcdDocumentInfoDto.setType("CCD");
        expectedCcdDocumentInfoDto.setTitle("CCD");
        expectedCcdDocumentInfoDto.setExtension("xml");
        expectedCcdDocumentInfoDto.setMimeType("text/xml");
        expectedCcdDocumentInfoDto.setCreatedOn(null);
        expectedCcdDocumentInfoDto.setSizeKb(null);
        expectedCcdDocumentInfoDto.setDataSource(careTeamMember.getResident().getFacility().getName());
        final DocumentInfoDto expectedFacesheetDocumentInfoDto = new DocumentInfoDto();
        expectedFacesheetDocumentInfoDto.setId(null);
        expectedFacesheetDocumentInfoDto.setHash(null);
        expectedFacesheetDocumentInfoDto.setType("Facesheet");
        expectedFacesheetDocumentInfoDto.setTitle("FACESHEET");
        expectedFacesheetDocumentInfoDto.setExtension("pdf");
        expectedFacesheetDocumentInfoDto.setMimeType("application/pdf");
        expectedFacesheetDocumentInfoDto.setCreatedOn(null);
        expectedFacesheetDocumentInfoDto.setSizeKb(null);
        expectedFacesheetDocumentInfoDto.setDataSource(careTeamMember.getResident().getFacility().getName());

        final Pageable pageable = PaginationUtils.buildPageable(3, 0);

        // Mockito expectations
        when(documentDao.queryForDocuments(anyCollectionOf(Resident.class), same(currentUser.getEmployee())))
                .thenReturn(Arrays.asList(document, document2));
        when(documentDao.queryForDocumentsByResidentIdIn(anyCollectionOf(Long.class), same(currentUser.getEmployee()), isNull(Pageable.class)))
                .thenReturn(Arrays.asList(document, document2));
        when(documentDao.queryForDocumentsByResidentIdIn(anyCollectionOf(Long.class), same(currentUser.getEmployee()), eq(0), gt(1)))
                .thenReturn(Arrays.asList(document, document2));
        when(documentDao.queryForDocumentsByResidentIdIn(activeResidentIds, currentUser.getEmployee(), 0, 1))
                .thenReturn(Arrays.asList(document));
        when(documentDao.queryForDocumentsByResidentIdIn(allResidentIds, currentUser.getEmployee(), 0, 1))
                .thenReturn(Arrays.asList(document));

        // Execute the method being tested
        List<DocumentInfoDto> result = documentService.getDocumentsForReceiver(careReceiverId, pageable);

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedCcdDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(1), sameBeanAs(expectedFacesheetDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(2), sameBeanAs(expectedDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        verify(careTeamSecurityUtils).checkAccessToCareTeamMemberOrThrow(careTeamMember, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testCountDocuments() {
        // Expected objects
        setUpMockitoExpectations(userId);

        // Mockito expectations
        when(documentDao.countDocumentsByResidentIdIn(activeResidentIds, null))
                .thenReturn(2L);

        // Execute the method being tested
        final Long result = documentService.countDocumentsForUser(userId);

        // Validation
        assertThat(result, equalTo(2L + 2));
    }

    @Test
    public void testCountDocumentsForReceiver() throws Exception {
        // Expected objects
        setUpMockitoExpectationsForReceiver(careReceiverId, false);
        final User currentUser = super.createProvider(userId);
        when(careTeamSecurityUtils.getCurrentUser()).thenReturn(currentUser);

        // Mockito expectations
        when(documentDao.countDocumentsByResidentIdIn(activeResidentIds, currentUser.getEmployee()))
                .thenReturn(2L);

        // Execute the method being tested
        final Long result = documentService.countDocumentsForReceiver(careReceiverId);

        // Validation
        assertThat(result, equalTo(2L + 2));
    }


    @Test
    public void testCountDocumentsAsProvider() {
        // Expected objects
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        final User provider = setUpMockitoExpectationsAsProvider(userId);
        final User consumer = setUpMockitoExpectations(consumerId);

        // Mockito expectations
        when(documentDao.countDocumentsByResidentIdIn(allResidentIds, provider.getEmployee()))
                .thenReturn(3L);

        // Execute the method being tested
        final Long result = documentService.countDocumentsForUser(consumerId);

        // Validation
        assertThat(result, equalTo(3L + 2));
    }

    @Test
    public void testGetDocumentsAsProviderCdaViewable() throws Exception {
        testGetDocumentsAsProvider(true);
    }


    @Test
    public void testGetDocumentsAsProviderNotCdaViewable() throws Exception {
        testGetDocumentsAsProvider(false);
    }

    public void testGetDocumentsAsProvider(boolean isCda) {
        // Expected objects
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        final User provider = setUpMockitoExpectationsAsProvider(userId);
        final User consumer = setUpMockitoExpectations(consumerId);

        final Date creationTime = TestDataGenerator.randomDate();

        final Document document = new Document();
        document.setId(documentId);
        document.setHash("hash");
        document.setSize(102400);
        document.setCreationTime(creationTime);
        document.setDocumentTitle("title.png");
        document.setOriginalFileName("title.png");
        document.setMimeType("application/png");
        document.setVisible(Boolean.TRUE);
        document.setResidentDatabaseAlternativeId("test alternative id");
        document.setIsCDA(isCda);

        final DateDto dateDto = new DateDto();
        dateDto.setDateTime(creationTime.getTime());
        final DocumentInfoDto expectedDocumentInfoDto = new DocumentInfoDto();
        expectedDocumentInfoDto.setId(documentId);
        expectedDocumentInfoDto.setHash("hash");
        expectedDocumentInfoDto.setType(isCda ? "Ccd" : "Custom");
        expectedDocumentInfoDto.setTitle("title");
        expectedDocumentInfoDto.setExtension("png");
        expectedDocumentInfoDto.setMimeType("application/png");
        expectedDocumentInfoDto.setCreatedOn(dateDto);
        expectedDocumentInfoDto.setSizeKb(MathUtils.round(102400 / 1024.0, 2));
        expectedDocumentInfoDto.setDataSource("test alternative id");
        expectedDocumentInfoDto.setIsCdaViewable(isCda);
        final DocumentInfoDto expectedCcdDocumentInfoDto = new DocumentInfoDto();
        expectedCcdDocumentInfoDto.setId(null);
        expectedCcdDocumentInfoDto.setHash(null);
        expectedCcdDocumentInfoDto.setType("CCD");
        expectedCcdDocumentInfoDto.setTitle("CCD");
        expectedCcdDocumentInfoDto.setExtension("xml");
        expectedCcdDocumentInfoDto.setMimeType("text/xml");
        expectedCcdDocumentInfoDto.setCreatedOn(null);
        expectedCcdDocumentInfoDto.setSizeKb(null);
        expectedCcdDocumentInfoDto.setDataSource("Merged data");
        final DocumentInfoDto expectedFacesheetDocumentInfoDto = new DocumentInfoDto();
        expectedFacesheetDocumentInfoDto.setId(null);
        expectedFacesheetDocumentInfoDto.setHash(null);
        expectedFacesheetDocumentInfoDto.setType("Facesheet");
        expectedFacesheetDocumentInfoDto.setTitle("FACESHEET");
        expectedFacesheetDocumentInfoDto.setExtension("pdf");
        expectedFacesheetDocumentInfoDto.setMimeType("application/pdf");
        expectedFacesheetDocumentInfoDto.setCreatedOn(null);
        expectedFacesheetDocumentInfoDto.setSizeKb(null);
        expectedFacesheetDocumentInfoDto.setDataSource("Merged data");

        // Mockito expectations
        when(documentDao.queryForDocuments(allResidents, provider.getEmployee())).thenReturn(Collections.singletonList(document));
        when(documentDao.queryForDocumentsByResidentIdIn(allResidentIds, provider.getEmployee(), null)).thenReturn(Collections.singletonList(document));

        // Execute the method being tested
        List<DocumentInfoDto> result = documentService.getDocumentsForUser(consumerId, null);

        // Validation
        assertThat(result, hasSize(3));
        assertThat(result.get(0), sameBeanAs(expectedCcdDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(1), sameBeanAs(expectedFacesheetDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        assertThat(result.get(2), sameBeanAs(expectedDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testGetDocumentCdaViewable() throws Exception {
        testGetDocument(true);
    }

    @Test
    public void testGetDocumentNotCdaViewable() throws Exception {
        testGetDocument(false);
    }

    private void testGetDocument(boolean isCda) {
        // Expected objects
        final Date creationTime = TestDataGenerator.randomDate();

        final Database database = new Database();
        database.setAlternativeId("test alternative id");
        resident.setDatabase(database);
        resident.setLegacyId("test legacy id");
        final Document document = new Document();
        document.setId(documentId);
        document.setHash("hash");
        document.setSize(102400);
        document.setCreationTime(creationTime);
        document.setDocumentTitle("title.png");
        document.setOriginalFileName("title.png");
        document.setMimeType("application/png");
        document.setVisible(Boolean.TRUE);
        document.setResidentDatabaseAlternativeId("test alternative id");
        document.setResidentLegacyId("test legacy id");
        document.setIsCDA(isCda);

        final DateDto dateDto = new DateDto();
        dateDto.setDateTime(creationTime.getTime());
        final DocumentInfoDto expectedDocumentInfoDto = new DocumentInfoDto();
        expectedDocumentInfoDto.setId(documentId);
        expectedDocumentInfoDto.setHash("hash");
        expectedDocumentInfoDto.setType(isCda ? "Ccd" : "Custom");
        expectedDocumentInfoDto.setTitle("title");
        expectedDocumentInfoDto.setExtension("png");
        expectedDocumentInfoDto.setMimeType("application/png");
        expectedDocumentInfoDto.setCreatedOn(dateDto);
        expectedDocumentInfoDto.setSizeKb(MathUtils.round(102400 / 1024.0, 2));
        expectedDocumentInfoDto.setDataSource("test alternative id");
        expectedDocumentInfoDto.setIsCdaViewable(isCda);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(documentDao.findDocument(documentId)).thenReturn(document);
        when(residentDao.getResidentId(document.getResidentDatabaseAlternativeId(), document.getResidentLegacyId())).thenReturn(residentId);

        // Execute the method being tested
        DocumentInfoDto result = documentService.getDocument(userId, documentId);

        // Validation
        assertThat(result, sameBeanAs(expectedDocumentInfoDto)
                .ignoring("createdOn.dateTimeStr"));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
    }

    @Test(expected = PhrException.class)
    public void testGetDocumentThrowsNotFound() {
        // Expected objects
        final Date creationTime = TestDataGenerator.randomDate();

        final Database database = new Database();
        database.setAlternativeId("test alternative id");
        resident2.setDatabase(database);
        final Document document = new Document();
        document.setId(documentId);
        document.setHash("hash");
        document.setSize(102400);
        document.setCreationTime(creationTime);
        document.setDocumentTitle("title.png");
        document.setOriginalFileName("title.png");
        document.setMimeType("application/png");
        document.setVisible(Boolean.TRUE);
        document.setResidentDatabaseAlternativeId("test alternative id");

        final DateDto dateDto = new DateDto();
        dateDto.setDateTime(creationTime.getTime());

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(documentDao.findDocument(documentId)).thenReturn(document);

        // Execute the method being tested
        documentService.getDocument(userId, documentId);
    }

    @Test
    public void testDownloadCustomDocument() {
        // Expected objects
        final DocumentBean document = new DocumentBean();
        document.setId(documentId);
        document.setResidentId(residentId);

        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(documentFacade.findDocument(documentId)).thenReturn(document);

        // Execute the method being tested
        documentService.downloadCustomDocument(userId, documentId, response);

        // Validation
        verify(documentFacade).downloadOrViewCustomDocument(document, response, false);
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testDownloadCustomDocumentForReceiver() throws Exception {
        // Expected objects
        final DocumentBean document = new DocumentBean();
        document.setId(documentId);
        document.setResidentId(residentId);

        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectation
        final ResidentCareTeamMember careTeamMember = setUpMockitoExpectationsForReceiver(careReceiverId, true);
        when(documentFacade.findDocument(documentId)).thenReturn(document);

        // Execute the method being tested
        documentService.downloadCustomDocumentForReceiver(careReceiverId, documentId, response);

        // Validation
        verify(documentFacade).downloadOrViewCustomDocument(document, response, false);
        verify(careTeamSecurityUtils).checkAccessToCareTeamMemberOrThrow(careTeamMember, AccessRight.Code.MY_PHR);
    }

    @Test(expected = PhrException.class)
    public void testDownloadCustomDocumentThrowsNotFound() {
        // Expected objects
        final DocumentBean document = new DocumentBean();
        document.setId(documentId);
        document.setResidentId(residentId2);

        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(documentFacade.findDocument(documentId)).thenReturn(document);

        // Execute the method being tested
        documentService.downloadCustomDocument(userId, documentId, response);
    }

    @Test(expected = PhrException.class)
    public void testDownloadCustomDocumentForReceiverThrowsNotFound() throws Exception {
        // Expected objects
        final DocumentBean document = new DocumentBean();
        document.setId(documentId);


        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectations
        final ResidentCareTeamMember careTeamMember = setUpMockitoExpectationsForReceiver(careReceiverId, true);
        document.setResidentId(TestDataGenerator.randomIdExceptOf(careTeamMember.getResidentId()));
        when(documentFacade.findDocument(documentId)).thenReturn(document);

        // Execute the method being tested
        documentService.downloadCustomDocumentForReceiver(careReceiverId, documentId, response);
    }

    @Test(expected = PhrException.class)
    public void testDownloadCustomDocumentThrowsNotFound2() {
        // Expected objects
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final DocumentNotFoundException exception = new DocumentNotFoundException(documentId);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(documentFacade.findDocument(documentId)).thenThrow(exception);

        System.out.println("The below DocumentNotFoundException is expected.");

        // Execute the method being tested
        documentService.downloadCustomDocument(userId, documentId, response);
    }

    @Test(expected = PhrException.class)
    public void testDownloadCustomDocumentForReceiverThrowsNotFound2() throws Exception {
        // Expected objects
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final DocumentNotFoundException exception = new DocumentNotFoundException(documentId);

        // Mockito expectations
        setUpMockitoExpectationsForReceiver(careReceiverId, true);
        when(documentFacade.findDocument(documentId)).thenThrow(exception);

        System.out.println("The below DocumentNotFoundException is expected.");

        // Execute the method being tested
        documentService.downloadCustomDocumentForReceiver(careReceiverId, documentId, response);
    }

    @Test
    public void testDownloadContinuityOfCareDocument() {
        // Expected objects
        final DocumentBean document = new DocumentBean();
        document.setId(documentId);

        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectations
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        documentService.downloadContinuityOfCareDocument(userId, response);

        // Validation
        verify(documentFacade).downloadOrViewReport(residentId, activeResidentIds, "ccd", response, false);
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testDownloadContinuityOfCareDocumentForReceiver() throws Exception {
        // Expected objects
        final DocumentBean document = new DocumentBean();
        document.setId(documentId);

        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectations
        final ResidentCareTeamMember careTeamMember = setUpMockitoExpectationsForReceiver(careReceiverId, true);

        // Execute the method being tested
        documentService.downloadContinuityOfCareDocumentForReceiver(careReceiverId, response);

        // Validation
        verify(documentFacade).downloadOrViewReport(residentId, allResidentIds, "ccd", response, false);
        verify(careTeamSecurityUtils).checkAccessToCareTeamMemberOrThrow(careTeamMember, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testDownloadFacesheetReport() {
        // Expected objects
        final DocumentBean document = new DocumentBean();
        document.setId(documentId);

        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectations
        final User currentUser = setUpMockitoExpectations(userId);

        // Execute the method being tested
        documentService.downloadFacesheetReport(userId, response);

        // Validation
        verify(documentFacade).downloadOrViewReport(residentId, activeResidentIds, "facesheet", response, false, -currentUser.getTimeZoneOffset());
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
    }

    @Test
    public void testDownloadFacesheetReportForReceiver() throws Exception {
        // Expected objects
        final DocumentBean document = new DocumentBean();
        document.setId(documentId);

        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Mockito expectations
        final ResidentCareTeamMember careTeamMember = setUpMockitoExpectationsForReceiver(careReceiverId, true);

        // Execute the method being tested
        documentService.downloadFacesheetReportForReceiver(careReceiverId, response);

        // Validation
        verify(documentFacade).downloadOrViewReport(residentId, allResidentIds, "facesheet", response, false);
        verify(careTeamSecurityUtils).checkAccessToCareTeamMemberOrThrow(careTeamMember, AccessRight.Code.MY_PHR);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme