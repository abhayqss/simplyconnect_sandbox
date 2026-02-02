package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.EventSpecificationGenerator;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanScoringCalculable;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus;
import com.scnsoft.eldermark.service.AssessmentScoringService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.ServicePlanUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.EventTypeCodeEnum.ERV;
import static com.scnsoft.eldermark.beans.EventTypeCodeEnum.H;
import static com.scnsoft.eldermark.beans.reports.enums.ReportType.DEMOGRAPHICS;
import static com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus.IN_DEVELOPMENT;
import static com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus.SHARED_WITH_CLIENT;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class DemographicsReportGenerator extends DefaultReportGenerator<DemographicsReport> {

    private static final Logger logger = LoggerFactory.getLogger(DemographicsReportGenerator.class);
    private static final List<String> EVENT_TYPES = Stream.of(ERV, H).map(Objects::toString).collect(toList());
    private static final Integer GOAL_ACCOMPLISHED_THRESHOLD = 100;

    @Autowired
    private EventDao eventdao;

    @Autowired
    private EventSpecificationGenerator eventSpecificationGenerator;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ClientSpecificationGenerator clientSpecifications;

    @Autowired
    private AssessmentScoringService assessmentScoringService;

    @Autowired
    private ServicePlanDao servicePlanDao;

    @Autowired
    private ServicePlanScoringDao servicePlanScoringDao;

    @Autowired
    private ServicePlanNeedDao servicePlanNeedDao;

    @Autowired
    private ServicePlanGoalDao servicePlanGoalDao;

    @Override
    public DemographicsReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new DemographicsReport();

        populateReportingCriteriaFields(filter, report);

        var clients = getClients(filter, permissionFilter);
        var clientsById = clients.stream().collect(StreamUtils.toMapOfUniqueKeys(ClientProjection::getId));

        var assessments = getAssessments(filter, permissionFilter);
        var servicePlans = getServicePlans(filter, permissionFilter);

        report.setAssessmentsGeneralList(assessmentsGeneral(filter.getAccessibleCommunityIdsAndNames(), assessments));
        report.setGad7PHQ9ScoringList(gad7PHQ9Scoring(clients, assessments));
        report.setComprehensiveDetailList(comprehensiveDetailList(clientsById, assessments));
        report.setSpGeneralList(spGeneral(filter.getAccessibleCommunityIdsAndNames(), clients, servicePlans));
        report.setSpIndividuals(spIndividuals(clientsById, servicePlans));
        report.setSpDetailList(spDetailList(clientsById, servicePlans));
        return report;
    }


    private List<ClientProjection> getClients(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var clientsOfCommunities = clientSpecifications.byCommunities(filter.getAccessibleCommunityIdsAndNames());
        var hasAccessToClients = clientSpecifications.hasDetailsAccess(permissionFilter);
        var createdBefore = clientSpecifications.createdBeforeOrWithoutDateCreated(filter.getInstantTo());

        var clients = clientDao.findAll(clientsOfCommunities.and(hasAccessToClients).and(createdBefore), ClientProjection.class);

        return clients;
    }

    private List<AssessmentResultProjection> getAssessments(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var latestAccessibleAssessmentWithinPeriod = latestAccessibleAssessmentWithinPeriod(filter, permissionFilter);
        var ofCommunities = assessmentResultSpecifications.ofCommunities(filter.getAccessibleCommunityIdsAndNames());

        var assessments = clientAssessmentDao.findAll(
                ofCommunities.and(latestAccessibleAssessmentWithinPeriod), AssessmentResultProjection.class);

        return assessments;
    }

    private List<ServicePlanProjectionWrapper> getServicePlans(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var accessibleCommunityIds = filter.getAccessibleCommunityIdsAndNames().stream().map(IdAware::getId).collect(Collectors.toList());

        var spOfCommunities = servicePlanSpecifications.ofCommunityIds(accessibleCommunityIds);
        var latestAccessibleSpWithinPeriod = latestAccessibleSpWithinPeriod(filter, permissionFilter);
        var servicePlans = servicePlanDao.findAll(spOfCommunities.and(latestAccessibleSpWithinPeriod), ServicePlanProjection.class)
                .stream().map(ServicePlanProjectionWrapper::new).collect(toList());

        var servicePlanIds = servicePlans.stream().map(ServicePlanProjection::getId).collect(Collectors.toList());

        var scoringBySp = servicePlanScoringDao.findAllInBatches((longs) -> servicePlanSpecifications.scoringsOfServicePlans(longs),
                servicePlanIds, ServicePlanScoringProjection.class)
                .stream()
                .collect(StreamUtils.toMapOfUniqueKeys(ServicePlanScoringProjection::getServicePlanId));

        servicePlans.forEach(sp -> sp.setScoring(scoringBySp.getOrDefault(sp.getId(), null)));

        var needs = servicePlanNeedDao.findAllInBatches((longs) -> servicePlanSpecifications.needsOfServicePlans(longs),
                servicePlanIds, ServicePlanNeedProjection.class).stream()
                .map(ServicePlanNeedProjectionWrapper::new)
                .collect(toList());

        var goals = servicePlanGoalDao.findAllInBatches(longs -> servicePlanSpecifications.goalsOfServicePlans(longs),
                servicePlanIds, ServicePlanGoalProjection.class);

        assignNeedsAndGoals(servicePlans, needs, goals);

        return servicePlans;
    }

    private void assignNeedsAndGoals(List<ServicePlanProjectionWrapper> servicePlans, List<ServicePlanNeedProjectionWrapper> needs,
                                     List<ServicePlanGoalProjection> goals) {
        var needById = needs.stream().collect(StreamUtils.toMapOfUniqueKeys(ServicePlanNeedProjection::getId));
        var goalsByNeed = goals.stream().collect(Collectors.groupingBy(ServicePlanGoalProjection::getNeedId));
        goalsByNeed.forEach((key, value) -> needById.get(key).setGoals(value));

        var spById = servicePlans.stream().collect(StreamUtils.toMapOfUniqueKeys(ServicePlanProjection::getId));
        var needsBySp = needs.stream().collect(Collectors.groupingBy(ServicePlanNeedProjection::getServicePlanId));
        needsBySp.forEach((key, value) -> spById.get(key).setNeeds(value));
    }

    @Override
    public ReportType getReportType() {
        return DEMOGRAPHICS;
    }

    private <T extends IdNameAware> List<AssessmentsGeneral> assessmentsGeneral(List<T> communities, List<AssessmentResultProjection> assessments) {
        var completedByCommunity = assessments.stream()
                .filter(a -> AssessmentStatus.COMPLETED.equals(a.getAssessmentStatus()))
                .collect(Collectors.groupingBy(AssessmentResultProjection::getClientCommunityId));

        List<AssessmentsGeneral> assessmentsGeneralList = communities
                .stream()
                .map(community -> {
                    var general = new AssessmentsGeneral();
                    general.setCommunityName(community.getName());

                    var assessmentsByType = completedByCommunity.getOrDefault(community.getId(), Collections.emptyList())
                            .stream()
                            .collect(Collectors.groupingBy(AssessmentResultProjection::getAssessmentShortName, Collectors.counting()));

                    general.setGad7Completed(assessmentsByType.getOrDefault(Assessment.GAD7, 0L));
                    general.setPhq9Completed(assessmentsByType.getOrDefault(Assessment.PHQ9, 0L));
                    general.setComprehensiveCompleted(assessmentsByType.getOrDefault(Assessment.COMPREHENSIVE, 0L));
                    general.setNorCalComprehensiveCompleted(assessmentsByType.getOrDefault(Assessment.NOR_CAL_COMPREHENSIVE, 0L));

                    return general;
                })
                .collect(Collectors.toList());

        return assessmentsGeneralList;
    }

    private List<GAD7PHQ9Scoring> gad7PHQ9Scoring(List<ClientProjection> clients,
                                                  List<AssessmentResultProjection> assessments) {

        var assessmentsByClientId = assessments.stream().collect(Collectors.groupingBy(AssessmentResultProjection::getClientId));

        var gad7phq9Assessments = assessments.stream()
                .filter(assessmentResultProjection ->
                        Assessment.GAD7.equals(assessmentResultProjection.getAssessmentShortName()) || Assessment.PHQ9.equals(assessmentResultProjection.getAssessmentShortName())
                )
                .collect(toList());

        //map of assessment result id -> score
        var assessmentScores = assessmentScoringService.calculateScores(gad7phq9Assessments)
                .stream()
                .collect(StreamUtils.toMapOfUniqueKeysAndThen(p -> p.getFirst().getId(), Pair::getSecond));

        return clients.stream()
                .map(client -> {
                    var scoring = new GAD7PHQ9Scoring();
                    scoring.setClientId(client.getId());
                    scoring.setFirstName(client.getFirstName());
                    scoring.setLastName(client.getLastName());
                    scoring.setOrganization(client.getOrganizationName());
                    scoring.setCommunity(client.getCommunityName());

                    var assessmentsByType = assessmentsByClientId.getOrDefault(client.getId(), Collections.emptyList()).stream()
                            .collect(Collectors.groupingBy(AssessmentResultProjection::getAssessmentShortName));

                    scoring.setGad7scores(getScores(assessmentScores, assessmentsByType.get(Assessment.GAD7)));
                    scoring.setPhq9Scores(getScores(assessmentScores, assessmentsByType.get(Assessment.PHQ9)));

                    return scoring;
                })
                .collect(Collectors.toList());
    }

    private List<Long> getScores(Map<Long, Long> assessmentScores, List<AssessmentResultProjection> assessments) {
        return CollectionUtils.emptyIfNull(assessments).stream()
                .map(AssessmentResultProjection::getId)
                .map(assessmentScores::get)
                .collect(Collectors.toList());
    }

    private List<ComprehensiveDetail> comprehensiveDetailList(Map<Long, ClientProjection> clientsById,
                                                              List<AssessmentResultProjection> assessments) {
        var completedComprehensives = assessments.stream()
                .filter(a -> (Assessment.COMPREHENSIVE.equals(a.getAssessmentShortName()) || Assessment.NOR_CAL_COMPREHENSIVE.equals(a.getAssessmentShortName()))
                        && AssessmentStatus.COMPLETED.equals(a.getAssessmentStatus()));

        return completedComprehensives
                .map(clientAssessmentResult -> {
                    var detail = new ComprehensiveDetail();
                    var comprehensiveParsed = parseComprehensive(clientAssessmentResult);
                    var client = Optional.ofNullable(clientsById.getOrDefault(clientAssessmentResult.getClientId(), null))
                            .orElseGet(() -> loadMissingClient("Assessment", clientAssessmentResult.getClientId()));

                    detail.setPatientName(client.getFullName());
                    detail.setPatientId(client.getId());
                    detail.setCommunity(client.getCommunityName());

                    Long minutes = DateTimeUtils.millisToMinutes(clientAssessmentResult.getTimeToComplete());
                    detail.setTimeToCompleteInMinutes(minutes != null && minutes == 0L ? Long.valueOf(1L) : minutes);

                    detail.setGenderFormClient(client.getGenderDisplayName());
                    detail.setGenderFromAssessment(comprehensiveParsed.getGender());

                    detail.setRaceFromClient(client.getRaceDisplayName());
                    detail.setRaceFromAssessment(comprehensiveParsed.getRace());
                    LocalDate birthDate = client.getBirthDate();
                    detail.setAge(dateDiff(birthDate, LocalDate.now(), ChronoUnit.YEARS));
                    detail.setInsuranceNetwork(client.getInNetworkInsuranceDisplayName());
                    detail.setInsurancePlan(client.getInsurancePlan());
                    detail.setIncome(comprehensiveParsed.getMonthlyIncome());

                    return detail;
                })
                .collect(Collectors.toList());
    }

    private <T extends IdNameAware> List<SPGeneral> spGeneral(List<T> communities, List<ClientProjection> clients,
                                      List<ServicePlanProjectionWrapper> servicePlans) {

        var clientCountsByCommunity = clients.stream().collect(Collectors.groupingBy(ClientProjection::getCommunityId, Collectors.counting()));
        var spByByCommunity = servicePlans.stream().collect(Collectors.groupingBy(ServicePlanProjection::getClientCommunityId));

        return communities.stream()
                .map(community -> {
                    SPGeneral spGeneral = new SPGeneral();
                    spGeneral.setCommunityName(community.getName());
                    spGeneral.setNumberOfPatients(clientCountsByCommunity.getOrDefault(community.getId(), 0L));

                    var communitySp = spByByCommunity.getOrDefault(community.getId(), Collections.emptyList());
                    spGeneral.setNumberOfSpOpened(getCountOfStatus(communitySp, IN_DEVELOPMENT));
                    spGeneral.setNumberOfSpClosed(getCountOfStatus(communitySp, SHARED_WITH_CLIENT));

                    var needsCount = communitySp.stream().map(ServicePlanProjectionWrapper::getNeeds)
                            .mapToLong(List::size).sum();

                    var goals = communitySp.stream().map(ServicePlanProjectionWrapper::getNeeds).flatMap(List::stream)
                            .map(ServicePlanNeedProjectionWrapper::getGoals).flatMap(List::stream).collect(toList());

                    spGeneral.setTotalNumberOfNeeds(needsCount);
                    spGeneral.setTotalNumberOfGoals(goals.size());
                    spGeneral.setNumberOfAccomplishedGoals(goals.stream()
                            .filter(g -> GOAL_ACCOMPLISHED_THRESHOLD.equals(g.getGoalCompletion())).count());

                    spGeneral.setAverageNumberOfServicePlansPerClient(calculateFraction(spGeneral.getNumberOfSPopened() + spGeneral.getNumberOfSpClosed(), spGeneral.getNumberOfPatients()));
                    spGeneral.setAverageNumberOfGoalsPerClient(calculateFraction(spGeneral.getTotalNumberOfGoals(), spGeneral.getNumberOfPatients()));
                    spGeneral.setAverageNumberOfNeedsPerClient(calculateFraction(spGeneral.getTotalNumberOfNeeds(), spGeneral.getNumberOfPatients()));
                    spGeneral.setPercentOfAccomplishedGoals(calculateFraction(spGeneral.getNumberOfAccomplishedGoals(), spGeneral.getTotalNumberOfGoals()));

                    return spGeneral;
                })
                .collect(Collectors.toList());
    }

    private <T extends ServicePlanProjection> long getCountOfStatus(List<T> servicePlans, ServicePlanStatus status) {
        return servicePlans.stream().filter(q -> status.equals(q.getServicePlanStatus())).count();
    }

    private double calculateFraction(long numerator, long denominator) {
        return denominator != 0 ? round((double) numerator / denominator) : 0;
    }

    private double round(double number) {
        return Math.round(number * 100) / 100.00;
    }

    private List<SPIndividualTab> spIndividuals(Map<Long, ClientProjection> clientsById, List<ServicePlanProjectionWrapper> servicePlans) {
        var spByCommunity = servicePlans.stream().collect(Collectors.groupingBy(ServicePlanProjection::getClientCommunityId));

        return spByCommunity.values().stream()
                .map(communitySP -> {

                    var tab = new SPIndividualTab();
                    tab.setCommunity(communitySP.get(0).getClientCommunityName());
                    tab.setSpIndividualClients(createSpIndividualClients(clientsById, communitySP));

                    return tab;
                })
                .collect(Collectors.toList());
    }

    private List<SPIndividualClient> createSpIndividualClients(Map<Long, ClientProjection> clientsById, Collection<ServicePlanProjectionWrapper> servicePlans) {
        var spByClient = servicePlans.stream().collect(Collectors.groupingBy(ServicePlanProjection::getClientId));

        return spByClient.entrySet().stream()
                .map(e -> {
                    var clientSP = e.getValue();

                    var clientId = e.getKey();
                    var client = Optional.ofNullable(clientsById.getOrDefault(clientId, null))
                            .orElseGet(() -> loadMissingClient("Service Plan", clientId));


                    var spIndividualClient = new SPIndividualClient();

                    spIndividualClient.setName(client.getFullName());
                    spIndividualClient.setId(client.getId());
                    spIndividualClient.setSpIndividualDomains(createSpIndividualDomains(clientSP));

                    return spIndividualClient;
                })
                .collect(Collectors.toList());
    }

    private List<SPIndividualDomain> createSpIndividualDomains(List<ServicePlanProjectionWrapper> clientServicePlans) {
        var partitionedSP = clientServicePlans.stream()
                .collect(Collectors.partitioningBy(sp -> CollectionUtils.isEmpty(sp.getNeeds())));

        //one record for all service plans with empty needs
        var emptyDomain = partitionedSP.getOrDefault(Boolean.TRUE, Collections.emptyList())
                .stream()
                .findFirst()
                .map(t -> new SPIndividualDomain())
                .stream();


        var withDomains = partitionedSP.getOrDefault(Boolean.FALSE, Collections.emptyList())
                .stream()
                .flatMap(servicePlanWithNeeds -> {

                    var groupedByDomains = servicePlanWithNeeds.getNeeds().stream()
                            .collect(Collectors.groupingBy(ServicePlanNeedProjection::getDomain));

                    return groupedByDomains.entrySet()
                            .stream()
                            .map(e -> {
                                        var domain = e.getKey();
                                        var needs = e.getValue();

                                        var spDomain = new SPIndividualDomain();

                                        spDomain.setDomainName(domain.getDisplayName());
                                        spDomain.setScoreOfTiedToServicePlanNeed(ServicePlanUtils.resolveScore(
                                                servicePlanWithNeeds.getScoring(), domain));

                                        var resourceNames = needs.stream()
                                                .map(ServicePlanNeedProjectionWrapper::getGoals)
                                                .flatMap(List::stream)
                                                .map(ServicePlanGoalProjection::getResourceName)
                                                .filter(StringUtils::isNotEmpty)
                                                .collect(toList());

                                        spDomain.setResourceNames(resourceNames);

                                        return spDomain;
                                    }
                            );

                });

        return Stream.concat(emptyDomain, withDomains).collect(Collectors.toList());
    }

    private List<SPDetails> spDetailList(Map<Long, ClientProjection> clientsById,
                                         List<ServicePlanProjectionWrapper> servicePlans) {
        var clientIdsWithServicePlans = servicePlans.stream().map(ServicePlanProjection::getClientId).distinct().collect(toList());

        //todo merged clients?
        //todo not-viewable settings?
        var eventsOfClients = eventSpecificationGenerator.byClientIds(clientIdsWithServicePlans);
        var byEventTypeCodes = eventSpecificationGenerator.byEventTypeCodes(EVENT_TYPES);
        var eventCounts = eventdao.countsByClientId(eventsOfClients.and(byEventTypeCodes));

        return servicePlans
                .stream()
                .map(servicePlan -> {
                    var spDetails = new SPDetails();
                    var client = Optional.ofNullable(clientsById.getOrDefault(servicePlan.getClientId(), null))
                            .orElseGet(() -> loadMissingClient("Service plan", servicePlan.getClientId()));

                    spDetails.setPatientName(client.getFullName());
                    spDetails.setPatientId(client.getId());
                    spDetails.setCommunity(servicePlan.getClientCommunityName());
                    spDetails.setStatus(servicePlan.getServicePlanStatus().name());
                    spDetails.setDaysToComplete(dateDiff(servicePlan.getDateCreated(), servicePlan.getDateCompleted(), ChronoUnit.DAYS));

                    spDetails.setEventsCount(eventCounts.getOrDefault(client.getId(), 0L));

                    var goalCount = CollectionUtils.emptyIfNull(servicePlan.getNeeds()).stream()
                            .map(ServicePlanNeedProjectionWrapper::getGoals)
                            .mapToLong(List::size).sum();
                    spDetails.setGoalsCount(goalCount);
                    return spDetails;
                })
                .collect(Collectors.toList());
    }

    private Long dateDiff(Temporal first, Temporal second, TemporalUnit unit) {
        return ObjectUtils.allNotNull(first, second) ? unit.between(first, second) : null;
    }

    private ClientProjection loadMissingClient(String entity, Long clientId) {
        logger.warn("{} belongs to client[{}], but such a client is not among accessible within communities. Client will be loaded by id",
                entity, clientId);
        return clientDao.findById(clientId, ClientProjection.class).orElseThrow();
    }

    interface ClientProjection extends IdAware, CommunityIdAware, CommunityNameAware {
        String getFirstName();

        String getLastName();

        default String getFullName() {
            return Stream.of(getFirstName(), getLastName()).filter(StringUtils::isNotEmpty)
                    .collect(Collectors.joining(" "));
        }

        String getOrganizationName();

        String getGenderDisplayName();

        String getRaceDisplayName();

        LocalDate getBirthDate();

        String getInNetworkInsuranceDisplayName();

        String getInsurancePlan();
    }

    interface AssessmentResultProjection extends IdAware, ClientIdAware, AssessmentScoringCalculable {
        Long getClientCommunityId();

        String getAssessmentShortName();

        AssessmentStatus getAssessmentStatus();

        Long getTimeToComplete();
    }

    interface ServicePlanProjection extends IdAware, ClientIdAware {
        Long getClientCommunityId();

        String getClientCommunityName();

        ServicePlanStatus getServicePlanStatus();

        Instant getDateCreated();

        Instant getDateCompleted();
    }

    static class ServicePlanProjectionWrapper implements ServicePlanProjection {
        private final ServicePlanProjection delegate;
        private ServicePlanScoringProjection scoring;
        private List<ServicePlanNeedProjectionWrapper> needs = new ArrayList<>();

        public ServicePlanProjectionWrapper(ServicePlanProjection delegate) {
            this.delegate = delegate;
        }

        public Long getId() {
            return delegate.getId();
        }

        public Long getClientId() {
            return delegate.getClientId();
        }

        @Override
        public Long getClientCommunityId() {
            return delegate.getClientCommunityId();
        }

        @Override
        public String getClientCommunityName() {
            return delegate.getClientCommunityName();

        }

        public ServicePlanStatus getServicePlanStatus() {
            return delegate.getServicePlanStatus();
        }

        public Instant getDateCreated() {
            return delegate.getDateCreated();
        }

        public Instant getDateCompleted() {
            return delegate.getDateCompleted();
        }

        public List<ServicePlanNeedProjectionWrapper> getNeeds() {
            return needs;
        }

        public void setNeeds(List<ServicePlanNeedProjectionWrapper> needs) {
            this.needs = needs;
        }

        public ServicePlanScoringProjection getScoring() {
            return scoring;
        }

        public void setScoring(ServicePlanScoringProjection scoring) {
            this.scoring = scoring;
        }
    }

    interface ServicePlanScoringProjection extends IdAware, ServicePlanIdAware, ServicePlanScoringCalculable {
    }

    interface ServicePlanNeedProjection extends IdAware, ServicePlanIdAware {
        ServicePlanNeedType getDomain();
    }

    static class ServicePlanNeedProjectionWrapper implements ServicePlanNeedProjection {
        private final ServicePlanNeedProjection projection;
        private List<ServicePlanGoalProjection> goals = new ArrayList<>();

        public ServicePlanNeedProjectionWrapper(ServicePlanNeedProjection projection) {
            this.projection = projection;
        }

        @Override
        public Long getId() {
            return projection.getId();
        }

        @Override
        public Long getServicePlanId() {
            return projection.getServicePlanId();
        }

        @Override
        public ServicePlanNeedType getDomain() {
            return projection.getDomain();
        }

        public List<ServicePlanGoalProjection> getGoals() {
            return goals;
        }

        public void setGoals(List<ServicePlanGoalProjection> goals) {
            this.goals = goals;
        }

    }

    interface ServicePlanGoalProjection extends IdAware {
        Long getNeedId();

        Integer getGoalCompletion();

        String getResourceName();
    }
}
