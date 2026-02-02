package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.carecoordination.NoteDao;
import com.scnsoft.eldermark.dao.healthdata.AdvanceDirectiveDao;
import com.scnsoft.eldermark.dao.healthdata.AllergyObservationDao;
import com.scnsoft.eldermark.dao.healthdata.FamilyHistoryDao;
import com.scnsoft.eldermark.dao.healthdata.EncounterDao;
import com.scnsoft.eldermark.dao.healthdata.ImmunizationDao;
import com.scnsoft.eldermark.dao.healthdata.MedicalEquipmentDao;
import com.scnsoft.eldermark.dao.healthdata.MedicationDao;
import com.scnsoft.eldermark.dao.healthdata.*;
import com.scnsoft.eldermark.dao.healthdata.PlanOfCareActivityDao;
import com.scnsoft.eldermark.dao.healthdata.VitalSignObservationDao;
import com.scnsoft.eldermark.dao.phr.SectionUpdateRequestDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.phr.*;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.Section;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.scnsoft.eldermark.web.entity.Section.ALLERGIES;
import static com.scnsoft.eldermark.web.entity.Section.MEDICATIONS;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.collection.IsIn.isIn;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/14/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class CcdSectionServiceTest extends BaseServiceTest {

    @Mock
    private ImmunizationDao immunizationDao;

    @Mock
    private ProblemObservationDao problemObservationDao;

    @Mock
    private MedicationDao medicationDao;

    // Not used: replaced by AllergyObservationDao
    @Mock
    private AllergyDao allergyDao;

    @Mock
    private AllergyObservationDao allergyObservationDao;

    // Not used: replaced by VitalSignObservationDao
    @Mock
    private VitalSignDao vitalSignDao;

    @Mock
    private VitalSignObservationDao vitalSignObservationDao;

    @Mock
    private PolicyActivityDao policyActivityDao;

    @Mock
    private DocumentDao documentDao;

    @Mock
    private ResidentDao residentDao;

    @Mock
    private EmployeeDao employeeDao;

    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @Mock
    private SectionUpdateRequestDao sectionUpdateRequestDao;

    @Mock
    private NotificationsFacade notificationsFacade;

    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Mock
    private UserDao userDao;

    @Mock
    private NoteDao noteDao;

    @Mock
    private ProcedureActivityDao procedureActivityDao;

    @Mock
    private EncounterDao encounterDao;

    @Mock
    private AdvanceDirectiveDao advanceDirectiveDao;

    @Mock
    private PregnancyObservationDao pregnancyObservationDao;

    @Mock
    private SmokingStatusObservationDao smokingStatusObservationDao;

    @Mock
    private SocialHistoryObservationDao socialHistoryObservationDao;

    @Mock
    private TobaccoUseDao tobaccoUseDao;

    @Mock
    private ResultObservationDao resultObservationDao;

    @Mock
    private FamilyHistoryDao familyHistoryDao;

    @Mock
    private MedicalEquipmentDao medicalEquipmentDao;

    @Mock
    private PlanOfCareActivityDao planOfCareActivityDao;

    @InjectMocks
    private CcdSectionService ccdSectionService;

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\n\n",
                userId, residentId);
    }

    private void setUpMockitoExpectations(Long consumerUserId, Boolean hasAccessToMedications) {
        User currentUser = super.setUpMockitoExpectationsForCurrentUser(consumerUserId);

        when(careTeamSecurityUtils.checkAccessToUserInfo(consumerUserId, AccessRight.Code.MEDICATIONS_LIST)).thenReturn(hasAccessToMedications);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        when(residentDao.getResidents(activeResidentIds)).thenReturn(activeResidents);
        when(residentDao.getResidents(allResidentIds)).thenReturn(allResidents);
        when(userDao.findOne(userId)).thenReturn(currentUser);
        when(employeeDao.get(currentUser.getEmployeeId())).thenReturn(currentUser.getEmployee());

        when(immunizationDao.countByResidentIdIn(activeResidentIds)).thenReturn(3L);
        when(immunizationDao.countByResidentIdIn(allResidentIds)).thenReturn(4L);
        when(immunizationDao.countResidentImmunizationsWithoutDuplicates(activeResidentIds)).thenReturn(1L);
        when(immunizationDao.countResidentImmunizationsWithoutDuplicates(allResidentIds)).thenReturn(2L);
        when(problemObservationDao.countByProblemResidentIdIn(activeResidentIds)).thenReturn(5L);
        when(problemObservationDao.countByProblemResidentIdIn(allResidentIds)).thenReturn(6L);
        when(problemObservationDao.countResidentProblemsWithoutDuplicates(activeResidentIds)).thenReturn(3L);
        when(problemObservationDao.countResidentProblemsWithoutDuplicates(allResidentIds)).thenReturn(4L);
        when(medicationDao.countByResidentIdIn(activeResidentIds)).thenReturn(7L);
        when(medicationDao.countByResidentIdIn(allResidentIds)).thenReturn(8L);
        when(medicationDao.countResidentMedicationsWithoutDuplicates(activeResidentIds)).thenReturn(5L);
        when(medicationDao.countResidentMedicationsWithoutDuplicates(allResidentIds)).thenReturn(6L);
        when(vitalSignDao.countByResidentIds(activeResidentIds)).thenReturn(5L);    // intentional wrong value
        when(vitalSignDao.countByResidentIds(allResidentIds)).thenReturn(6L);       // intentional wrong value
        when(vitalSignObservationDao.countByVitalSignResidentId(residentId)).thenReturn(7L);
        when(vitalSignObservationDao.countResidentVitalSignObservationsWithoutDuplicates(activeResidentIds, VitalSignType.supportedCodes())).thenReturn(7L);
        when(vitalSignObservationDao.countResidentVitalSignObservationsWithoutDuplicates(allResidentIds, VitalSignType.supportedCodes())).thenReturn(8L);
        when(policyActivityDao.countByPayerResidentIdIn(activeResidentIds)).thenReturn(11L);
        when(policyActivityDao.countByPayerResidentIdIn(allResidentIds)).thenReturn(12L);
        when(policyActivityDao.countResidentPolicyActivitiesWithoutDuplicates(activeResidentIds)).thenReturn(9L);
        when(policyActivityDao.countResidentPolicyActivitiesWithoutDuplicates(allResidentIds)).thenReturn(10L);
        when(documentDao.countDocuments(activeResidents, currentUser.getEmployee())).thenReturn(11L);
        when(documentDao.countDocuments(allResidents, currentUser.getEmployee())).thenReturn(12L);
        when(documentDao.countDocumentsByResidentIdIn(activeResidentIds, currentUser.getEmployee())).thenReturn(11L);
        when(documentDao.countDocumentsByResidentIdIn(allResidentIds, currentUser.getEmployee())).thenReturn(12L);
        when(allergyDao.countByResidentIds(activeResidentIds)).thenReturn(15L);
        when(allergyDao.countByResidentIds(allResidentIds)).thenReturn(16L);
        when(allergyObservationDao.countByAllergyResidentIdIn(activeResidentIds)).thenReturn(17L);
        when(allergyObservationDao.countByAllergyResidentIdIn(allResidentIds)).thenReturn(18L);
        when(allergyObservationDao.countResidentAllergiesWithoutDuplicates(activeResidentIds)).thenReturn(15L);
        when(allergyObservationDao.countResidentAllergiesWithoutDuplicates(allResidentIds)).thenReturn(16L);
        when(noteDao.countByResident_IdInAndArchivedIsFalse(activeResidentIds)).thenReturn(20L);
        when(noteDao.countByResident_IdInAndArchivedIsFalse(allResidentIds)).thenReturn(21L);
        when(procedureActivityDao.countResidentProcedureActivitiesWithoutDuplicates(activeResidentIds)).thenReturn(41L);
        when(procedureActivityDao.countResidentProcedureActivitiesWithoutDuplicates(allResidentIds)).thenReturn(42L);
        when(encounterDao.countResidentEncountersWithoutDuplicates(activeResidentIds)).thenReturn(43L);
        when(encounterDao.countResidentEncountersWithoutDuplicates(allResidentIds)).thenReturn(44L);
        when(advanceDirectiveDao.countResidentAdvanceDirectivesWithoutDuplicates(activeResidentIds)).thenReturn(45L);
        when(advanceDirectiveDao.countResidentAdvanceDirectivesWithoutDuplicates(allResidentIds)).thenReturn(46L);
        when(resultObservationDao.countResidentResultsWithoutDuplicates(activeResidentIds)).thenReturn(47l);
        when(resultObservationDao.countResidentResultsWithoutDuplicates(allResidentIds)).thenReturn(48l);
        when(pregnancyObservationDao.countResidentsPregnancyObservationsWithoutDuplicates(activeResidentIds)).thenReturn(49l);
        when(pregnancyObservationDao.countResidentsPregnancyObservationsWithoutDuplicates(allResidentIds)).thenReturn(50l);
        when(smokingStatusObservationDao.countResidentsSmokingStatusObservationsWithoutDuplicates(activeResidentIds)).thenReturn(51l);
        when(smokingStatusObservationDao.countResidentsSmokingStatusObservationsWithoutDuplicates(allResidentIds)).thenReturn(52l);
        when(socialHistoryObservationDao.countResidentsSocialHistoryObservationsWithoutDuplicates(activeResidentIds)).thenReturn(53l);
        when(socialHistoryObservationDao.countResidentsSocialHistoryObservationsWithoutDuplicates(allResidentIds)).thenReturn(54l);
        when(tobaccoUseDao.countResidentsTobaccoUseWithoutDuplicates(activeResidentIds)).thenReturn(55l);
        when(tobaccoUseDao.countResidentsTobaccoUseWithoutDuplicates(allResidentIds)).thenReturn(56l);
        when(familyHistoryDao.countResidentFamilyHistoryWithoutDuplicates(activeResidentIds)).thenReturn(83L);
        when(familyHistoryDao.countResidentFamilyHistoryWithoutDuplicates(allResidentIds)).thenReturn(84L);
        when(medicalEquipmentDao.countResidentsMedicalEquipmentWithoutDuplicates(activeResidentIds)).thenReturn(57l);
        when(medicalEquipmentDao.countResidentsMedicalEquipmentWithoutDuplicates(allResidentIds)).thenReturn(58l);
        when(planOfCareActivityDao.countResidentPlanOfCareActivityWithoutDuplicates(activeResidentIds)).thenReturn(59l);
        when(planOfCareActivityDao.countResidentPlanOfCareActivityWithoutDuplicates(allResidentIds)).thenReturn(60l);
    }

    private void validateAccessRights(Long consumerUserId) {
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerUserId, AccessRight.Code.MY_PHR);
        verify(careTeamSecurityUtils).checkAccessToUserInfo(consumerUserId, AccessRight.Code.MEDICATIONS_LIST);
    }

    @Test
    public void testGetSectionsWithCountAsConsumer() {
        final Long consumerUserId = userId;

        // Expected object
        Map<Section, Long> expectedResult = new HashMap<>();
        expectedResult.put(Section.IMMUNIZATIONS, 1L);
        expectedResult.put(Section.PROBLEMS, 3L);
        expectedResult.put(Section.MEDICATIONS, 5L);
        expectedResult.put(Section.VITAL_SIGNS, 7L);
        expectedResult.put(Section.PAYERS, 9L);
        expectedResult.put(Section.DOCUMENTS, 11L + 2L);
        expectedResult.put(ALLERGIES, 15L);
        expectedResult.put(Section.NOTES, 20L);
        expectedResult.put(Section.PROCEDURES, 41L);
        expectedResult.put(Section.ENCOUNTERS, 43L);
        expectedResult.put(Section.ADVANCED_DIRECTIVES, 45L);
        expectedResult.put(Section.RESULTS, 47L);
        expectedResult.put(Section.SOCIAL_HISTORY, 49L + 51L + 53L + 55L);
        expectedResult.put(Section.FAMILY_HISTORY, 83L);
        expectedResult.put(Section.MEDICAL_EQUIPMENT, 57L);
        expectedResult.put(Section.PLAN_OF_CARE, 59L);

        // Mockito expectations
        setUpMockitoExpectations(consumerUserId, Boolean.TRUE);

        // Execute the method being tested
        Map<Section, Long> result = ccdSectionService.getSectionsWithCount(consumerUserId);

        // Validate
        assertThat(result.entrySet(), everyItem(isIn(expectedResult.entrySet())));
        assertThat(expectedResult.entrySet(), everyItem(isIn(result.entrySet())));
        verify(userResidentRecordsDao).getActiveResidentIdsByUserId(consumerUserId);
        //verify(residentDao).getResidents(activeResidentIds);
        verify(medicationDao).countResidentMedicationsWithoutDuplicates(activeResidentIds);
        verify(noteDao).countByResident_IdInAndArchivedIsFalse(activeResidentIds);
        verifyNoMoreInteractions(userResidentRecordsDao, residentDao);
        validateAccessRights(consumerUserId);
    }

    @Test
    public void testGetSectionsWithCountAsProvider() {
        final Long consumerUserId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected object
        Map<Section, Long> expectedResult = new HashMap<>();
        expectedResult.put(Section.IMMUNIZATIONS, 2L);
        expectedResult.put(Section.PROBLEMS, 4L);
        expectedResult.put(Section.MEDICATIONS, 6L);
        expectedResult.put(Section.VITAL_SIGNS, 8L);
        expectedResult.put(Section.PAYERS, 10L);
        expectedResult.put(Section.DOCUMENTS, 12L + 2L);
        expectedResult.put(ALLERGIES, 16L);
        expectedResult.put(Section.NOTES, 21L);
        expectedResult.put(Section.PROCEDURES, 42L);
        expectedResult.put(Section.ENCOUNTERS, 44L);
        expectedResult.put(Section.ADVANCED_DIRECTIVES, 46L);
        expectedResult.put(Section.RESULTS, 48L);
        expectedResult.put(Section.SOCIAL_HISTORY, 50L + 52L + 54L + 56L);
        expectedResult.put(Section.FAMILY_HISTORY, 84L);
        expectedResult.put(Section.MEDICAL_EQUIPMENT, 58L);
        expectedResult.put(Section.PLAN_OF_CARE, 60L);

        // Mockito expectations
        setUpMockitoExpectations(consumerUserId, Boolean.TRUE);

        // Execute the method being tested
        Map<Section, Long> result = ccdSectionService.getSectionsWithCount(consumerUserId);

        // Validate
        assertThat(result.entrySet(), everyItem(isIn(expectedResult.entrySet())));
        assertThat(expectedResult.entrySet(), everyItem(isIn(result.entrySet())));
        verify(userResidentRecordsDao).getAllResidentIdsByUserId(consumerUserId);
        //verify(residentDao).getResidents(allResidentIds);
        verify(medicationDao).countResidentMedicationsWithoutDuplicates(allResidentIds);
        verify(noteDao).countByResident_IdInAndArchivedIsFalse(allResidentIds);
        verifyNoMoreInteractions(userResidentRecordsDao, residentDao);
        validateAccessRights(consumerUserId);
    }

    @Test
    public void testGetSectionsWithCountAsProviderWithoutAccessToMedicationsList() {
        final Long consumerUserId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected object
        Map<Section, Long> expectedResult = new HashMap<>();
        expectedResult.put(Section.IMMUNIZATIONS, 2L);
        expectedResult.put(Section.PROBLEMS, 4L);
        expectedResult.put(Section.VITAL_SIGNS, 8L);
        expectedResult.put(Section.PAYERS, 10L);
        expectedResult.put(Section.DOCUMENTS, 12L + 2L);
        expectedResult.put(ALLERGIES, 16L);
        expectedResult.put(Section.NOTES, 21L);
        expectedResult.put(Section.PROCEDURES, 42L);
        expectedResult.put(Section.ENCOUNTERS, 44L);
        expectedResult.put(Section.ADVANCED_DIRECTIVES, 46L);
        expectedResult.put(Section.RESULTS, 48L);
        expectedResult.put(Section.SOCIAL_HISTORY, 50L + 52L + 54L + 56L);
        expectedResult.put(Section.FAMILY_HISTORY, 84L);
        expectedResult.put(Section.MEDICAL_EQUIPMENT, 58L);
        expectedResult.put(Section.PLAN_OF_CARE, 60L);


        // Mockito expectations
        setUpMockitoExpectations(consumerUserId, Boolean.FALSE);

        // Execute the method being tested
        Map<Section, Long> result = ccdSectionService.getSectionsWithCount(consumerUserId);

        // Validate
        assertThat(result.entrySet(), everyItem(isIn(expectedResult.entrySet())));
        assertThat(expectedResult.entrySet(), everyItem(isIn(result.entrySet())));
        verify(userResidentRecordsDao).getAllResidentIdsByUserId(consumerUserId);
        //verify(residentDao).getResidents(allResidentIds);
        verify(noteDao).countByResident_IdInAndArchivedIsFalse(allResidentIds);

        verifyZeroInteractions(medicationDao);
        verifyNoMoreInteractions(userResidentRecordsDao, residentDao);
        validateAccessRights(consumerUserId);
    }

    @Test
    public void testCreateUpdateRequest() {
        // Expected objects
        final String comment = "time to change";
        final SectionUpdateRequest.Type requestType = SectionUpdateRequest.Type.UPDATE;
        final Section section = ALLERGIES;

        final List<MultipartFile> files = new ArrayList<>();
        final byte[] content = "Hello World".getBytes();
        final String originalFilename = "original filename";
        final MultipartFile file = new MockMultipartFile("filename", originalFilename, ContentType.TEXT_PLAIN.getMimeType(), content);
        files.add(file);

        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final String email = TestDataGenerator.randomEmail();

        final Resident resident = new Resident();
        resident.setId(residentId);
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        final Organization organization = new Organization();
        organization.setEmail(email);
        final com.scnsoft.eldermark.entity.phr.UserResidentRecord userResidentRecord = new com.scnsoft.eldermark.entity.phr.UserResidentRecord();
        userResidentRecord.setResidentId(residentId);
        userResidentRecord.setUserId(userId);
        userResidentRecord.setOrganization(organization);
        final User currentUser = User.Builder.anUser()
                .withId(userId)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withResident(resident)
                .build();

        final SectionUpdateRequest expectedSectionUpdateRequest = new SectionUpdateRequest();
        expectedSectionUpdateRequest.setSection("ALLERGIES");
        expectedSectionUpdateRequest.setRequestType(requestType);
        expectedSectionUpdateRequest.setComment(comment);
        expectedSectionUpdateRequest.setSendToAll(Boolean.FALSE);
        expectedSectionUpdateRequest.setPatientId(userId);
        expectedSectionUpdateRequest.setCreatedById(userId);
        expectedSectionUpdateRequest.setAddressees(Collections.singleton(organization));
        final SectionUpdateRequestFile expectedFile = new SectionUpdateRequestFile();
        expectedFile.setFile(content);
        expectedFile.setOriginalName(originalFilename);
        expectedFile.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        expectedFile.setSectionUpdateRequest(expectedSectionUpdateRequest);
        expectedSectionUpdateRequest.setAttachments(Collections.singleton(expectedFile));

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId)).thenReturn(Collections.singletonList(userResidentRecord));
        when(userResidentRecordsDao.getActiveByUserId(userId)).thenReturn(Collections.singletonList(userResidentRecord));
        when(sectionUpdateRequestDao.save(any(SectionUpdateRequest.class))).then(returnsFirstArg());
        when(residentDao.get(residentId)).thenReturn(resident);
        when(userDao.findOne(userId)).thenReturn(currentUser);

        // Execute the method being tested
        SectionUpdateRequest result = ccdSectionService.createUpdateRequest(userId, section, comment, Boolean.FALSE, requestType, files);

        // Validation
        assertThat(result, sameBeanAs(expectedSectionUpdateRequest));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        verify(careTeamSecurityUtils, never()).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MEDICATIONS_LIST);
        verify(sectionUpdateRequestDao).save(result);
        verify(notificationsFacade).sendSectionUpdateRequest(email, currentUser.getFullName(), resident.getFullName(), section, comment, requestType, files);
        verifyNoMoreInteractions(sectionUpdateRequestDao, notificationsFacade);
    }

    @Test
    public void testCreateUpdateRequestMedicationsSendToAll() {
        // Expected objects
        final String comment = "time to change";
        final SectionUpdateRequest.Type requestType = SectionUpdateRequest.Type.UPDATE;
        final Section section = MEDICATIONS;

        final List<MultipartFile> files = new ArrayList<>();
        final byte[] content = "Hello World".getBytes();
        final String originalFilename = "original filename";
        final MultipartFile file = new MockMultipartFile("filename", originalFilename, ContentType.TEXT_PLAIN.getMimeType(), content);
        files.add(file);

        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final String email = TestDataGenerator.randomEmail();

        final Resident resident = new Resident();
        resident.setId(residentId);
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        final Organization organization = new Organization();
        organization.setEmail(email);
        final com.scnsoft.eldermark.entity.phr.UserResidentRecord userResidentRecord = new UserResidentRecord();
        userResidentRecord.setResidentId(residentId);
        userResidentRecord.setUserId(userId);
        userResidentRecord.setOrganization(organization);
        final User currentUser = User.Builder.anUser()
                .withId(userId)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withResident(resident)
                .build();

        final SectionUpdateRequest expectedSectionUpdateRequest = new SectionUpdateRequest();
        expectedSectionUpdateRequest.setSection("MEDICATIONS");
        expectedSectionUpdateRequest.setRequestType(requestType);
        expectedSectionUpdateRequest.setComment(comment);
        expectedSectionUpdateRequest.setSendToAll(Boolean.TRUE);
        expectedSectionUpdateRequest.setPatientId(userId);
        expectedSectionUpdateRequest.setCreatedById(userId);
        expectedSectionUpdateRequest.setAddressees(Collections.singleton(organization));
        final SectionUpdateRequestFile expectedFile = new SectionUpdateRequestFile();
        expectedFile.setFile(content);
        expectedFile.setOriginalName(originalFilename);
        expectedFile.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        expectedFile.setSectionUpdateRequest(expectedSectionUpdateRequest);
        expectedSectionUpdateRequest.setAttachments(Collections.singleton(expectedFile));

        // Mockito expectations
        when(userResidentRecordsDao.getByUserId(userId)).thenReturn(Collections.singletonList(userResidentRecord));
        when(userResidentRecordsDao.getActiveByUserId(userId)).thenReturn(Collections.singletonList(userResidentRecord));
        when(sectionUpdateRequestDao.save(any(SectionUpdateRequest.class))).then(returnsFirstArg());
        when(residentDao.get(residentId)).thenReturn(resident);
        when(userDao.findOne(userId)).thenReturn(currentUser);

        // Execute the method being tested
        SectionUpdateRequest result = ccdSectionService.createUpdateRequest(userId, section, comment, Boolean.TRUE, requestType, files);

        // Validation
        assertThat(result, sameBeanAs(expectedSectionUpdateRequest));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MEDICATIONS_LIST);
        verify(sectionUpdateRequestDao).save(result);
        verify(notificationsFacade).sendSectionUpdateRequest(email, currentUser.getFullName(), resident.getFullName(), section, comment, requestType, files);
        verifyNoMoreInteractions(sectionUpdateRequestDao, notificationsFacade);
    }

}
