package com.scnsoft.eldermark.service.report.generator;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.projection.StringResultAware;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.EncounterNoteDao;
import com.scnsoft.eldermark.dao.specification.*;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;
import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.entity.note.NoteSubType;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class DefaultReportGenerator<R extends Report> implements ReportGenerator<R> {

    private static final DateTimeFormatter ASSESSMENT_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a z");

    protected static final String FACE_TO_FACE_VISIT_NOTE_TYPE_CODE = "FACE_TO_FACE_VISIT";

    protected static final String ATTEMPT_TO_CONTACT_NOTE_TYPE = "ATTEMPT_TO_CONTACT";

    protected static final String CARE_COORDINATION_NOTE_TYPE = "CARE_COORDINATION";

    protected static final String PHONE_CALL_EMAIL_NOTE_TYPE = "PHONE_CALL_EMAIL";

    protected static final String PRES_MGMT_RECON_NOTE_TYPE = "PRES_MGMT_RECON";

    protected static final List<String> NON_FACE_TO_FACE_CODE_TYPES = asList(
            ATTEMPT_TO_CONTACT_NOTE_TYPE,
            CARE_COORDINATION_NOTE_TYPE,
            PHONE_CALL_EMAIL_NOTE_TYPE,
            PRES_MGMT_RECON_NOTE_TYPE
    );

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CommunitySpecificationGenerator communitySpecificationGenerator;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private EncounterNoteDao encounterNoteDao;

    @Autowired
    private EncounterNoteSpecificationGenerator encounterNoteSpecificationGenerator;

    @Autowired
    protected ClientAssessmentResultSpecificationGenerator assessmentResultSpecifications;

    @Autowired
    protected ServicePlanSpecificationGenerator servicePlanSpecifications;

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    @Autowired
    private ClientDao clientDao;


    protected void populateReportingCriteriaFields(InternalReportFilter filter, Report report) {
        report.setDateFrom(filter.getInstantFrom());
        report.setDateTo(filter.getInstantTo());
        report.setCommunityNames(filter.getAccessibleCommunityIdsAndNames().stream().map(IdNameAware::getName).collect(toList()));
        report.setReportType(this.getReportType());
    }

    protected List<EncounterNote> findEncounterNotes(InternalReportFilter filter, PermissionFilter permissionFilter, NoteSubType.EncounterCode encounterCode, List<String> noteTypes) {
        var hasAccess = encounterNoteSpecificationGenerator.hasAccessAndDistinctIgnoringNotViewable(permissionFilter);
        var latest = encounterNoteSpecificationGenerator.leaveLatestFilteredByDates(filter.getInstantFrom(), filter.getInstantTo());

        var noteSpecification = encounterNoteSpecificationGenerator.getByEncounterCodeAndNoteTypesAndCommunityIds(encounterCode, noteTypes, filter.getAccessibleCommunityIdsAndNames())
                .and(hasAccess)
                .and(latest);

        return encounterNoteDao.findAll(noteSpecification);
    }

    protected List<EncounterNoteFirstTab> generateEncNoteFirstTabList(List<EncounterNote> encounterNoteList, Map<Long, ClientNameAndCommunityAware> clientNamesAndCommunitiesByIds, Map<Long, Set<Long>> clientIdsByNoteIdMap) {
        return encounterNoteList.stream().map(encNote -> {
            EncounterNoteFirstTab encounterNoteFirstTab = new EncounterNoteFirstTab();

            encounterNoteFirstTab.setClientNames(listNames(clientIdsByNoteIdMap.get(encNote.getId()).stream().map(clientNamesAndCommunitiesByIds::get).filter(Objects::nonNull).collect(toList())));
            encounterNoteFirstTab.setClientIds(clientIdsByNoteIdMap.get(encNote.getId()).stream().map(Object::toString).collect(Collectors.joining(", ")));

            //community should be the same even in case of group notes
            encounterNoteFirstTab.setCommunityName(clientIdsByNoteIdMap.get(encNote.getId()).stream().findFirst()
                    .map(clientNamesAndCommunitiesByIds::get)
                    .map(ClientNameAndCommunityAware::getCommunityName).orElse(null));

            encounterNoteFirstTab.setServiceCoordinatorName(encNote.getClinicianCompletingEncounter() != null ?
                    encNote.getClinicianCompletingEncounter().getFullName() : encNote.getOtherClinicianCompletingEncounter());
            encounterNoteFirstTab.setTotalTimeSpent(encNote.getEncounterFromTime().until(encNote.getEncounterToTime(), MINUTES));
            return encounterNoteFirstTab;
        }).collect(toList());
    }

    protected List<EncounterNoteSecondTab> generateEncNoteSecondTabList(List<EncounterNote> encounterNoteList, Map<Long, ClientNameAndCommunityAware> clientNamesAndCommunitiesByIds, Map<Long, Set<Long>> clientIdsByNoteIdMap) {
        return encounterNoteList.stream().map(encNote -> {
            EncounterNoteSecondTab encounterNoteSecondTab = new EncounterNoteSecondTab();
            encounterNoteSecondTab.setClientNames(listNames(clientIdsByNoteIdMap.get(encNote.getId()).stream().map(clientNamesAndCommunitiesByIds::get).filter(Objects::nonNull).collect(toList())));
            encounterNoteSecondTab.setClientIds(clientIdsByNoteIdMap.get(encNote.getId()).stream().map(Object::toString).collect(Collectors.joining(", ")));

            //community should be the same even in case of group notes
            encounterNoteSecondTab.setCommunityName(clientIdsByNoteIdMap.get(encNote.getId()).stream().findFirst()
                    .map(clientNamesAndCommunitiesByIds::get)
                    .map(ClientNameAndCommunityAware::getCommunityName).orElse(null));

            encounterNoteSecondTab.setFromTime(encNote.getEncounterFromTime());
            encounterNoteSecondTab.setToTime(encNote.getEncounterToTime());
            encounterNoteSecondTab.setTimeSpent(encNote.getEncounterFromTime().until(encNote.getEncounterToTime(), MINUTES));
            encounterNoteSecondTab.setServiceCoordinatorName(encNote.getClinicianCompletingEncounter() != null ?
                    encNote.getClinicianCompletingEncounter().getFullName() : encNote.getOtherClinicianCompletingEncounter());
            encounterNoteSecondTab.setSubjective(encNote.getSubjective());
            encounterNoteSecondTab.setObjective(encNote.getObjective());
            encounterNoteSecondTab.setAssessment(encNote.getAssessment());
            encounterNoteSecondTab.setPlan(encNote.getPlan());
            return encounterNoteSecondTab;
        }).collect(toList());
    }

    protected Map<String, List<TotalClientsTab>> generateTotalClientsTab(List<EncounterNote> encounterNoteList, Map<Long, ClientNameAndCommunityAware> clientNamesAndCommunitiesByIds, Map<Long, Set<Long>> clientIdsByNoteIdMap) {
        var encounterMap = encounterNoteList
                .stream()
                .flatMap(note -> clientIdsByNoteIdMap.get(note.getId()).stream().map(clientId -> new Pair<>(clientId, note)))
                .collect(groupingBy(Pair::getFirst, mapping(Pair::getSecond, toList())));
        List<TotalClientsTab> totalClientsTabList = new ArrayList<>();
        for (Map.Entry<Long, List<EncounterNote>> entry : encounterMap.entrySet()) {
            TotalClientsTab totalClientsTab = new TotalClientsTab();
            totalClientsTab.setClientName(clientNamesAndCommunitiesByIds.get(entry.getKey()).getFullName());
            totalClientsTab.setClientId(entry.getKey());
            totalClientsTab.setCommunityName(clientNamesAndCommunitiesByIds.get(entry.getKey()).getCommunityName());
            totalClientsTab.setInPersonTimeWithIndividualsTotalMin(countTotalTimeSpent(entry.getValue(), NoteSubType.EncounterCode.FACE_TO_FACE_ENCOUNTER));
            totalClientsTab.setPhoneCallTimeWithIndividualsOrServicesTotalMin(countTotalTimeSpent(entry.getValue(), NoteSubType.EncounterCode.NON_FACE_TO_FACE_ENCOUNTER));
            totalClientsTab.setTotalMinutes(totalClientsTab.getInPersonTimeWithIndividualsTotalMin() + totalClientsTab.getPhoneCallTimeWithIndividualsOrServicesTotalMin());
            totalClientsTabList.add(totalClientsTab);
        }
        return totalClientsTabList.stream()
                .collect(groupingBy(TotalClientsTab::getCommunityName));
    }

    protected Map<String, List<TotalServiceCoordinatorsTab>> generateTotalServiceCoordinatorsTab(List<EncounterNote> encounterNoteList, Map<Long, ClientNameAndCommunityAware> clientNamesAndCommunitiesByIds, Map<Long, Set<Long>> clientIdsByNoteIdMap) {
        var encounterToClientCommunitiesMap = encounterNoteList
                .stream()
                .filter(encNote -> isNotBlank(encNote.getClinicianCompletingEncounter() != null ?
                        encNote.getClinicianCompletingEncounter().getFullName() : encNote.getOtherClinicianCompletingEncounter()))
                .collect(groupingBy(encounterNote -> CareCoordinationUtils.trimAndRemoveMultipleSpaces(encounterNote.getClinicianCompletingEncounter() != null ?
                                encounterNote.getClinicianCompletingEncounter().getFullName() : encounterNote.getOtherClinicianCompletingEncounter()),
                        mapping(encounterNote -> clientNamesAndCommunitiesByIds.get(clientIdsByNoteIdMap.get(encounterNote.getId()).stream().findFirst().orElseThrow()).getCommunityName(), toSet())));

        List<TotalServiceCoordinatorsTab> totalServiceCoordinatorsTabList = new ArrayList<>();
        for (var entry : encounterToClientCommunitiesMap.entrySet()) {
            entry.getValue().forEach(communityName -> {
                TotalServiceCoordinatorsTab totalSCTab = new TotalServiceCoordinatorsTab();
                totalSCTab.setServiceCoordinatorName(entry.getKey());
                totalSCTab.setCommunityName(communityName);
                totalSCTab.setInPersonTimeWithIndividualsTotalMin(countTotalTimeSpent(encounterNoteList, NoteSubType.EncounterCode.FACE_TO_FACE_ENCOUNTER, entry.getKey(), communityName, clientNamesAndCommunitiesByIds, clientIdsByNoteIdMap));
                totalSCTab.setPhoneCallTimeWithIndividualsTotalMin(countTotalTimeSpent(encounterNoteList, NoteSubType.EncounterCode.NON_FACE_TO_FACE_ENCOUNTER, entry.getKey(), communityName, clientNamesAndCommunitiesByIds, clientIdsByNoteIdMap));
                totalSCTab.setTotalMin(totalSCTab.getInPersonTimeWithIndividualsTotalMin() + totalSCTab.getPhoneCallTimeWithIndividualsTotalMin());
                totalServiceCoordinatorsTabList.add(totalSCTab);
            });
        }
        return totalServiceCoordinatorsTabList
                .stream()
                .collect(groupingBy(TotalServiceCoordinatorsTab::getCommunityName));
    }

    protected Map<Long, Set<Long>> getClientIdsByEncounterNoteIdMap(List<EncounterNote> encounterNoteList) {
        var encounterNoteIds = encounterNoteList.stream().map(EncounterNote::getId).collect(Collectors.toList());
        return encounterNoteDao.findClientIdsByEncounterNoteIdMap(encounterNoteIds);
    }

    protected Map<Long, ClientNameAndCommunityAware> getClientNameAndCommunityByIdMap(Map<Long, Set<Long>> clientIdsByEncounterNoteId) {
        List<Long> clientIds = clientIdsByEncounterNoteId.entrySet().stream().map(Map.Entry::getValue).flatMap(Set::stream).collect(Collectors.toList());
        return Optional.ofNullable(clientDao.findAllInBatches((ids) -> clientSpecificationGenerator.byIds(clientIds), clientIds, ClientNameAndCommunityAware.class))
                .map(clientNamesAndCommunities -> clientNamesAndCommunities.stream().collect(Collectors.toMap(ClientNameAndCommunityAware::getId, Function.identity(), (o, o2) -> o)))
                .orElse(new HashMap<>());
    }

    private Long countTotalTimeSpent(List<EncounterNote> encounterNoteList, NoteSubType.EncounterCode encounterCode) {
        return encounterNoteList.stream()
                .filter(encNote -> encNote.getSubType().getEncounterCode().equals(encounterCode))
                .mapToLong(this::countTotalTimeSpent)
                .sum();
    }

    private Long countTotalTimeSpent(List<EncounterNote> encounterNoteList, NoteSubType.EncounterCode encounterCode, String serviceCoordinatorName,
                                     String communityName,  Map<Long, ClientNameAndCommunityAware> clientNamesAndCommunitiesByIds, Map<Long, Set<Long>> clientIdsByNoteIdMap) {
        return encounterNoteList.stream()
                .filter(encNote -> encNote.getSubType().getEncounterCode().equals(encounterCode) &&
                        serviceCoordinatorName.equals(CareCoordinationUtils.trimAndRemoveMultipleSpaces(encNote.getClinicianCompletingEncounter() != null ?
                                encNote.getClinicianCompletingEncounter().getFullName() : encNote.getOtherClinicianCompletingEncounter())))
                .filter(encounterNote -> clientNamesAndCommunitiesByIds.get(clientIdsByNoteIdMap.get(encounterNote.getId()).stream().findFirst().orElseThrow()).getCommunityName().equalsIgnoreCase(communityName))
                .mapToLong(this::countTotalTimeSpent)
                .sum();
    }

    private Long countTotalTimeSpent(EncounterNote encNote) {
        if (encNote.getEncounterFromTime() == null || encNote.getEncounterToTime() == null) {
            return 0L;
        }
        return Duration.between(encNote.getEncounterFromTime(), encNote.getEncounterToTime()).toMinutes();
    }

    protected <T extends StringResultAware> ComprehensiveAssessment<T> parseComprehensive(T assessmentResult) {
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(ComprehensiveAssessment.class, assessmentResult.getClass());
            ComprehensiveAssessment<T> parsed = mapper.readerFor(type).readValue(assessmentResult.getResult());
            parsed.setAssessmentDbRecord(assessmentResult);
            return parsed;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Optional<Instant> parseAssessmentDate(String s) {
        //possible inputs:
        //"12/01/2019 02:01 PM +03:00"
        //"04/21/2020 10:06 AM -05:00"
        //"05/22/2019 09:51 AM (MST)"
        //"03/01/2020 12:06 PM (GMT)"
        if (StringUtils.isNotEmpty(s)) {
            s = s.replace("(", "").replace(")", "");
            try {
                return Optional.of(Instant.from(ASSESSMENT_DATE_FORMAT.parse(s)));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return Optional.empty();
    }

    private String listNames(Collection<ClientNameAndCommunityAware> clients) {
        return clients.stream().map(ClientNameAndCommunityAware::getFullName).filter(StringUtils::isNotEmpty).collect(Collectors.joining(", "));
    }

    protected Specification<ClientAssessmentResult> latestAccessibleAssessmentWithinPeriod(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = assessmentResultSpecifications.hasAccess(permissionFilter);
        var betweenDates = assessmentResultSpecifications.withinReportPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var latest = assessmentResultSpecifications.leaveLatest(filter.getInstantTo());
        return hasAccess.and(betweenDates).and(latest);
    }

    protected Specification<ServicePlan> latestAccessibleSpWithinPeriod(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasSPAccess = servicePlanSpecifications.hasAccess(permissionFilter);
        var spWithinPeriod = servicePlanSpecifications.withinReportPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var latestSP = servicePlanSpecifications.leaveLatest(filter.getInstantTo());
        return hasSPAccess.and(spWithinPeriod).and(latestSP);
    }

    protected Specification<ServicePlanNeed> latestAccessibleNeedsWithinPeriod(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasNeedAccess = servicePlanSpecifications.hasAccessToNeed(permissionFilter);
        var needsWithinPeriod = servicePlanSpecifications.needsWithinReportPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var latestNeeds = servicePlanSpecifications.leaveLatestNeeds(filter.getInstantTo());
        return hasNeedAccess.and(needsWithinPeriod).and(latestNeeds);
    }

    protected Specification<ServicePlanGoal> latestAccessibleGoalsWithinPeriod(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasGoalAccess = servicePlanSpecifications.hasAccessToGoal(permissionFilter);
        var goalsWithinPeriod = servicePlanSpecifications.goalsWithinReportPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var latestGoals = servicePlanSpecifications.leaveLatestGoals(filter.getInstantTo());
        return hasGoalAccess.and(goalsWithinPeriod).and(latestGoals);
    }

    protected String resolveClientStatus(Boolean active) {
        if (active == null) {
            return "Unknown";
        }
        return BooleanUtils.isTrue(active) ? "Active" : "Inactive";
    }
}
