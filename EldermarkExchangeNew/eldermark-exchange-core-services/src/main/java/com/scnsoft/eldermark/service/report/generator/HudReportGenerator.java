package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.CodeSystem;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;
import com.scnsoft.eldermark.beans.reports.model.HudSecondTab;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.INFO_NOT_COLLECTED_CODE;
import static com.scnsoft.eldermark.beans.reports.enums.ReportType.HUD;
import static java.util.Arrays.asList;
import static java.util.Map.of;

@Service
public class HudReportGenerator extends HudMfscReportGenerator {

    private static final List<String> HIV_ICD_10_CODES = asList("B20", "B97.35", "O98.7", "O98.71", "O98.711", "O98.712", "O98.713", "O98.719", "Z71.7", "O98.72", "O98.73");
    private static final List<String> HIV_ICD_9_CODES = asList("V65.44", "V08", "042", "079.53", "647.61", "647.2", "647.63", "647.63", "647.60");
    private static final List<String> HIV_SNOMED_CT_CODES = asList("111880001", "103408004", "103411003", "103412005", "103413000", "103414006", "165816005", "402916007", "79019005", "40780007", "91947003", "86406008");

    private static final Map<CodeSystem, List<String>> HIV_CODES_MAP = of(
            CodeSystem.ICD_10_CM, HIV_ICD_10_CODES,
            CodeSystem.ICD_9_CM, HIV_ICD_9_CODES,
            CodeSystem.SNOMED_CT, HIV_SNOMED_CT_CODES
    );

    private static final Map<String, Integer> HOMELESS_STATUS_MAP = of(
            "Home", 100,
            "Apartment", INFO_NOT_COLLECTED_CODE,
            "Mobile home", INFO_NOT_COLLECTED_CODE,
            "Duplex", INFO_NOT_COLLECTED_CODE,
            "Assisted living facility (ALF)", INFO_NOT_COLLECTED_CODE,
            "Condominium", INFO_NOT_COLLECTED_CODE,
            "Group home", INFO_NOT_COLLECTED_CODE,
            "One story", INFO_NOT_COLLECTED_CODE,
            "Two story", INFO_NOT_COLLECTED_CODE,
            "Homeless", 1   //homeless
    );

    @Override
    protected HudSecondTab addAdditionalInfo(HudSecondTab hudSecondTab, ClientHudMfscReportDetails client, InternalReportFilter filter, PermissionFilter permissionFilter, List<ComprehensiveAssessment<AssessmentHudMfscDetails>> assessmentsSorted, Map<CodeSystem, Map<Long, Long>> hivProblemsCount) {
        hudSecondTab.setHardToHouseCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setReturningCitizenCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setAidsHiv(hasHiv(client, hivProblemsCount) ? 1 : INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setOpportunityAreaCensusTract(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setHousingStatusCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setEnrollmentInEduOrVocProgram(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setLicenseAttainmentCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setDegreeAttainmentCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setEmpStatusCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setEmpTypeStatusCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setEmpDateInfoNotCollected(1);
        hudSecondTab.setOccupationCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setMonthlyPaidEarningsAmount(resolveIncome(assessmentsSorted));
        if (StringUtils.isEmpty(hudSecondTab.getMonthlyPaidEarningsAmount())) {
            hudSecondTab.setMonthlyPaidEarningsAmountInfoNotCollected(1);
        }
        hudSecondTab.setHomelessStatusCode(resolveHomelessStatusCode(assessmentsSorted));
        hudSecondTab.setWeeksHomelessCountInfoNotCollected(1);
        hudSecondTab.setChronicHomelessStatusCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setPriorNightClientCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setBloodLeadTestCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setBloodLeadTestResult(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setAdultBasicEduServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setCareerGuidanceServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setSelfDirectedJobSearchAssistCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setWorkReadinessAssistanceServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setOstServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setJobDevelopmentServiceCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setJobRetentionCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setParentingSkillsCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setChildhoodEducationCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setHighSchoolCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setPostSecondaryEducationCode(INFO_NOT_COLLECTED_CODE);
        hudSecondTab.setShelterPlacementCode(INFO_NOT_COLLECTED_CODE);
        return hudSecondTab;
    }

    @Override
    protected Map<CodeSystem, Map<Long, Long>> getProblemsByCodeSystemByClient(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var problemsByCodeSystemByClient = new HashMap<CodeSystem, Map<Long, Long>>();
        var hudClients = problemObservationSpecificationGenerator.byAccessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate(permissionFilter, filter.getAccessibleCommunityIdsAndNames(), filter.getInstantTo(), filter.getInstantFrom());
        HIV_CODES_MAP.forEach((key, value) -> {
            var byCodeSystemAndCode = problemObservationSpecificationGenerator.byCodeSystemAndCode(key, value);
            var problemsByClient = problemObservationDao.countsByClientId(byCodeSystemAndCode.and(hudClients));
            problemsByCodeSystemByClient.put(key, problemsByClient);
        });
        return problemsByCodeSystemByClient;
    }

    private boolean hasHiv(ClientHudMfscReportDetails client, Map<CodeSystem, Map<Long, Long>> problemsByCodeSystemByClient) {
        return problemsByCodeSystemByClient.entrySet().stream()
                .anyMatch(codeSystemMapEntry -> codeSystemMapEntry.getValue().getOrDefault(client.getId(), 0L) > 0);
    }

    private String resolveIncome(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> clientComprehensiveAssessments) {
        return assessmentWithLatestStartDate(clientComprehensiveAssessments)
                .map(ComprehensiveAssessment::getMonthlyIncome)
                .orElse(null);
    }

    private int resolveHomelessStatusCode(List<ComprehensiveAssessment<AssessmentHudMfscDetails>> clientComprehensiveAssessments) {
        var list = assessmentWithLatestStartDate(clientComprehensiveAssessments)
                .map(ComprehensiveAssessment::getLivingConditions)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (list.containsAll(asList("Home", "Homeless"))) {
            return INFO_NOT_COLLECTED_CODE;
        }

        if (list.contains("Home")) {
            return HOMELESS_STATUS_MAP.get("Home");
        }

        if (list.contains("Homeless")) {
            return HOMELESS_STATUS_MAP.get("Homeless");
        }

        return INFO_NOT_COLLECTED_CODE;
    }

    @Override
    public ReportType getReportType() {
        return HUD;
    }
}
