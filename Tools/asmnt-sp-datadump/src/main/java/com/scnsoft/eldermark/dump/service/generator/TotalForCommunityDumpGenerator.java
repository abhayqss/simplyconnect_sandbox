package com.scnsoft.eldermark.dump.service.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dump.bean.ComprehensiveAssessment;
import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.dao.*;
import com.scnsoft.eldermark.dump.entity.Client;
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

import static com.scnsoft.eldermark.dump.service.generator.DumpGeneratorUtils.displayName;

@Service
@Transactional(readOnly = true)
public class TotalForCommunityDumpGenerator implements DumpGenerator {

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

    @Autowired
    private CommunityDao communityDao;

    @Override
    public List<Dump> generateDump(DumpFilter filter) {
        var dump = new TotalForCommunityDump();
        dump.setCommunityAssessmentsGeneralList(communityAssessmentsGeneralList(filter));
        dump.setGad7PHQ9ScoringList(gad7PHQ9Scoring(filter));
        dump.setComprehensiveDetailList(comprehensiveDetailList(filter));
        dump.setCommunitySPGeneralList(communitySPGeneralList(filter));
        dump.setSpIndividualList(spIndividualList(filter));
        dump.setSpDetailList(spDetailList(filter));
        dump.setAssessedClientInsuranceInfoList(assessedClientInsuranceInfoList(filter));
        return Collections.singletonList(dump);
    }

    private List<CommunityAssessmentsGeneral> communityAssessmentsGeneralList(DumpFilter filter) {
        return communityDao.findAllByOrganizationId(filter.getOrganizationId())
                .stream()
                .map(community -> {
                    var communityAssessmentsGeneral = new CommunityAssessmentsGeneral();
                    communityAssessmentsGeneral.setCommunityName(community.getName());
                    communityAssessmentsGeneral.setGad7Completed(clientAssessmentResultDao.count(assessmentResultSpecifications.gad7CompletedByCommunity(community).and(assessmentResultSpecifications.ofActiveClients())));
                    communityAssessmentsGeneral.setPhq9Completed(clientAssessmentResultDao.count(assessmentResultSpecifications.phq9CompletedByCommunity(community).and(assessmentResultSpecifications.ofActiveClients())));
                    communityAssessmentsGeneral.setComprehensiveCompleted(clientAssessmentResultDao.count(assessmentResultSpecifications.comprehensiveCompletedByCommunity(community).and(assessmentResultSpecifications.ofActiveClients())));
                    return communityAssessmentsGeneral;
                })
                .collect(Collectors.toList());

    }

    private List<GAD7PHQ9Scoring> gad7PHQ9Scoring(DumpFilter configuration) {
        return clientDao.findAll(clientSpecifications.byIds(configuration).and(clientSpecifications.isActive()))
                .stream()
                .map(client -> {
                    var scoring = new GAD7PHQ9Scoring();

                    DumpGeneratorUtils.fillClientBaseInfo(scoring, client);

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

                    return scoring;
                })
                .collect(Collectors.toList());
    }


    private List<ComprehensiveDetail> comprehensiveDetailList(DumpFilter filter) {
        return clientAssessmentResultDao.findAll(assessmentResultSpecifications.comprehensiveCompleted(filter.getResidentIds()).and(assessmentResultSpecifications.ofActiveClients())
                .and(assessmentResultSpecifications.ofActiveClients()))
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


    private List<CommunitySPGeneral> communitySPGeneralList(DumpFilter filter) {
        return communityDao.findAllByOrganizationId(filter.getOrganizationId())
                .stream()
                .map(community -> {
                    var spGeneral = new CommunitySPGeneral();
                    spGeneral.setCommunityName(community.getName());

                    spGeneral.setNumberOfPatients(clientDao.count(clientSpecifications.byCommunity(community)
                            .and(clientSpecifications.isActive())));

                    spGeneral.setNumberOfSpOpened(
                            clientServicePlanDao.count(
                                    servicePlanSpecifications.byCommunityAndStatus(community, ServicePlanStatus.IN_DEVELOPMENT)
                                            .and(servicePlanSpecifications.ofActiveClient())
                            )
                    );
                    spGeneral.setNumberOfSpClosed(
                            clientServicePlanDao.count(
                                    servicePlanSpecifications.byCommunityAndStatus(community, ServicePlanStatus.SHARED_WITH_CLIENT)
                                            .and(servicePlanSpecifications.ofActiveClient())
                            )
                    );

                    spGeneral.setTotalNumberOfNeeds(
                            servicePlanNeedDao.count(
                                    servicePlanSpecifications.needsByCommunity(community)
                                            .and(servicePlanSpecifications.needsOfActiveClient())
                            )
                    );
                    spGeneral.setTotalNumberOfGoals(
                            servicePlanGoalDao.count(
                                    servicePlanSpecifications.goalsByCommunity(community)
                                            .and(servicePlanSpecifications.goalsOfActiveClient())
                            )
                    );
                    spGeneral.setNumberOfAccomplishedGoals(
                            servicePlanGoalDao.count(
                                    servicePlanSpecifications.accomplishedGoalsByCommunity(community)
                                            .and(servicePlanSpecifications.goalsOfActiveClient())
                            )
                    );

                    return spGeneral;
                })
                .collect(Collectors.toList());

    }

    private Long dateDiff(Temporal first, Temporal second, TemporalUnit unit) {
        if (ObjectUtils.allNotNull(first, second)) {
            return unit.between(first, second);

        }
        return null;
    }

    private List<SPIndividual> spIndividualList(DumpFilter filter) {
        return clientServicePlanDao.findAll(servicePlanSpecifications.details(filter).and(servicePlanSpecifications.ofActiveClient()))
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
        return clientServicePlanDao.findAll(servicePlanSpecifications.details(filter).and(servicePlanSpecifications.ofActiveClient()))
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

    private List<AssessedClientInsuranceInfo> assessedClientInsuranceInfoList(DumpFilter filter) {
        var assessmentCounts = clientDao.comprehensiveCompletedCount(filter.getOrganizationId())
                .stream().collect(Collectors.toMap(ClientDao.AssessmentCount::getId, ClientDao.AssessmentCount::getCount));
        return clientDao.findAll(clientSpecifications.byOrganizationId(filter.getOrganizationId()).and(clientSpecifications.isActive()))
                .stream()
                .map(client -> {
                    var info = new AssessedClientInsuranceInfo();

                    DumpGeneratorUtils.fillClientBaseInfo(info, client);

                    DumpGeneratorUtils.fillInsuranceInfo(info, client);

                    info.setCompletedComprehensiveCount(assessmentCounts.getOrDefault(client.getId(), 0L));

                    return info;
                })
                .collect(Collectors.toList());
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.TOTAL_FOR_COMMUNITY;
    }
}
