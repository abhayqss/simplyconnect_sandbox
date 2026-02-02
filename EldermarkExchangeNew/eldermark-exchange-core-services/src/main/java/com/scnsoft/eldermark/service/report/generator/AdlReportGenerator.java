package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.AdlReportField;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.AdlReport;
import com.scnsoft.eldermark.beans.reports.model.AdlReportMedicalHistoryRow;
import com.scnsoft.eldermark.beans.reports.model.AdlReportRow;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.entity.assessment.ClientIdAndStringResultAware;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.reports.enums.AdlReportField.*;
import static com.scnsoft.eldermark.beans.reports.enums.ReportType.ADL_REPORT;

@Service
@Transactional(readOnly = true)
public class AdlReportGenerator extends DefaultReportGenerator<AdlReport> {

    private static final List<String> ASSISTANCE_LIST = List.of("Some assistance", "Total assistance");

    private static final Sort sort = Sort.by(
            CareCoordinationUtils.concat(".", ClientAssessmentResult_.CLIENT, Client_.COMMUNITY, Community_.NAME))
            .and(Sort.by(Sort.Direction.DESC, ClientAssessmentResult_.DATE_STARTED))
            .and(Sort.by(CareCoordinationUtils.concat(".", ClientAssessmentResult_.CLIENT, Client_.FIRST_NAME))
                    .and(Sort.by(CareCoordinationUtils.concat(".", ClientAssessmentResult_.CLIENT, Client_.LAST_NAME))));

    private static final List<AdlReportField> ADL_FIELDS = List.of(BATHING, DRESSING, TOILETING, EATING);
    private static final List<AdlReportField> IADL_FIELDS = List.of(MEDICATIONS, HOUSEKEEPING, MEALS, LAUNDRY, TELEPHONE, COOKING, SHOPPING, PAYING, MEDICATION_APPOINTMENT);
    private static final List<AdlReportField> MEDICAL_HISTORY_FIELDS = List
            .of(MEDICAL_HISTORY_CARDIAC, MEDICAL_HISTORY_PULMONARY, MEDICAL_HISTORY_DIABETIC, MEDICAL_HISTORY_NEUROLOGICAL,
                    MEDICAL_HISTORY_GASTROINTESTINA, MEDICAL_HISTORY_MUSCULOSKELETAL, MEDICAL_HISTORY_GYN_URINARY, MEDICAL_HISTORY_INFECTIOUS_DISEASE, MEDICAL_HISTORY_IMMUNE_DISORDERS,
                    MEDICAL_HISTORY_BEHAVIORAL_HEALTH, MEDICAL_HISTORY_WOUNDS, MEDICAL_HISTORY_VISION_HEARING_DENTAL);
    private static final List<AdlReportField> MEDICAL_HISTORY_CHRONIC_PAIN_FIELDS = List.of(MEDICAL_HISTORY_CHRONIC_PAIN_LOCATION, MEDICAL_HISTORY_CHRONIC_PAIN_AGITATORS,
            MEDICAL_HISTORY_CHRONIC_PAIN_SEVERITY, MEDICAL_HISTORY_CHRONIC_PAIN_LENGTH_OF_TIME, MEDICAL_HISTORY_CHRONIC_PAIN_RELIEVING_FACTORS, MEDICAL_HISTORY_CHRONIC_PAIN_COMMENT);

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ClientService clientService;

    @Override
    public AdlReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new AdlReport();
        populateReportingCriteriaFields(filter, report);
        fillReport(filter, permissionFilter, report);
        return report;
    }

    @Override
    public ReportType getReportType() {
        return ADL_REPORT;
    }

    private void fillReport(InternalReportFilter filter, PermissionFilter permissionFilter, AdlReport report) {
        var latestAccessibleAssessmentWithinPeriod = latestAccessibleAssessmentWithinPeriod(filter, permissionFilter);
        var comprehensiveType = assessmentResultSpecifications.comprehensiveType();
        var norCalComprehensiveType = assessmentResultSpecifications.byType(Assessment.NOR_CAL_COMPREHENSIVE);
        var ofCommunities = assessmentResultSpecifications.ofCommunities(filter.getAccessibleCommunityIdsAndNames());
        var assessments =
                clientAssessmentDao.findAll(
                        latestAccessibleAssessmentWithinPeriod.and((comprehensiveType.or(norCalComprehensiveType)).and(ofCommunities)),
                        ClientIdAndStringResultAware.class,
                        sort
                );
        assessments.forEach(assessment -> {
            var comprehensiveAssessment = parseComprehensive(assessment);
            addDataFromAssessment(report, comprehensiveAssessment, assessment.getClientId());
        });
    }

    private void addDataFromAssessment(AdlReport report, ComprehensiveAssessment<ClientIdAndStringResultAware> assessment, Long clientId) {
        var adlFieldsData = convertDataByReportFields(assessment, ADL_FIELDS, true);
        var iadlFieldsData = convertDataByReportFields(assessment, IADL_FIELDS, true);
        var medicalHistoryFieldsData = convertDataByReportFields(assessment, MEDICAL_HISTORY_FIELDS, false);
        var chronicPainFieldsData = convertDataByReportFields(assessment, MEDICAL_HISTORY_CHRONIC_PAIN_FIELDS, false);

        if (Stream.of(adlFieldsData, iadlFieldsData, medicalHistoryFieldsData, chronicPainFieldsData).allMatch(MapUtils::isEmpty)) {
            return;
        }

        var clientData = clientService.findById(clientId, ClientNameAndCommunityAware.class);
        if (MapUtils.isNotEmpty(adlFieldsData)) {
            var reportRow = createAdlReportRow(clientData, adlFieldsData);
            report.getAdlRows().add(reportRow);
        }
        if (MapUtils.isNotEmpty(iadlFieldsData)) {
            var reportRow = createAdlReportRow(clientData, iadlFieldsData);
            report.getIadlRows().add(reportRow);
        }
        if (MapUtils.isNotEmpty(medicalHistoryFieldsData) || MapUtils.isNotEmpty(chronicPainFieldsData)) {
            var reportRow = new AdlReportMedicalHistoryRow();
            reportRow.setCommunityName(clientData.getCommunityName());
            reportRow.setClientId(clientData.getId());
            reportRow.setClientName(clientData.getFullName());
            if (MapUtils.isNotEmpty(medicalHistoryFieldsData)) {
                reportRow.setFieldsWithContent(medicalHistoryFieldsData);
            }
            if (MapUtils.isNotEmpty(chronicPainFieldsData)) {
                reportRow.setChronicPainRows(new TreeMap<>(chronicPainFieldsData));
            }
            report.getMedicalHistoryRows().add(reportRow);
        }
    }

    private Map<AdlReportField, String> convertDataByReportFields(ComprehensiveAssessment<ClientIdAndStringResultAware> assessment, List<AdlReportField> reportFields, boolean withCheckAssistance) {
        var fieldsWithValues = new HashMap<AdlReportField, String>();
        reportFields.forEach(field -> {
            var value = callMethod(assessment, field.getMethod());
            if (StringUtils.isNotEmpty(value)) {
                if (withCheckAssistance) {
                    if (ASSISTANCE_LIST.contains(value)) {
                        fieldsWithValues.put(field, value);
                    }
                } else {
                    fieldsWithValues.put(field, value);
                }
            }
        });
        return fieldsWithValues;
    }

    private String callMethod(ComprehensiveAssessment<ClientIdAndStringResultAware> assessment, Function<ComprehensiveAssessment<?>, Object> method) {
        var value = method.apply(assessment);
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof List<?>) {
            return CareCoordinationUtils.concat(", ", CollectionUtils.emptyIfNull((List<?>) value).stream().map(String::valueOf));
        }
        return null;
    }

    private AdlReportRow createAdlReportRow(ClientNameAndCommunityAware clientData, Map<AdlReportField, String> fieldsWithContent) {
        var reportRow = new AdlReportRow();
        reportRow.setCommunityName(clientData.getCommunityName());
        reportRow.setClientId(clientData.getId());
        reportRow.setClientName(clientData.getFullName());
        reportRow.setFieldsWithContent(fieldsWithContent);
        return reportRow;
    }
}
