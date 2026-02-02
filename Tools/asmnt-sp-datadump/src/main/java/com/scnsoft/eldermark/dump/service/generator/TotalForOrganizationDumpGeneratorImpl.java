package com.scnsoft.eldermark.dump.service.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dump.bean.ComprehensiveAssessment;
import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.dao.*;
import com.scnsoft.eldermark.dump.entity.DisplayableNamedEntity;
import com.scnsoft.eldermark.dump.entity.EventTypeEnum;
import com.scnsoft.eldermark.dump.entity.serviceplan.*;
import com.scnsoft.eldermark.dump.model.*;
import com.scnsoft.eldermark.dump.service.AssessmentScoringService;
import com.scnsoft.eldermark.dump.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.dump.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dump.specification.ServicePlanSpecificationGenerator;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class TotalForOrganizationDumpGeneratorImpl implements DumpGenerator {

    private static final List<EventTypeEnum> EVENT_TYPES = Arrays.asList(EventTypeEnum.ERV, EventTypeEnum.H);

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientSpecificationGenerator clientSpecifications;

    @Autowired
    private ClientAssessmentResultDao clientAssessmentResultDao;

    @Autowired
    private ClientAssessmentResultSpecificationGenerator assessmentResultSpecifications;

    @Autowired
    private AssessmentScoringService assessmentScoringService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ClientServicePlanDao clientServicePlanDao;

    @Autowired
    private ServicePlanSpecificationGenerator servicePlanSpecifications;

    @Autowired
    private ServicePlanNeedDao servicePlanNeedDao;

    @Autowired
    private ServicePlanGoalDao servicePlanGoalDao;

    @Autowired
    private EventDao eventdao;

    @Override
    public List<Dump> generateDump(DumpFilter filter) {
        var dump = new TotalForOrganizationDump();
        dump.setAssessmentsGeneral(assessmentsGeneral(filter));
        dump.setGad7PHQ9ScoringList(gad7PHQ9Scoring(filter));
        dump.setComprehensiveDetailList(comprehensiveDetailList(filter));
        dump.setSpGeneral(spGeneral(filter));
        dump.setSpIndividualList(spIndividualList(filter));
        dump.setSpDetailList(spDetailList(filter));
        return Collections.singletonList(dump);
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.TOTAL_FOR_ORGANIZATION;
    }

    private AssessmentsGeneral assessmentsGeneral(DumpFilter filter) {
        var assessmentsGeneral = new AssessmentsGeneral();
        assessmentsGeneral.setGad7Completed(clientAssessmentResultDao.count(assessmentResultSpecifications.gad7CompletedByOrganizationId(filter.getOrganizationId())));
        assessmentsGeneral.setPhq9Completed(clientAssessmentResultDao.count(assessmentResultSpecifications.phq9CompletedByOrganizationId(filter.getOrganizationId())));
        assessmentsGeneral.setComprehensiveCompleted(clientAssessmentResultDao.count(assessmentResultSpecifications.comprehensiveCompletedByOrganizationId(filter.getOrganizationId())));
        return assessmentsGeneral;
    }

    private List<GAD7PHQ9Scoring> gad7PHQ9Scoring(DumpFilter configuration) {
        return clientDao.findAll(clientSpecifications.byIds(configuration))
                .stream()
                .map(client -> {
                    var scoring = new GAD7PHQ9Scoring();
                    scoring.setResidentId(client.getId());
                    scoring.setFirstName(client.getFirstName());
                    scoring.setLastName(client.getLastName());
                    scoring.setOrganization(client.getOrganization().getName());
                    scoring.setCommunity(client.getCommunity().getName());
                    scoring.setGad7scores(
                            clientAssessmentResultDao.findAll(assessmentResultSpecifications.gad7OfClient(client))
                                    .stream()
                                    .map(assessmentScoringService::calculateScore)
                                    .collect(Collectors.toList()));

                    scoring.setPhq9Scores(
                            clientAssessmentResultDao.findAll(assessmentResultSpecifications.phq9OfClient(client))
                                    .stream()
                                    .map(assessmentScoringService::calculateScore)
                                    .collect(Collectors.toList())
                    );
                    scoring.setActive(client.isActive());
                    return scoring;
                })
                .collect(Collectors.toList());
    }


    private List<ComprehensiveDetail> comprehensiveDetailList(DumpFilter filter) {
        return clientAssessmentResultDao.findAll(assessmentResultSpecifications.comprehensiveCompleted(filter.getResidentIds()))
                .stream()
                .map(clientAssessmentResult -> {
                    var detail = new ComprehensiveDetail();
                    var comprehensiveParsed = parseComprehensive(clientAssessmentResult.getResult());

                    detail.setPatientName(clientAssessmentResult.getClient().getFullName());
                    detail.setCommunity(clientAssessmentResult.getClient().getCommunity().getName());
                    detail.setTimeToCompleteInMinutes(
                            dateDiff(clientAssessmentResult.getDateAssigned(), clientAssessmentResult.getDateCompleted(), ChronoUnit.MINUTES)
                    );

                    detail.setGenderFromClient(displayName(clientAssessmentResult.getClient().getGender()));
                    detail.setGenderFromAssessment(comprehensiveParsed.getGender());

                    detail.setRaceFromClient(displayName(clientAssessmentResult.getClient().getRace()));
                    detail.setRaceFromAssessment(comprehensiveParsed.getRace());
                    detail.setAge(dateDiff(clientAssessmentResult.getClient().getBirthDate(), LocalDate.now(), ChronoUnit.YEARS));


                    detail.setInsuranceNetwork(displayName(clientAssessmentResult.getClient().getInNetworkInsurance()));
                    detail.setInsurancePlan(clientAssessmentResult.getClient().getInsurancePlan());

                    return detail;
                })
                .collect(Collectors.toList());
    }

    private ComprehensiveAssessment parseComprehensive(String result) {
        try {
            return mapper.readerFor(ComprehensiveAssessment.class).readValue(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private SPGeneral spGeneral(DumpFilter filter) {
        var spGeneral = new SPGeneral();

        spGeneral.setNumberOfPatients(clientDao.count(clientSpecifications.byOrganizationId(filter.getOrganizationId())));

        spGeneral.setNumberOfSpOpened(
                clientServicePlanDao.count(
                        servicePlanSpecifications.byOrganizationIdAndStatus(filter.getOrganizationId(), ServicePlanStatus.IN_DEVELOPMENT)
                )
        );
        spGeneral.setNumberOfSpClosed(
                clientServicePlanDao.count(
                        servicePlanSpecifications.byOrganizationIdAndStatus(filter.getOrganizationId(), ServicePlanStatus.SHARED_WITH_CLIENT)
                )
        );

        spGeneral.setTotalNumberOfNeeds(
                servicePlanNeedDao.count(
                        servicePlanSpecifications.needsByOrganizationId(filter.getOrganizationId())
                )
        );
        spGeneral.setTotalNumberOfGoals(
                servicePlanGoalDao.count(
                        servicePlanSpecifications.goalsByOrganizationId(filter.getOrganizationId())
                )
        );
        spGeneral.setNumberOfAccomplishedGoals(
                servicePlanGoalDao.count(
                        servicePlanSpecifications.accomplishedGoalsByOrganizationId(filter.getOrganizationId())
                )
        );

        return spGeneral;
    }

    private Long dateDiff(Temporal first, Temporal second, TemporalUnit unit) {
        if (ObjectUtils.allNotNull(first, second)) {
            return unit.between(first, second);

        }
        return null;
    }

    private String displayName(DisplayableNamedEntity displayableNamedEntity) {
        if (displayableNamedEntity == null) {
            return null;
        }
        return displayableNamedEntity.getDisplayName();
    }

    private List<SPIndividual> spIndividualList(DumpFilter filter) {
        return clientServicePlanDao.findAll(servicePlanSpecifications.details(filter))
                .stream()
                .flatMap(this::toSpIndividualStream)
                .collect(Collectors.toList());
    }

    private Stream<SPIndividual> toSpIndividualStream(ClientServicePlan clientServicePlan) {
        var spIndividuals = new ArrayList<SPIndividual>();

        for (var baseNeed : clientServicePlan.getNeeds()) {
            if (baseNeed instanceof ServicePlanGoalNeed) {
                var need = (ServicePlanGoalNeed) baseNeed;
                for (var goal : need.getGoals()) {
                    var spIndividual = new SPIndividual();

                    spIndividual.setPatient(clientServicePlan.getClient().getFullName());
                    spIndividual.setCommunity(clientServicePlan.getClient().getCommunity().getName());
                    spIndividual.setResourceName(goal.getResourceName());
                    spIndividual.setScoreOfTiedToServicePlanNeed(resolveScore(clientServicePlan.getScoring(), need.getDomain()));
                    spIndividual.setDomain(need.getDomain().name());

                    spIndividuals.add(spIndividual);
                }
            }
        }

        return spIndividuals.stream();
    }

    private Integer resolveScore(ServicePlanScoring scoring, ServicePlanNeedType domain) {
        switch (domain) {
            case BEHAVIORAL:
                return scoring.getBehavioralScore();
            case SUPPORT:
                return scoring.getSupportScore();
            case HEALTH_STATUS:
                return scoring.getHealthStatusScore();
            case HOUSING:
                return scoring.getHousingScore();
            case NUTRITION_SECURITY:
                return scoring.getNutritionSecurityScore();
            case TRANSPORTATION:
                return scoring.getTransportationScore();
            case OTHER:
                return scoring.getOtherScore();

            default:
                return null;
        }
    }

    private List<SPDetails> spDetailList(DumpFilter filter) {
        return clientServicePlanDao.findAll(servicePlanSpecifications.details(filter))
                .stream()
                .map(servicePlan -> {
                    var spDetails = new SPDetails();
                    spDetails.setPatientName(servicePlan.getClient().getFullName());
                    spDetails.setCommunity(servicePlan.getClient().getCommunity().getName());
                    spDetails.setStatus(servicePlan.getServicePlanStatus().name());
                    spDetails.setDaysToComplete(dateDiff(servicePlan.getDateCreated(), servicePlan.getDateCompleted(), ChronoUnit.DAYS)); //todo check
                    spDetails.setEventsCount(eventdao.countAllByClientAndEventTypeCodeIn(servicePlan.getClient(), EVENT_TYPES));
                    spDetails.setGoalsCount(servicePlanGoalDao.count(servicePlanSpecifications.clientGoals(servicePlan.getClient())));
                    return spDetails;
                })
                .collect(Collectors.toList());
    }
}
