package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.CodeSystem;
import com.scnsoft.eldermark.beans.reports.enums.EthnicityType;
import com.scnsoft.eldermark.beans.reports.enums.GenderType;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;
import com.scnsoft.eldermark.beans.reports.model.HudFirstTab;
import com.scnsoft.eldermark.beans.reports.model.HudReport;
import com.scnsoft.eldermark.beans.reports.model.HudSecondTab;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.projection.AssessmentScoringCalculable;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.ServicePlanIdAware;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.EventSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ProblemObservationSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.*;
import static com.scnsoft.eldermark.beans.reports.enums.ReportType.HUD_MFSC;
import static java.util.Arrays.asList;
import static java.util.Map.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service
public class HudMfscReportGenerator extends DefaultReportGenerator<HudReport> {

    private static final String ASTHMA_ICD_10_CODE = "J45";

    private static final Map<String, Integer> RACE_MAP = of(
            "Yes", 1,
            "No", 2
    );

    private Map<Integer, List<String>> insuranceMap;

    @PostConstruct
    private void postConstruct() {
        insuranceMap = new HashMap<>();
        insuranceMap.put(2, asList("Aetna", "Anthem", "BCBS IL", "BCBS Louisisana", "BCBS Mass", "BCBS Mich", "BCBS ND", "Blue Sheild of CA", "Buckeye Health Plan", "CareFirst BCBS", "Caresource", "Cigna healthcare", "Coastal healthcare",
                "Confinity inc", "Constitution life insurance company", "Fallon community health plan", "Golden Rule", "Gunderson Health Plan", "Health Plus of Louisiana", "Healthchoice of Oklahome", "Healthsmart WTC program",
                "Highmark BCBS", "Independce Blue Cross", "Medical mutual", "Omaha", "Pacific independent physician association", "Paramount", "Physicians health plan of northern Indiana", "preffered one",
                "Premera blue cross", "Priority health managed benefits", "Providence health plan", "Sanford health plan", "Scripps health", "Select health inc", "State farm", "Summa care", "Wellmark BCBS"));
        insuranceMap.put(3, asList("AARP", "Americo", "Bankers Fidelity", "BCBS Mass Medicare Asvantage", "Forethought (supplemenbt)", "Hawaii Medicare Service Associ", "Humana", "Medicare", "New Mexico health connections"));
        insuranceMap.put(4, asList("County or local government funds", "Medicaid", "Northeast medical services", "Other state funds", "State education funds", "State financed health insurance plan other than Medicaid", "State mental health agency funds",
                "State welfare or child and family services funds"));
        insuranceMap.put(6, asList("VA funds"));
        insuranceMap.put(7, asList("HIS/Tribal/Urban ITU funds"));
        insuranceMap.put(8, asList("Sharp rees stealy", "State corrections or jeveniule justice funds"));
        insuranceMap.put(9, asList("Cash or self payment"));
    }

    private static final Map<String, Integer> ASSESSMENT_RACE_MAPPING = of(
            "Alaska Native", 1,
            "American Indian", 1,
            "Asian", 2,
            "Black or African American", 3,
            "Hispanic or Latino", INFO_NOT_COLLECTED_CODE,
            "Native Hawaiian or Pacific Islander", 4,
            "White", 5,
            "Other", INFO_NOT_COLLECTED_CODE
    );


    private static final String SOME_ASSISTANCE = "Some assistance";
    private static final String TOTAL_ASSISTANCE = "Total assistance";

    private static final List<String> EVENT_DESCRIPTIONS = asList("ER Visit", "Medical emergency", "Hospitalization");

    private static DateTimeFormatter DATE_FORMATTER_MM_DD_YYYY = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private EventSpecificationGenerator eventSpecificationGenerator;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private ServicePlanDao servicePlanDao;

    @Autowired
    protected ProblemObservationDao problemObservationDao;

    @Autowired
    protected ClientSpecificationGenerator clientSpecifications;

    @Autowired
    private ServicePlanNeedDao servicePlanNeedDao;

    @Autowired
    protected ProblemObservationSpecificationGenerator problemObservationSpecificationGenerator;

    @Override
    public HudReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new HudReport();
        report.setHudFirstTab(getHudReportFirstTab(filter));
        report.setHudSecondTab(getHudSecondTabList(filter, permissionFilter));
        return report;
    }

    private HudFirstTab getHudReportFirstTab(InternalReportFilter filter) {
        var hudFirstTab = new HudFirstTab();
        hudFirstTab.setReportingPeriodStartDate(filter.getInstantFrom());
        hudFirstTab.setReportingPeriodEndDate(filter.getInstantTo());
        return hudFirstTab;
    }

    private List<HudSecondTab> getHudSecondTabList(InternalReportFilter filter, PermissionFilter permissionFilter) {
        List<ClientHudMfscReportDetails>  clientsList = getClients(filter, permissionFilter);
        var eventCounts = getEventsAndErEventsByClient(filter, permissionFilter);
        var assessments = getAssessmentsWithinPeriodByClient(filter, permissionFilter, ClientAssessmentDao.ORDER_BY_DATE_STARTED);
        var servicePlans = getServicePlansByClient(filter, permissionFilter, servicePlanDao.SORT_BY_DATE_CREATED_ASC);
        var problemCounts = getProblemsCountByClients(filter, permissionFilter);
        var hivProblemsCount = getProblemsByCodeSystemByClient(filter, permissionFilter);
        return clientsList.stream().map(client -> toHudReportSecondTab(client, eventCounts, assessments, servicePlans, problemCounts, hivProblemsCount, filter, permissionFilter)).collect(toList());
    }

    protected Map<CodeSystem, Map<Long, Long>> getProblemsByCodeSystemByClient(InternalReportFilter filter, PermissionFilter permissionFilter) {
        return Collections.EMPTY_MAP;
    }

    private Map<Long, Long> getProblemsCountByClients(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hudClients = problemObservationSpecificationGenerator.byAccessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate(permissionFilter, filter.getAccessibleCommunityIdsAndNames(), filter.getInstantTo(), filter.getInstantFrom());
        var byCodeSystemAndCode = problemObservationSpecificationGenerator.byCodeSystemAndCode(CodeSystem.ICD_10_CM, Set.of(ASTHMA_ICD_10_CODE));
        return problemObservationDao.countsByClientId(byCodeSystemAndCode.and(hudClients));
    }

    private Map<Long, Pair<Long, Long>> getEventsAndErEventsByClient(InternalReportFilter filter, PermissionFilter permissionFilter) {
        return eventDao.countsAllAndWithRestrictionGroupByClientId(eventsOfAccessibleClients(filter, permissionFilter),
                eventSpecificationGenerator.byEventTypesIn(EVENT_DESCRIPTIONS).or(eventSpecificationGenerator.byErVisit(true)));
    }

    private Specification<Event> eventsOfAccessibleClients(InternalReportFilter filter, PermissionFilter permissionFilter) {
        return eventSpecificationGenerator.byAccessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate(permissionFilter, filter.getAccessibleCommunityIdsAndNames(), filter.getInstantTo(), filter.getInstantFrom())
                .and(eventSpecificationGenerator.hasAccessIgnoringNotViewable(permissionFilter))
                .and(eventSpecificationGenerator.byEventDateTimeIn(filter.getInstantFrom(), filter.getInstantTo()));
    }

    private List<ClientHudMfscReportDetails> getClients(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hudClients = clientSpecifications.accessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate(permissionFilter, filter.getAccessibleCommunityIdsAndNames(), filter.getInstantTo(), filter.getInstantFrom());
        return clientDao.findAll(hudClients, ClientHudMfscReportDetails.class);
    }

    private Map<Long, List<ComprehensiveAssessment<AssessmentHudMfscDetails>>> getAssessmentsWithinPeriodByClient(InternalReportFilter filter,
                                                                                                                  PermissionFilter permissionFilter,
                                                                                                                  Sort sort) {
        var latestAccessibleAssessmentWithinPeriod = latestAccessibleAssessmentWithinPeriod(filter, permissionFilter);
        var comprehensive = assessmentResultSpecifications.comprehensiveType();
        var norCalComprehensive = assessmentResultSpecifications.byType(Assessment.NOR_CAL_COMPREHENSIVE);
        var byAccessibleClients = assessmentResultSpecifications.byAccessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate
                (permissionFilter, filter.getAccessibleCommunityIdsAndNames(), filter.getInstantTo(), filter.getInstantFrom());

        List<AssessmentHudMfscDetails> assessments;
        if (sort == null) {
            assessments = clientAssessmentDao.findAll(byAccessibleClients.and(comprehensive.or(norCalComprehensive)).and(latestAccessibleAssessmentWithinPeriod), AssessmentHudMfscDetails.class);
        } else {
            assessments = clientAssessmentDao.findAll(byAccessibleClients.and(comprehensive.or(norCalComprehensive)).and(latestAccessibleAssessmentWithinPeriod), AssessmentHudMfscDetails.class, sort);
        }

        return assessments.stream().collect(Collectors.groupingBy(AssessmentHudMfscDetails::getClientId)).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(this::parseComprehensive).collect(toList())));
    }

    private Map<Long, List<ServicePlanHudMfscDetailsWrapper>> getServicePlansByClient(InternalReportFilter filter, PermissionFilter permissionFilter, Sort sort) {

        var latestAccessibleSpWithinPeriod = latestAccessibleSpWithinPeriod(filter, permissionFilter);
        var byAccessibleClients = servicePlanSpecifications.byAccessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate
                (permissionFilter, filter.getAccessibleCommunityIdsAndNames(), filter.getInstantTo(), filter.getInstantFrom());

        List<ServicePlanHudMfscDetails> servicePlansProjections;
        if (sort == null) {
            servicePlansProjections = servicePlanDao.findAll(byAccessibleClients.and(latestAccessibleSpWithinPeriod), ServicePlanHudMfscDetails.class);
        } else {
            servicePlansProjections = servicePlanDao.findAll(byAccessibleClients.and(latestAccessibleSpWithinPeriod), ServicePlanHudMfscDetails.class, sort);
        }

        var servicePlans = servicePlansProjections.stream().map(ServicePlanHudMfscDetailsWrapper::new).collect(toList());
        var servicePlanIds = servicePlans.stream().map(ServicePlanHudMfscDetailsWrapper::getId).collect(Collectors.toList());

        var needs = servicePlanNeedDao.findAllInBatches((longs) -> servicePlanSpecifications.needsOfServicePlans(longs),
                servicePlanIds, ServicePlanNeedHudMfscDetails.class);

        assignNeeds(servicePlans, needs);
        return servicePlans.stream().collect(Collectors.groupingBy(ServicePlanHudMfscDetailsWrapper::getClientId));
    }

    private void assignNeeds(List<ServicePlanHudMfscDetailsWrapper> servicePlans, List<ServicePlanNeedHudMfscDetails> needs) {
        var spById = servicePlans.stream().collect(StreamUtils.toMapOfUniqueKeys(ServicePlanHudMfscDetails::getId));
        var needsBySp = needs.stream().collect(Collectors.groupingBy(ServicePlanNeedHudMfscDetails::getServicePlanId));
        needsBySp.forEach((key, value) -> spById.get(key).setNeeds(value));
    }

    protected HudSecondTab toHudReportSecondTab(ClientHudMfscReportDetails client,
                                                Map<Long, Pair<Long, Long>> eventCounts,
                                                Map<Long, List<ComprehensiveAssessment<AssessmentHudMfscDetails>>> assessments,
                                                Map<Long, List<ServicePlanHudMfscDetailsWrapper>> servicePlans,
                                                Map<Long, Long> problemCounts,
                                                Map<CodeSystem, Map<Long, Long>> hivProblemsCount,
                                                InternalReportFilter filter,
                                                PermissionFilter permissionFilter) {
        var hudSecondTab = new HudSecondTab();

        var assessmentsSorted = assessments.getOrDefault(client.getId(), Collections.emptyList());
        var servicePlansSorted = servicePlans.getOrDefault(client.getId(), Collections.emptyList());

        hudSecondTab.setParticipantStatusCode(1);
        hudSecondTab.setPersonIdentifier(ofNullable(client.getGenacrossId()).orElse(client.getId()));
        hudSecondTab.setHouseHoldIdentifier("H" + hudSecondTab.getPersonIdentifier());
        hudSecondTab.setIntakeDate(client.getIntakeDate() != null ? client.getIntakeDate() : client.getCreatedDate());
        hudSecondTab.setAge(calculateAge(client, assessmentsSorted));
        hudSecondTab.setGenderCode(getGenderCode(client, assessmentsSorted));
        hudSecondTab.setEthnicityCode(resolveEthnicity(assessmentsSorted));
        hudSecondTab.setRaceCode(resolveRace(assessmentsSorted));
        hudSecondTab.setHeadOfHouseholdCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setClientCensusTract(resolveAddress(assessmentsSorted, client));
        hudSecondTab.setClientCensusTractInfoNotCollected(ifShouldFillClientCensusTractInfoNotCollected(hudSecondTab) ? 1 : null);
        hudSecondTab.setVeteranStatusCode(resolveVeteranStatusCode(assessmentsSorted));
        hudSecondTab.setYearsInHousingNumber(null);
        hudSecondTab.setYearsInHousingNumberInfoNotCollected(1);
        hudSecondTab.setDisabilityStatusCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setDisabilityCategoryCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setDisabilityRequiresAssistanceCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setHardToHouseCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setReturningCitizenCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setEarnedIncomeTaxCreditCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setFinancialAccountCreationCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setSnapCode(resolveSnap(assessmentsSorted));
        hudSecondTab.setTanfCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setSsiCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setSsdiCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setSubstanceAbuseTreatmentCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setAidsHiv(hasHiv(client) ? 1 : INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setAdlCount(getADLCount(assessmentsSorted));
        if (hudSecondTab.getAdlCount() == null) {
            hudSecondTab.setAdlCountInfoNotCollected(1);
        }
        hudSecondTab.setIadlCount(getIADLCount(assessmentsSorted));
        if (hudSecondTab.getIadlCount() == null) {
            hudSecondTab.setIadlCountInfoNotCollected(1);
        }

        hudSecondTab.setServiceStartDate(getServiceStartDate(client));
        if (hudSecondTab.getServiceStartDate() == null) {
            hudSecondTab.setServiceStartDateInfoNotCollected(1);
        }
        hudSecondTab.setServiceEndDate(getServiceEndDate(client));
        if (hudSecondTab.getServiceEndDate() == null) {
            hudSecondTab.setServiceEndDateInfoNotCollected(1);
        }
        //hudSecondTab.setOpportunityAreaCensusTract(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setHousingStatusCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setPrimaryHealthCareProviderCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setHealthCoverageCode(getHealthCoverageCode(client));
        hudSecondTab.setMedicalExaminationStatusCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setHighestEducationLevelCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setEnrollmentInEduOrVocProgram(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setLicenseAttainmentCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setDegreeAttainmentCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setEmpStatusCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setEmpTypeStatusCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setEmpDateInfoNotCollected(1);
        //hudSecondTab.setOccupationCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setMonthlyPaidEarningsAmount(resolveIncome(assessmentsSorted));
        //if (StringUtils.isEmpty(hudSecondTab.getMonthlyPaidEarningsAmount())) {
        //    hudSecondTab.setMonthlyPaidEarningsAmountInfoNotCollected(1);
        //}
        hudSecondTab.setHouseHoldAnnualGrossAmountInfoNotCollected(1);
        //hudSecondTab.setHomelessStatusCode(resolveHomelessStatusCode(assessmentsSorted));
        //hudSecondTab.setWeeksHomelessCountInfoNotCollected(1);
        //hudSecondTab.setChronicHomelessStatusCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setPriorNightClientCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setIntermediateHousingStatusCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setHouseHoldHousingCostAmountInfoNotCollected(1);
        hudSecondTab.setHouseHoldTransporationCostAmountInfoNotCollected(1);
        var diagnosisCount = problemCounts.getOrDefault(client.getId(), 0L);
        hudSecondTab.setAsthmaConditionCode(diagnosisCount > 0 ? 1 : INFO_NOT_COLLECTED_CODE);

        var totalEventsCount = eventCounts.containsKey(client.getId()) ? eventCounts.get(client.getId()).getFirst() : 0L;
        var erEventsCount = eventCounts.containsKey(client.getId()) ? eventCounts.get(client.getId()).getSecond() : 0L;
        hudSecondTab.setEmergencyRoomVisitCode(resolveEmergencyRoomVisitCode(totalEventsCount, erEventsCount));
        hudSecondTab.setEmergencyRoomVisitCodeNumberTotal(hudSecondTab.getEmergencyRoomVisitCode() == 1 ? erEventsCount : null);
        hudSecondTab.setEmergencyRoomVisitCodeAsthma(hudSecondTab.getEmergencyRoomVisitCode() == 1 ? erEventsCount : null);

        //hudSecondTab.setBloodLeadTestCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setBloodLeadTestResult(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setAdultBasicEduServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setEslClassServiceCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setCareerGuidanceServiceCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setSelfDirectedJobSearchAssistCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setWorkReadinessAssistanceServiceCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setOstServiceCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setJobDevelopmentServiceCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setJobRetentionCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setFairHousingServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setTaxPreparationCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setFinancialAccountCreationServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setLegalAssistanceServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setLegalAssistanceTypeServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setFinancialEducationServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setPrehousingServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setPostHousingCode(INFO_NOT_COLLECTED_CODE);

        hudSecondTab.setFoodAndNutritionCode(resolveFoodAndNutritionServiceCode(servicePlansSorted));
        hudSecondTab.setFoodAndNutritionCodeNumbOfTimes(hudSecondTab.getFoodAndNutritionCode() == 1 ? 0L : null);
        hudSecondTab.setConflictResolutionCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setInterpretationServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setHousingRetentionCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setHouseHoldSkillsCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setNeedsAssessmentServiceCode(servicePlansSorted.size() > 0 || assessmentsSorted.size() > 0 ? 1 : 100);
        hudSecondTab.setServiceCoordinationCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setParentingSkillsCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setChildhoodEducationCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setHighSchoolCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setPostSecondaryEducationCode(INFO_NOT_COLLECTED_CODE);
        //hudSecondTab.setShelterPlacementCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setTempHousingPlacementCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setPermanentHousingPlacementCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setPermanentHousingPlacementInfoNotCollected(1);
        hudSecondTab.setIndependentLivingServiceCode(INFO_NOT_COLLECTED_CODE);

        hudSecondTab.setTransportationServiceCode(resolveTransportationServiceCode(servicePlansSorted, assessmentsSorted));
        hudSecondTab.setTransportationServiceCodeNumberOfTimes(hudSecondTab.getTransportationServiceCode() == 1 ? 0 : null);

        hudSecondTab.setHivAidsCode(INFO_NOT_COLLECTED_CODE);

        var withLatestStartDate = withLatestStartDate(servicePlansSorted, assessmentsSorted);

        var adultAssistanceCode = resolveAdultAssistanceCode(withLatestStartDate, servicePlansSorted.isEmpty());
        hudSecondTab.setAdultPersonalAssistanceCode(adultAssistanceCode);
        hudSecondTab.setAdultPersonalAssistanceCodeNumOfTimes(adultAssistanceCode == 1 ? 0 : null);

        var medicalCareCode = medicalCareCode(servicePlansSorted);
        hudSecondTab.setMedicalCareCode(medicalCareCode);
        hudSecondTab.setMedicalCareNumOfTimes(medicalCareCode == 1 ? 0 : null);

        var mentalHealthCode = mentalHealthCode(servicePlansSorted);
        hudSecondTab.setMentalHealthCode(mentalHealthCode);
        hudSecondTab.setMentalHealthCodeNumOfTimes(mentalHealthCode == 1 ? 0 : null);

        hudSecondTab.setSubstanceAbuseCode(INFO_NOT_COLLECTED_CODE);

        hudSecondTab = addAdditionalInfo(hudSecondTab, client, filter, permissionFilter, assessmentsSorted, hivProblemsCount);

        return hudSecondTab;
    }

    protected HudSecondTab addAdditionalInfo(HudSecondTab hudSecondTab, ClientHudMfscReportDetails client, InternalReportFilter filter,
                                             PermissionFilter permissionFilter, List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessmentsSorted,
                                             Map<CodeSystem, Map<Long, Long>> hivProblemsCount) {
        return hudSecondTab;
    }

    private Integer calculateAge(ClientHudMfscReportDetails client, List<ComprehensiveAssessment<AssessmentHudMfscDetails>> comprehensiveAssessments) {
        if (client.getBirthDate() != null) {
            return calculateAge(client.getBirthDate());
        }

        return assessmentWithLatestStartDate(comprehensiveAssessments)
                .map(ComprehensiveAssessment::getDateOfBirth)
                .filter(StringUtils::isNotEmpty)
                .map(dob -> LocalDate.parse(dob, DATE_FORMATTER_MM_DD_YYYY))
                .map(this::calculateAge)
                .orElse(null);
    }

    private Integer calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private int getGenderCode(ClientHudMfscReportDetails client, List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessmentsSorted) {
        if (client.getGenderCode() != null) {
            return GenderType.fromCcdCode(client.getGenderCode()).getCode();
        }

        return assessmentWithLatestStartDate(assessmentsSorted)
                .map(ComprehensiveAssessment::getGender)
                .filter(StringUtils::isNotEmpty)
                .map(GenderType::fromAssessmentValue)
                .orElse(GenderType.INFO_NOT_COLLECTED)
                .getCode();
    }

    private int resolveEthnicity(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessmentsSorted) {
        return assessmentWithLatestStartDate(assessmentsSorted)
                .map(ComprehensiveAssessment::getRace)
                .filter(StringUtils::isNotEmpty)
                .map(race -> "Hispanic or Latino".equals(race) ? EthnicityType.LATINO : EthnicityType.NOT_LATINO)
                .map(EthnicityType::getCode)
                .orElse(INFO_NOT_COLLECTED_CODE);
    }

    private int resolveRace(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessments) {
        return assessmentWithLatestStartDate(assessments)
                .map(ComprehensiveAssessment::getRace)
                .filter(StringUtils::isNotEmpty)
                .map(race -> ASSESSMENT_RACE_MAPPING.getOrDefault(race, INFO_NOT_COLLECTED_CODE))
                .orElse(INFO_NOT_COLLECTED_CODE);

    }

    private String resolveAddress(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> clientComprehensiveAssessments, ClientHudMfscReportDetails client) {
//        var latestStartDate = assessmentWithLatestStartDate(clientComprehensiveAssessments);
//        return latestStartDate
//                .filter(this::isAddressPresent)
//                .map(this::buildAddressString)
//                .orElse(buildAddressFromClient(client));

        return null; //CCN-3491
    }

    private boolean isAddressPresent(ComprehensiveAssessment comprehensiveAssessment) {
        return !StringUtils.isAllEmpty(
                comprehensiveAssessment.getStreet(),
                comprehensiveAssessment.getState(),
                comprehensiveAssessment.getCity(),
                comprehensiveAssessment.getZipCode());
    }

    public String buildAddressString(ComprehensiveAssessment ca) {
        return buildAddressString(ca.getStreet(), ca.getCity(), ca.getState(), ca.getZipCode());
    }

    private String buildAddressFromClient(Client client) {
        return CollectionUtils.emptyIfNull(client.getPerson().getAddresses())
                .stream()
                .findFirst()
                .map(this::buildAddressString)
                .orElse(null);
    }

    public String buildAddressString(PersonAddress pa) {
        return buildAddressString(pa.getStreetAddress(), pa.getCity(), pa.getState(), pa.getPostalCode());
    }

    public String buildAddressString(String street, String city, String state, String zip) {
        return Stream.of(street, city, state, zip)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(", "));
    }

    private boolean ifShouldFillClientCensusTractInfoNotCollected(HudSecondTab hudSecondTab) {
        return StringUtils.isEmpty(hudSecondTab.getClientCensusTract());
    }

    private int resolveVeteranStatusCode(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessments) {
        return assessmentWithLatestStartDate(assessments)
                .map(ComprehensiveAssessment::getVeteranStatus)
                .filter(StringUtils::isNotEmpty)
                .map(race -> RACE_MAP.getOrDefault(race, INFO_NOT_COLLECTED_CODE))
                .orElse(INFO_NOT_COLLECTED_CODE);
    }


    private int resolveSnap(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessments) {
        return assessmentWithLatestStartDate(assessments)
                .map(ca -> {
                    if (StringUtils.equalsAny("Yes", ca.getMealsOnWheels(), ca.getFoodSupport())) {
                        return SNAP_YES_CODE;
                    }
                    if (StringUtils.equalsAny("No", ca.getMealsOnWheels(), ca.getFoodSupport())) {
                        return SNAP_NO_CODE;
                    }
                    return INFO_NOT_COLLECTED_CODE;
                })
                .orElse(INFO_NOT_COLLECTED_CODE);
    }

    protected boolean hasDiagnosis(ClientHudMfscReportDetails client, CodeSystem codeSystem, Set<String> codes) {
        return problemObservationDao.existsDiagnosisForClient(client.getId(), codeSystem.getOid(), codeSystem.getNames(), codes);
    }

    private Integer getADLCount(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessments) {
        return assessmentWithLatestStartDate(assessments)
                .filter(this::hasADL)
                .stream()
                .flatMap(ca -> Stream.of(ca.getBathingADL(), ca.getDressingADL(), ca.getEatingADL(), ca.getToiletingADL()))
                .map(this::countADLs)
                .reduce((c1, c2) -> c1 + c2)
                .orElse(null);

    }

    private boolean hasADL(ComprehensiveAssessment ca) {
        return !StringUtils.isAllEmpty(ca.getBathingADL(), ca.getDressingADL(), ca.getToiletingADL(), ca.getEatingADL());
    }

    private Integer getIADLCount(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessments) {
        return assessmentWithLatestStartDate(assessments)
                .filter(this::hasIADL)
                .stream()
                .flatMap(ca -> Stream.of(ca.getMedicationsIADL(),
                        ca.getHousekeepingIADL(),
                        ca.getMealsIADL(),
                        ca.getLaundryIADL(),
                        ca.getTelephoneIADL(),
                        ca.getCookingConcerns(),
                        ca.getShoppingConcerns(),
                        ca.getPayingBills(),
                        ca.getAppointmentsAssistance()))
                .map(this::countADLs)
                .reduce((c1, c2) -> c1 + c2)
                .orElse(null);
    }

    private boolean hasIADL(ComprehensiveAssessment ca) {
        return !StringUtils.isAllEmpty(
                ca.getMedicationsIADL(),
                ca.getHousekeepingIADL(),
                ca.getMealsIADL(),
                ca.getLaundryIADL(),
                ca.getTelephoneIADL(),
                ca.getCookingConcerns(),
                ca.getShoppingConcerns(),
                ca.getPayingBills(),
                ca.getAppointmentsAssistance()
        );
    }

    private int countADLs(String value) {
        return StringUtils.equalsAny(value, SOME_ASSISTANCE, TOTAL_ASSISTANCE) ? 1 : 0;
    }

    private Instant getServiceStartDate(ClientHudMfscReportDetails client) {
        if (client.getIntakeDate() != null) {
            return client.getIntakeDate();
        }
        return client.getCreatedDate();
    }

    private Instant getServiceEndDate(ClientHudMfscReportDetails client) {
        if (BooleanUtils.isNotTrue(client.getActive())) {
            return client.getLastUpdated(); //todo last updated date != deactivation date
        }
        return null;
    }

    //todo check mapping file
    private Integer getHealthCoverageCode(ClientHudMfscReportDetails client) {
        String insuranceName = client.getInNetworkInsuranceDisplayName();
        if (insuranceName == null) {
            return INFO_NOT_COLLECTED_CODE;
        }
        for (var entry : insuranceMap.entrySet()) {
            if (entry.getValue().contains(insuranceName)) {
                return entry.getKey();
            }
        }
        return 8; //Other
    }

    private Integer resolveEmergencyRoomVisitCode(long totalEventsCount, long erEventsCount) {
        if (totalEventsCount > 0) {
            return erEventsCount > 0 ? 1 /*yes*/ : 2; /*no*/
        }
        return INFO_NOT_COLLECTED_CODE;
    }

    private Specification<Event> events(ClientHudMfscReportDetails client, InternalReportFilter filter, PermissionFilter permissionFilter) {
        return eventSpecificationGenerator.byClients(asList(client.getId()))
                .and(eventSpecificationGenerator.hasAccess(permissionFilter))
                .and(eventSpecificationGenerator.byEventDateTimeIn(filter.getInstantFrom(), filter.getInstantTo()));
    }

    private int resolveFoodAndNutritionServiceCode(List<ServicePlanHudMfscDetailsWrapper> servicePlansSorted) {
        var yes = 1;
        var na = 100;

        return spWithLatestStartDate(servicePlansSorted)
                .map(sp -> countNeedsWithDomain(sp, ServicePlanNeedType.NUTRITION_SECURITY) > 0 ? yes : na)
                .orElse(INFO_NOT_COLLECTED_CODE);
    }

    private int resolveTransportationServiceCode(List<ServicePlanHudMfscDetailsWrapper> servicePlans, List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessments) {
        var YES_CODE = 1;

        var hasTransportationInSP = servicePlans.stream()
                .map(sp -> countNeedsWithDomain(sp, ServicePlanNeedType.TRANSPORTATION))
                .anyMatch(count -> count > 0);

        if (hasTransportationInSP) {
            return YES_CODE;
        }

        var hasTransportationInAssessment = assessments.stream()
                .map(ComprehensiveAssessment::getTransportationAssistance)
                .anyMatch("Yes"::equalsIgnoreCase);

        if (hasTransportationInAssessment) {
            return YES_CODE;
        }

        return INFO_NOT_COLLECTED_CODE;
    }

    private int resolveAdultAssistanceCode(Pair<ServicePlanHudMfscDetailsWrapper, ComprehensiveAssessment> assessmentOrServicePlan,
                                           boolean hasNoServicePlans) {
        var YES_CODE = 1;
        var NA_CODE = 100;

        if (assessmentOrServicePlan.getFirst() != null) {
            var servicePlan = assessmentOrServicePlan.getFirst();

            var count = countNeedsWithDomain(servicePlan, ServicePlanNeedType.NUTRITION_SECURITY)
                    + countNeedsWithDomain(servicePlan, ServicePlanNeedType.SUPPORT)
                    + countNeedsWithDomain(servicePlan, ServicePlanNeedType.PHYSICAL_WELLNESS);
            if (count > 0)
                return YES_CODE;


        }
        if (assessmentOrServicePlan.getSecond() != null) {
            var assessment = assessmentOrServicePlan.getSecond();

            if (CollectionUtils.isNotEmpty(assessment.getHomeServices())) {
                if (assessment.getHomeServices().contains("Home health/telehealth")) {
                    return YES_CODE;
                }

                if (hasNoServicePlans) {
                    return NA_CODE;
                }
            }
        }

        return INFO_NOT_COLLECTED_CODE;
    }

    private int medicalCareCode(List<ServicePlanHudMfscDetailsWrapper> servicePlansSorted) {
        var YES_CODE = 1;
        var NA_CODE = 100;

        return spWithLatestStartDate(servicePlansSorted)
                .map(sp -> countNeedsWithDomain(sp, ServicePlanNeedType.SUPPORT) > 0 ? YES_CODE : NA_CODE)
                .orElse(INFO_NOT_COLLECTED_CODE);
    }

    private int mentalHealthCode(List<ServicePlanHudMfscDetailsWrapper> servicePlansSorted) {
        var YES_CODE = 1;
        var NA_CODE = 100;

        return spWithLatestStartDate(servicePlansSorted)
                .map(sp -> countNeedsWithDomain(sp, ServicePlanNeedType.MENTAL_WELLNESS) + countNeedsWithDomain(sp, ServicePlanNeedType.BEHAVIORAL))
                .map(count -> count > 0 ? YES_CODE : NA_CODE)
                .orElse(INFO_NOT_COLLECTED_CODE);
    }

    private Pair<ServicePlanHudMfscDetailsWrapper, ComprehensiveAssessment> withLatestStartDate(List<ServicePlanHudMfscDetailsWrapper> servicePlansSorted, List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessmentsSorted) {
        return Stream.concat(
                spWithLatestStartDate(servicePlansSorted).stream().map(sp -> new Pair<>(sp.getDateCreated(), sp)),
                assessmentWithLatestStartDate(assessmentsSorted).stream()
                        .map(ca -> new Pair<>(ca.getAssessmentDbRecord().getDateStarted(), ca)))
                .reduce((pair1, pair2) -> pair1.getFirst().isAfter(pair2.getFirst()) ? pair1 : pair2)
                .map(Pair::getSecond)
                .map(assessmentOrServicePlan -> {
                    if (assessmentOrServicePlan instanceof ComprehensiveAssessment) {
                        return new Pair<>((ServicePlanHudMfscDetailsWrapper) null, (ComprehensiveAssessment) assessmentOrServicePlan);
                    } else return new Pair<>((ServicePlanHudMfscDetailsWrapper) assessmentOrServicePlan, (ComprehensiveAssessment) null);
                })
                .orElse(new Pair<>());
    }


    private long countNeedsWithDomain(ServicePlanHudMfscDetailsWrapper sp, ServicePlanNeedType spType) {
        return CollectionUtils.emptyIfNull(sp.getNeeds())
                .stream()
                .map(ServicePlanNeedHudMfscDetails::getDomain)
                .filter(spType::equals)
                .count();
    }

    protected Optional<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessmentWithLatestStartDate(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessmentsSorted) {
        //pre: list is sorted by startdate asc
        return assessmentsSorted.stream().reduce((earlier, later) -> later);
    }

    private Optional<ServicePlanHudMfscDetailsWrapper> spWithLatestStartDate(List<ServicePlanHudMfscDetailsWrapper> servicePlansSorted) {
        //pre: list is sorted by startdate asc
        return servicePlansSorted.stream().reduce((earlier, later) -> later);
    }

    private Optional<ServicePlan> spWithEarliestStartDate(List<ServicePlan> servicePlansSorted) {
        //pre: list is sorted by startdate asc
        return servicePlansSorted.stream().findFirst();
    }

    @Override
    public ReportType getReportType() {
        return HUD_MFSC;
    }

    interface ClientHudMfscReportDetails extends IdAware {
        Long getGenacrossId();
        Instant getIntakeDate();
        Instant getCreatedDate();
        LocalDate getBirthDate();
        String getGenderCode();
        Boolean getActive();
        Instant getLastUpdated();
        String getInNetworkInsuranceDisplayName();
    }

    interface AssessmentHudMfscDetails extends IdAware, ClientIdAware, AssessmentScoringCalculable {
        Instant getDateStarted();
    }

    interface ServicePlanHudMfscDetails extends IdAware, ClientIdAware {
        Instant getDateCreated();
        Instant getDateCompleted();
    }

    interface ServicePlanNeedHudMfscDetails extends IdAware, ServicePlanIdAware {
        ServicePlanNeedType getDomain();
    }

    static class ServicePlanHudMfscDetailsWrapper implements ServicePlanHudMfscDetails {
        private final ServicePlanHudMfscDetails delegate;
        private List<ServicePlanNeedHudMfscDetails> needs = new ArrayList<>();

        public ServicePlanHudMfscDetailsWrapper(ServicePlanHudMfscDetails delegate) {
            this.delegate = delegate;
        }

        public Long getId() {
            return delegate.getId();
        }

        public Long getClientId() {
            return delegate.getClientId();
        }

        public Instant getDateCreated() {
            return delegate.getDateCreated();
        }

        public Instant getDateCompleted() {
            return delegate.getDateCompleted();
        }

        public List<ServicePlanNeedHudMfscDetails> getNeeds() {
            return needs;
        }

        public void setNeeds(List<ServicePlanNeedHudMfscDetails> needs) {
            this.needs = needs;
        }
    }

}