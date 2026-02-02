package com.scnsoft.eldermark.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scnsoft.eldermark.beans.ClientAssessmentCount;
import com.scnsoft.eldermark.beans.projection.AssessmentStatusAware;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;
import com.scnsoft.eldermark.beans.reports.model.NorCalComprehensiveAssessmentHouseHoldMembers;
import com.scnsoft.eldermark.beans.reports.model.assessment.EmergencyContactsAware;
import com.scnsoft.eldermark.beans.reports.model.assessment.hmis.HmisAdultChildIntakeAssessment;
import com.scnsoft.eldermark.beans.reports.model.assessment.NorCalComprehensiveAssessment;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientAssessmentResultSecurityAwareEntity;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.dao.ClientComprehensiveAssessmentDao;
import com.scnsoft.eldermark.dao.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.basic.BaseAuditableService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.*;

@Service
public class ClientAssessmentResultServiceImpl extends BaseAuditableService<ClientAssessmentResult> implements ClientAssessmentResultService {

    private static final String COMPREHENSIVE_ASSESSMENT_CODE = "COMPREHENSIVE";

    private static final Logger logger = LoggerFactory.getLogger(ClientAssessmentResultServiceImpl.class);

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ClientComprehensiveAssessmentDao clientComprehensiveAssessmentDao;

    @Autowired
    private ClientAssessmentResultSpecificationGenerator specificationGenerator;

    @Autowired
    private ClientAssessmentNotificationService clientAssessmentNotificationService;

    @Autowired
    private AssessmentScoringService assessmentScoringService;

    @Autowired
    private ObjectMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Long count(PermissionFilter permissionFilter, Long clientId) {
        var byFilter = specificationGenerator.byFilter(clientId, null);
        var hasAccess = specificationGenerator.hasAccess(permissionFilter);
        //don't apply type restrictions because we display all types which client already have

        return clientAssessmentDao.count(byFilter.and(hasAccess));
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(PermissionFilter permissionFilter) {
        var unarchived = specificationGenerator.isUnarchived();
        var hasAccess = specificationGenerator.hasAccess(permissionFilter);
        //don't apply type restrictions because we display all types which client already have

        return clientAssessmentDao.count(unarchived.and(hasAccess));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientAssessmentCount> countGroupedByStatus(Long clientId, PermissionFilter permissionFilter) {
        var byFilter = specificationGenerator.byFilter(clientId, null);
        var hasAccess = specificationGenerator.hasAccess(permissionFilter);
        //don't apply type restrictions because we display all types which client already have
        var typeEnabled = specificationGenerator.isAssessmentTypeEnabled();

        return clientAssessmentDao.countGroupedByStatus(byFilter.and(hasAccess));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientAssessmentCount> countGroupedByStatus(PermissionFilter permissionFilter) {
        var unarchived = specificationGenerator.isUnarchived();
        var hasAccess = specificationGenerator.hasAccess(permissionFilter);
        //don't apply type restrictions because we display all types which client already have
        var typeEnabled = specificationGenerator.isAssessmentTypeEnabled();

        return clientAssessmentDao.countGroupedByStatus(unarchived.and(hasAccess));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ClientAssessmentResult> find(Long clientId, String searchString, PermissionFilter permissionFilter, Pageable pageable) {
        var byFilter = specificationGenerator.byFilter(clientId, searchString);
        var hasAccess = specificationGenerator.hasAccess(permissionFilter);
        //don't apply type restrictions because we display all types which client already have

        return clientAssessmentDao.findAll(byFilter.and(hasAccess), pageable);
    }

    @Override
    public Page<ClientAssessmentResult> findHistoryById(Long assessmentResultId, Pageable pageRequest) {
        var historyById = specificationGenerator.historyById(assessmentResultId);
        return clientAssessmentDao.findAll(historyById, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientAssessmentResult findById(Long assessmentResultId) {
        return clientAssessmentDao.findById(assessmentResultId)
            .map(this::updateComprehensiveAssessmentResultDataFromDb)
            .orElseThrow();
    }

    private ClientAssessmentResult updateComprehensiveAssessmentResultDataFromDb(ClientAssessmentResult assessmentResult) {
        if (COMPREHENSIVE_ASSESSMENT_CODE.equals(assessmentResult.getAssessment().getCode())) {
            clientComprehensiveAssessmentDao.findByClientAssessmentResult_Id(assessmentResult.getId())
                .ifPresent(cca -> {
                    try {
                        var json = mapper.readTree(assessmentResult.getResult());
                        if (json instanceof ObjectNode) {
                            var ca = (ObjectNode) json;
                            ca.put(PRIMARY_CARE_PHYSICIAN_FIRST_NAME, cca.getPrimaryCarePhysicianFirstName());
                            ca.put(PRIMARY_CARE_PHYSICIAN_LAST_NAME, cca.getPrimaryCarePhysicianLastName());
                            ca.put(PHARMACY_NAME, cca.getPharmacyName());
                            assessmentResult.setResult(mapper.writeValueAsString(ca));
                        } else {
                            throw new IOException("Unexpected json format");
                        }
                    } catch (IOException e) {
                        logger.error("Error parsing comprehensive json result : {}", assessmentResult.getResult(), e);
                    }
                });
        }
        return assessmentResult;
    }

    @Override
    @Transactional
    public ClientAssessmentResult save(ClientAssessmentResult entity) {
        return clientAssessmentDao.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void createEventForAssessmentWithRiskIdentified(Long assessmentResultId, Long previousAssessmentResultId) {
        ClientAssessmentResult clientAssessmentResult = clientAssessmentDao.findById(assessmentResultId).orElseThrow();
        Assessment assessment = clientAssessmentResult.getAssessment();
        if (BooleanUtils.isTrue(assessment.getEventsPreferences().getSendEvents())) {
            var scoreWithNegativeAnswers = assessmentScoringService.calculateScoreWithPositiveScoringAnswers(clientAssessmentResult);
            if (assessmentScoringService.isRiskIdentified(assessment.getId(), scoreWithNegativeAnswers.getFirst())) {
                var scoreWithNewNegativeAnswers = scoreWithNegativeAnswers;
                if  (previousAssessmentResultId != null) {
                    var previousAssessmentResult = clientAssessmentDao.findById(previousAssessmentResultId).orElseThrow();
                    var previousScoreWithNegativeAnswers = assessmentScoringService.calculateScoreWithPositiveScoringAnswers(previousAssessmentResult);
                    var previousNegativeQuestionsWithAnswers = previousScoreWithNegativeAnswers.getSecond();
                    scoreWithNewNegativeAnswers.setSecond(
                            scoreWithNegativeAnswers.getSecond().entrySet().stream()
                                .filter(entry -> !previousNegativeQuestionsWithAnswers.containsKey(entry.getKey()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    );
                }
                clientAssessmentNotificationService.createAssessmentEvent(clientAssessmentResult, scoreWithNegativeAnswers);
                clientAssessmentDao.save(clientAssessmentResult);
            }
        }
    }

    @Override
    @Transactional
    public void createEventNoteForAssessmentWithRiskIdentified(Long previousAssessmentResultId, Long assessmentResultId) {
        ClientAssessmentResult previousAssessmentResult = clientAssessmentDao.findById(previousAssessmentResultId).orElseThrow();
        Assessment assessment = previousAssessmentResult.getAssessment();
        if (BooleanUtils.isTrue(assessment.getEventsPreferences().getSendNotesOnNegativeUpdates())) {
            var previousScore = assessmentScoringService.calculateScore(previousAssessmentResult);
            if (assessmentScoringService.isRiskIdentified(assessment.getId(), previousScore)) {
                var event = previousAssessmentResult.getEvent();
                //TODO if event == null exception should be thrown. Not throwing for backward compatibility
                if (event != null) {
                    ClientAssessmentResult updatedAssessmentResult = clientAssessmentDao.findById(assessmentResultId).orElseThrow();
                    Long updatedScore = assessmentScoringService.calculateScore(updatedAssessmentResult);
                    String severity = assessmentScoringService.findSeverityOfScore(updatedAssessmentResult.getAssessment().getId(), updatedScore);
                    clientAssessmentNotificationService
                            .createAssessmentEventNote(previousAssessmentResult, updatedAssessmentResult, updatedScore, severity);
                } else {
                    logger.error("Event for risk identified assessment was not created. Id:" + previousAssessmentResult.getId());
                }

            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComprehensiveAssessment> findLatestNotEmptyInProgressOrCompletedComprehensiveByClientIdWithMerged(Long clientId, PermissionFilter permissionFilter, Predicate<ComprehensiveAssessment> notEmptyData) {

        var comprehensiveByMergedClients = specificationGenerator.comprehensiveOfMergedClients(clientId);
        return findLatestNotEmptyInProgressOrCompletedComprehensive(permissionFilter, notEmptyData, comprehensiveByMergedClients, ComprehensiveAssessment.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NorCalComprehensiveAssessmentHouseHoldMembers> findLatestNotEmptyInProgressOrCompletedNorCalComprehensiveHouseHoldMembersByClientIdWithMerged(
            Long clientId, PermissionFilter permissionFilter, Predicate<NorCalComprehensiveAssessmentHouseHoldMembers> notEmptyData
    ) {

        var comprehensiveByMergedClients = specificationGenerator.norCalComprehensiveOfMergedClients(clientId);
        return findLatestNotEmptyInProgressOrCompletedComprehensive(permissionFilter, notEmptyData, comprehensiveByMergedClients, NorCalComprehensiveAssessmentHouseHoldMembers.class);
    }


    private <T> Optional<T> findLatestNotEmptyInProgressOrCompletedComprehensive(PermissionFilter permissionFilter, Predicate<T> notEmptyData, Specification<ClientAssessmentResult> byTypeAndClientSpec, Class<T> parsedAssessmentClass) {
        var hasAccess = specificationGenerator.hasAccess(permissionFilter);
        var unarchived = specificationGenerator.isUnarchived();
        var typeEnabled = specificationGenerator.isAssessmentTypeEnabled();

        var spec = byTypeAndClientSpec
                .and(hasAccess)
                .and(unarchived)
                .and(typeEnabled);

        Function<ClientAssessmentResult, T> resultParser = clientAssessmentResult -> parseClientAssessmentResult(clientAssessmentResult, parsedAssessmentClass);

        return findLatestInProgressAndParse(spec, resultParser)
                .filter(notEmptyData != null ? notEmptyData : t -> true)
                .or(() -> findLatestCompletedAndParse(spec, resultParser)
                );
    }

    private <T> Optional<T> findLatestInProgressAndParse(Specification<ClientAssessmentResult> spec, Function<ClientAssessmentResult, T> resultParser) {
        return clientAssessmentDao.findAll(
                spec.and(specificationGenerator.inProgress()),
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, ClientAssessmentResult_.DATE_STARTED))
        )
                .get()
                .findFirst()
                .map(resultParser);
    }

    private <T> Optional<T> findLatestCompletedAndParse(Specification<ClientAssessmentResult> spec, Function<ClientAssessmentResult, T> resultParser) {
        return clientAssessmentDao.findAll(
                spec.and(specificationGenerator.completed()),
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, ClientAssessmentResult_.DATE_COMPLETED))
        )
                .get()
                .findFirst()
                .map(resultParser);
    }

    @Override
    public boolean hasPharmacyData(ComprehensiveAssessment assessment) {
        return !StringUtils.isAllBlank(assessment.getPharmacyName(), assessment.getPharmacyPhoneNumber(), assessment.getPharmacyAddressStreet(),
                assessment.getPharmacyAddressCity(), assessment.getPharmacyAddressState(), assessment.getPharmacyAddressZipCode());
    }

    @Override
    public <T extends EmergencyContactsAware> boolean hasEmergencyContactData(T assessment) {
        return Stream.of(assessment.getEmergencyContact1(), assessment.getEmergencyContact2(), assessment.getEmergencyContact3())
                .filter(Objects::nonNull)
                .anyMatch(a -> !StringUtils.isAllBlank(a.getFirstName(), a.getLastName(), a.getPhoneNumber(), a.getStreet(), a.getCity(), a.getState(), a.getZipCode()));
    }

    @Override
    public boolean hasMedicalContactData(ComprehensiveAssessment a) {
        return !StringUtils.isAllBlank(a.getPrimaryCarePhysicianFirstName(), a.getPrimaryCarePhysicianLastName(), a.getPrimaryCarePhysicianAddressStreet(),
                a.getPrimaryCarePhysicianAddressCity(), a.getPrimaryCarePhysicianAddressState(), a.getPrimaryCarePhysicianAddressZipCode()) ||
                !StringUtils.isAllBlank(a.getSpecialtyPhysicianFirstName(), a.getSpecialtyPhysicianLastName(), a.getSpecialtyPhysicianAddressStreet(),
                        a.getSpecialtyPhysicianAddressCity(), a.getSpecialtyPhysicianAddressState(), a.getSpecialtyPhysicianAddressZipCode());
    }

    private <T> T parseClientAssessmentResult(ClientAssessmentResult assessmentResult, Class<T> resultClass) {
        try {
            return mapper.readValue(assessmentResult.getResult(), resultClass);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing comprehensive json result : {}", assessmentResult.getResult(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsCreatedByAnyOfClient(Collection<Long> employeeIds, Long clientId) {
        var ofClient = specificationGenerator.ofClient(clientId);
        var createdByAny = specificationGenerator.createdByAny(employeeIds);

        return clientAssessmentDao.exists(ofClient.and(createdByAny));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientAssessmentResultSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return clientAssessmentDao.findById(id, ClientAssessmentResultSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientAssessmentResultSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return clientAssessmentDao.findByIdIn(ids, ClientAssessmentResultSecurityAwareEntity.class);
    }

    @Override
    @Transactional
    public Long hide(Long id, String comment) {
        var assessmentResult = findById(id);
        hideStatusValidation(assessmentResult);
        var clone = createClone(assessmentResult);
        clone.setComment(comment);
        clone.setAssessmentStatus(AssessmentStatus.HIDDEN);
        return updateAuditableEntity(clone);
    }

    @Override
    @Transactional
    public Long restore(Long id, String comment) {
        var assessmentResult = findById(id);
        restoreStatusValidation(assessmentResult);
        var clone = createClone(assessmentResult);
        clone.setAssessmentStatus(findLastNotHiddenStatusById(id).getAssessmentStatus());
        clone.setComment(comment);
        return updateAuditableEntity(clone);
    }

    private AssessmentStatusAware findLastNotHiddenStatusById(Long assessmentResultId) {
        return clientAssessmentDao.findFirst(
                specificationGenerator.historyById(assessmentResultId).and(specificationGenerator.notHidden()),
                AssessmentStatusAware.class, Sort.by(Sort.Direction.DESC, ClientAssessmentResult_.LAST_MODIFIED_DATE)).orElseThrow();

    }

    private void hideStatusValidation(ClientAssessmentResult assessmentResult) {
        if (assessmentResult.getAssessmentStatus().equals(AssessmentStatus.HIDDEN)) {
            throw new ValidationException("already hidden");
        }
    }

    private void restoreStatusValidation(ClientAssessmentResult assessmentResult) {
        if (!assessmentResult.getAssessmentStatus().equals(AssessmentStatus.HIDDEN)) {
            throw new ValidationException("already restored");
        }
    }


    private ClientAssessmentResult createClone(ClientAssessmentResult entity) {
        var clone = new ClientAssessmentResult();
        clone.setId(entity.getId());
        clone.setClientId(entity.getClientId());
        clone.setAssessment(entity.getAssessment());
        clone.setAssessmentId(entity.getAssessmentId());
        clone.setResult(entity.getResult());
        clone.setEmployee(entity.getEmployee());
        clone.setEmployeeId(entity.getEmployeeId());
        clone.setDateStarted(entity.getDateStarted());
        clone.setDateCompleted(entity.getDateCompleted());
        clone.setComment(entity.getComment());
        clone.setEvent(entity.getEvent());
        clone.setAssessmentStatus(entity.getAssessmentStatus());
        clone.setTimeToComplete(entity.getTimeToComplete());
        clone.setHasErrors(entity.getHasErrors());
        clone.setServicePlanNeedIdentificationExcludedQuestions(new HashSet<>(entity.getServicePlanNeedIdentificationExcludedQuestions()));
        clone.setServicePlanNeedIdentificationExcludedSections(new HashSet<>(entity.getServicePlanNeedIdentificationExcludedSections()));
        clone.setClientId(entity.getClientId());
        clone.setClient(entity.getClient());
        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return clientAssessmentDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientAssessmentDao.findByIdIn(ids, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasHouseholdMembersData(final NorCalComprehensiveAssessmentHouseHoldMembers assessment) {
        return Stream.of(
            assessment.getHouseholdMember1(),
            assessment.getHouseholdMember2(),
            assessment.getHouseholdMember3(),
            assessment.getHouseholdMember4(),
            assessment.getHouseholdMember5()
        )
            .filter(Objects::nonNull)
            .anyMatch(a -> !StringUtils.isBlank(a.getFirstName()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NorCalComprehensiveAssessment> findLatestInProgressOrCompletedNorCalComprehensiveByClientId(Long clientId, PermissionFilter permissionFilter) {
        var ofClient = specificationGenerator.ofClient(clientId);
        var byType = specificationGenerator.byType(Assessment.NOR_CAL_COMPREHENSIVE);
        return findLatestNotEmptyInProgressOrCompletedComprehensive(permissionFilter, null, ofClient.and(byType), NorCalComprehensiveAssessment.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NorCalComprehensiveAssessment> findLatestNotEmptyInProgressOrCompletedNorCalComprehensiveByClientIdWithMergedAndNotEmptyData(Long clientId, PermissionFilter permissionFilter, Predicate<NorCalComprehensiveAssessment> notEmptyData) {
        var comprehensiveByMergedClients = specificationGenerator.norCalComprehensiveOfMergedClients(clientId);
        return findLatestNotEmptyInProgressOrCompletedComprehensive(permissionFilter, notEmptyData, comprehensiveByMergedClients, NorCalComprehensiveAssessment.class);
    }

    @Override
    public Optional<HmisAdultChildIntakeAssessment> findHmisAdultChildIntakeAssessmentById(Long parentAssessmentResultId) {
        return clientAssessmentDao.findById(parentAssessmentResultId)
            .map(result->parseClientAssessmentResult(result, HmisAdultChildIntakeAssessment.class));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsInProcess(Long clientId, Long typeId) {
        var unarchived = specificationGenerator.isUnarchived();
        var byClient = specificationGenerator.ofClient(clientId);
        var byTypeId = specificationGenerator.byTypeId(typeId);
        var inProcess = specificationGenerator.inProgress();
        return clientAssessmentDao.exists(unarchived.and(byClient).and(byTypeId).and(inProcess));
    }

    @Override
    public <P> List<P> find(Specification<ClientAssessmentResult> specification, Class<P> projectionClass) {
        return clientAssessmentDao.findAll(specification, projectionClass);
    }
}
