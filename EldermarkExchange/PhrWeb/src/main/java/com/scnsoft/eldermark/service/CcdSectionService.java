package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.carecoordination.NoteDao;
import com.scnsoft.eldermark.dao.healthdata.*;
import com.scnsoft.eldermark.dao.healthdata.AdvanceDirectiveDao;
import com.scnsoft.eldermark.dao.healthdata.AllergyObservationDao;
import com.scnsoft.eldermark.dao.healthdata.FamilyHistoryDao;
import com.scnsoft.eldermark.dao.healthdata.EncounterDao;
import com.scnsoft.eldermark.dao.healthdata.ImmunizationDao;
import com.scnsoft.eldermark.dao.healthdata.MedicalEquipmentDao;
import com.scnsoft.eldermark.dao.healthdata.MedicationDao;
import com.scnsoft.eldermark.dao.healthdata.PlanOfCareActivityDao;
import com.scnsoft.eldermark.dao.healthdata.VitalSignObservationDao;
import com.scnsoft.eldermark.dao.phr.SectionUpdateRequestDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.*;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.Section;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author phomal
 * Created on 5/17/2017.
 */
@Service
@Transactional
public class CcdSectionService extends BasePhrService {

    Logger logger = Logger.getLogger(CcdSectionService.class.getName());

    @Autowired
    ImmunizationDao immunizationDao;

    @Autowired
    ProblemObservationDao problemObservationDao;

    @Autowired
    MedicationDao medicationDao;

    @Autowired
    AllergyObservationDao allergyObservationDao;

    @Autowired
    VitalSignObservationDao vitalSignObservationDao;

    @Autowired
    PolicyActivityDao policyActivityDao;

    @Autowired
    DocumentDao documentDao;

    @Autowired
    ResidentDao residentDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    UserResidentRecordsDao userResidentRecordsDao;

    @Autowired
    SectionUpdateRequestDao sectionUpdateRequestDao;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private NotificationsFacade notificationsFacade;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private ProcedureActivityDao procedureActivityDao;

    @Autowired
    private AdvanceDirectiveDao advanceDirectiveDao;

    @Autowired
    private EncounterDao encounterDao;

    @Autowired
    private ResultObservationDao resultObservationDao;

    @Autowired
    private PregnancyObservationDao pregnancyObservationDao;

    @Autowired
    private SmokingStatusObservationDao smokingStatusObservationDao;

    @Autowired
    private SocialHistoryObservationDao socialHistoryObservationDao;

    @Autowired
    private TobaccoUseDao tobaccoUseDao;

    @Autowired
    private FamilyHistoryDao familyHistoryDao;

    @Autowired
    private MedicalEquipmentDao medicalEquipmentDao;

    @Autowired
    private PlanOfCareActivityDao planOfCareActivityDao;

    @Transactional(readOnly = true)
    public Map<Section, Long> getSectionsWithCount(Long userId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        boolean hasAccessToMedications = careTeamSecurityUtils.checkAccessToUserInfo(userId, AccessRight.Code.MEDICATIONS_LIST);

        final Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);

        final Long countProblems = problemObservationDao.countResidentProblemsWithoutDuplicates(activeResidentIds);
        Long countMedications = 0L;
        if (hasAccessToMedications) {
            countMedications = medicationDao.countResidentMedicationsWithoutDuplicates(activeResidentIds);
        }
        final Long countAllergies = allergyObservationDao.countResidentAllergiesWithoutDuplicates(activeResidentIds);
        final Long countImmunizations = immunizationDao.countResidentImmunizationsWithoutDuplicates(activeResidentIds);
        final Long countVitalSigns = vitalSignObservationDao.countResidentVitalSignObservationsWithoutDuplicates(activeResidentIds, VitalSignType.supportedCodes());
        final Long countPayers = policyActivityDao.countResidentPolicyActivitiesWithoutDuplicates(activeResidentIds);

        final Long countNotes = noteDao.countByResident_IdInAndArchivedIsFalse(activeResidentIds);
        final Long countProcedures = procedureActivityDao.countResidentProcedureActivitiesWithoutDuplicates(activeResidentIds);
        final Long countFamilyHistory = familyHistoryDao.countResidentFamilyHistoryWithoutDuplicates(activeResidentIds);

        final Long socialHistoryCount = pregnancyObservationDao.countResidentsPregnancyObservationsWithoutDuplicates(activeResidentIds) +
                smokingStatusObservationDao.countResidentsSmokingStatusObservationsWithoutDuplicates(activeResidentIds) +
                socialHistoryObservationDao.countResidentsSocialHistoryObservationsWithoutDuplicates(activeResidentIds) +
                tobaccoUseDao.countResidentsTobaccoUseWithoutDuplicates(activeResidentIds);

        Employee requestingEmployee = null;
        if (!PhrSecurityUtils.checkAccessToUserInfo(userId)) {
            Long currentUserId = PhrSecurityUtils.getCurrentUserId();
            Long employeeId = getEmployeeIdOrThrow(currentUserId);
            requestingEmployee = employeeDao.get(employeeId);
        }
        final Long countDocs = documentDao.countDocumentsByResidentIdIn(activeResidentIds, requestingEmployee) + 2;

        final Map<Section, Long> sectionToCountMap = new HashMap<>();
        sectionToCountMap.put(Section.PROBLEMS, countProblems);
        if (hasAccessToMedications) {
            sectionToCountMap.put(Section.MEDICATIONS, countMedications);
        }
        sectionToCountMap.put(Section.ALLERGIES, countAllergies);
        sectionToCountMap.put(Section.IMMUNIZATIONS, countImmunizations);
        sectionToCountMap.put(Section.VITAL_SIGNS, countVitalSigns);
        sectionToCountMap.put(Section.PAYERS, countPayers);
        sectionToCountMap.put(Section.DOCUMENTS, countDocs);
        sectionToCountMap.put(Section.NOTES, countNotes);
        sectionToCountMap.put(Section.PROCEDURES, countProcedures);
        sectionToCountMap.put(Section.ADVANCED_DIRECTIVES, advanceDirectiveDao.countResidentAdvanceDirectivesWithoutDuplicates(activeResidentIds));
        sectionToCountMap.put(Section.ENCOUNTERS, encounterDao.countResidentEncountersWithoutDuplicates(activeResidentIds));
        sectionToCountMap.put(Section.RESULTS, resultObservationDao.countResidentResultsWithoutDuplicates(activeResidentIds));
        sectionToCountMap.put(Section.SOCIAL_HISTORY, socialHistoryCount);
        sectionToCountMap.put(Section.FAMILY_HISTORY, countFamilyHistory);
        sectionToCountMap.put(Section.MEDICAL_EQUIPMENT, medicalEquipmentDao.countResidentsMedicalEquipmentWithoutDuplicates(activeResidentIds));
        sectionToCountMap.put(Section.PLAN_OF_CARE, planOfCareActivityDao.countResidentPlanOfCareActivityWithoutDuplicates(activeResidentIds));

        return sectionToCountMap;
    }

    public SectionUpdateRequest createUpdateRequest(Long userId, Section section, String comment, Boolean sendToAll,
                                                    SectionUpdateRequest.Type requestType, List<MultipartFile> files) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        if (Section.MEDICATIONS.equals(section)) {
            careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MEDICATIONS_LIST);
        }

        List<com.scnsoft.eldermark.entity.phr.UserResidentRecord> records;
        if (Boolean.TRUE.equals(sendToAll)) {
            records = userResidentRecordsDao.getByUserId(userId);
        } else {
            records = Collections.singletonList(userResidentRecordsDao.getActiveByUserId(userId).get(0));
        }
        if (CollectionUtils.isEmpty(records)) {
            throw new PhrException(PhrExceptionType.NOT_FOUND_PATIENT_INFO);
        }
        if (CollectionUtils.isEmpty(files)) {
            files = new ArrayList<>();
        }

        SectionUpdateRequest sectionUpdateRequest = new SectionUpdateRequest();
        sectionUpdateRequest.setSection(String.valueOf(section));
        sectionUpdateRequest.setRequestType(requestType);
        sectionUpdateRequest.setComment(comment);
        sectionUpdateRequest.setSendToAll(sendToAll);
        sectionUpdateRequest.setPatientId(userId);
        sectionUpdateRequest.setCreatedById(PhrSecurityUtils.getCurrentUserId());

        Set<Organization> organizations = new HashSet<>();
        for (com.scnsoft.eldermark.entity.phr.UserResidentRecord record : records) {
            organizations.add(record.getOrganization());
        }
        sectionUpdateRequest.setAddressees(organizations);

        Set<SectionUpdateRequestFile> attachments = new HashSet<>(files.size());
        for (MultipartFile file : files) {
            if (!isAllowed(file.getContentType())) {
                throw new PhrException(PhrExceptionType.ATTACHMENT_TYPE_IS_NOT_ALLOWED);
            }
            SectionUpdateRequestFile sectionUpdateRequestFile = new SectionUpdateRequestFile();
            sectionUpdateRequestFile.setSectionUpdateRequest(sectionUpdateRequest);
            sectionUpdateRequestFile.setOriginalName(file.getOriginalFilename());
            sectionUpdateRequestFile.setContentType(file.getContentType());
            try {
                sectionUpdateRequestFile.setFile(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                throw new PhrException(e.getMessage());
            }
            attachments.add(sectionUpdateRequestFile);
        }
        sectionUpdateRequest.setAttachments(attachments);
        sectionUpdateRequest = sectionUpdateRequestDao.save(sectionUpdateRequest);

        // send notification(s) to health data provider(s)
        String creatorName = getCurrentUserFullName(userId);
        for (UserResidentRecord record : records) {
            String email = record.getOrganization().getEmail();
            if (StringUtils.isBlank(email)) {
                logger.warning("Can't send email. Provider organization (id=" + record.getProviderId() + ") contact email is missing.");
                continue;
            }
            Resident resident = residentDao.get(record.getResidentId());

            notificationsFacade.sendSectionUpdateRequest(email, creatorName, resident.getFullName(), section, comment, requestType, files);
        }

        return sectionUpdateRequest;
    }

    private boolean isAllowed(String contentType) {
        final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
                "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/pdf",
                "text/plain", "text/xml", "application/plain", "application/xml",
                "image/jpeg", "image/pjpeg", "image/png");
        return ALLOWED_CONTENT_TYPES.contains(StringUtils.lowerCase(contentType));
    }

    private String getCurrentUserFullName(Long userId) {
        if (PhrSecurityUtils.checkAccessToUserInfo(userId)) {
            Long residentId = getResidentIdOrThrow(userId);
            Resident resident = residentDao.get(residentId);
            return resident.getFullName();
        } else {
            Long employeeId = getEmployeeIdOrThrow(PhrSecurityUtils.getCurrentUserId());
            Employee employee = employeeDao.get(employeeId);
            return employee.getFullName();
        }
    }

}
