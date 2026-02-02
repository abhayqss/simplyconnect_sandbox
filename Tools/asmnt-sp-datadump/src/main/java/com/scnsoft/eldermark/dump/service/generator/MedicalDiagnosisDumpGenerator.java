package com.scnsoft.eldermark.dump.service.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dump.bean.ComprehensiveAssessment;
import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.dao.ClientAssessmentResultDao;
import com.scnsoft.eldermark.dump.dao.CommunityDao;
import com.scnsoft.eldermark.dump.entity.Client;
import com.scnsoft.eldermark.dump.entity.Client_;
import com.scnsoft.eldermark.dump.entity.Community;
import com.scnsoft.eldermark.dump.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.dump.model.*;
import com.scnsoft.eldermark.dump.specification.ClientAssessmentResultSpecificationGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MedicalDiagnosisDumpGenerator implements DumpGenerator {

    private static final Sort sort = Sort.by(ClientAssessmentResult_.CLIENT + "." + Client_.FIRST_NAME)
            .and(Sort.by(ClientAssessmentResult_.CLIENT + "." + Client_.LAST_NAME));

    @Autowired
    private ClientAssessmentResultSpecificationGenerator assessmentResultSpecifications;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private ClientAssessmentResultDao clientAssessmentResultDao;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<Dump> generateDump(DumpFilter filter) {
        var dump = new MedicalDiagnosisDump();
        fillDump(filter, dump);
        return Collections.singletonList(dump);
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.MEDICAL_DIAGNOSIS;
    }

    private void fillDump(DumpFilter filter, MedicalDiagnosisDump dump) {
        var withinReportPeriod = assessmentResultSpecifications.withinReportPeriod(filter.getFrom(), filter.getTo());
        var latest = assessmentResultSpecifications.leaveLatest(filter.getTo());
        var comprehensiveType = assessmentResultSpecifications.comprehensiveType();
        var communities = communityDao.findAllByOrganizationId(filter.getOrganizationId());
        communities.sort(Comparator.comparing(Community::getName));
        var dumpData = communities.stream().map(community -> {
            var ofCommunity = assessmentResultSpecifications.ofCommunity(community);
            var assessments = clientAssessmentResultDao.findAll(withinReportPeriod.and(latest.and(comprehensiveType.and(ofCommunity))), sort);
            var medicalDiagnosisInfoList = assessments.stream()
                    .map(assessment -> {
                        var comprehensiveAssessment = DumpGeneratorUtils.parseComprehensive(assessment.getResult(), mapper);
                        return convertMedicalDiagnosisInfo(comprehensiveAssessment, assessment.getClient());
                    }).filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(medicalDiagnosisInfoList)) {
                return Pair.of(community.getName(), medicalDiagnosisInfoList);
            }
            return null;
        }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        dump.setMedicalDiagnosisByCommunityNames(dumpData);
    }

    private MedicalDiagnosisInfo convertMedicalDiagnosisInfo(ComprehensiveAssessment comprehensiveAssessment, Client client) {
        var medicalDiagnosisFieldsData = convertDataByReportFields(comprehensiveAssessment);
        if (MapUtils.isEmpty(medicalDiagnosisFieldsData)) {
            return null;
        }
        return new MedicalDiagnosisInfo(client.getFullName(), medicalDiagnosisFieldsData);
    }

    private Map<MedicalDiagnosisField, String> convertDataByReportFields(ComprehensiveAssessment comprehensiveAssessment) {
        var fieldsWithValues = new HashMap<MedicalDiagnosisField, String>();
        for (var reportField : MedicalDiagnosisField.values()) {
            var value = callMethod(comprehensiveAssessment, reportField.getMethod());
            if (StringUtils.isNotEmpty(value)) {
                fieldsWithValues.put(reportField, value);
            }
        }
        return fieldsWithValues;
    }

    private String callMethod(ComprehensiveAssessment comprehensiveAssessment, Function<ComprehensiveAssessment, Object> method) {
        var value = method.apply(comprehensiveAssessment);
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof List<?>) {
            return CollectionUtils.emptyIfNull((List<?>) value).stream()
                    .map(String::valueOf)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.joining(", "));
        }
        return null;
    }
}
