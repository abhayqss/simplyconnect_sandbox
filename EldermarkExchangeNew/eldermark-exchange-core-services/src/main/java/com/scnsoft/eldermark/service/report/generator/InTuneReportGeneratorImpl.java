package com.scnsoft.eldermark.service.report.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.projection.NamesAware;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.intune.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class InTuneReportGeneratorImpl extends DefaultReportGenerator<InTuneReport> implements InTuneReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(InTuneReportGeneratorImpl.class);

    private static final Comparator<NamesAware> CLIENT_SORT_COMPARATOR =
        Comparator.comparing(NamesAware::getLastName, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(NamesAware::getFirstName, Comparator.nullsLast(Comparator.naturalOrder()));

    private static final List<String> TRIGGER_QUESTIONS = List.of(
        "Have you had a chance to see or talk with family or friends since my last visit?",
        "Did you have enough to eat yesterday?",
        "Do you have any new aches or pains since my last visit?",
        "Have you tripped or fallen since my last visit?",
        "In the last week have you often felt sad or depressed?",
        "Are you drinking water regularly?",
        "Have you done some exercise or physical activity since my last visit?",
        "Have you seen any changes in your ankles since my last visit?",
        "Is your blood sugar being tested regularly?",
        "Do you have enough diabetic testing supplies for the next 7 days?",
        "Do you have enough of your medication for the next 7 days?",
        "May I help you contact the pharmacy or doctor for refills?",
        "Do you have a doctor's appointment coming up or can I help you schedule one today?",
        "Do you need transportation to your doctor's appointment?",
        "Is there anything we can help you with for clothing or personal care items?",
        "Do you want your care coordinator to reach out to you?"
    );

    private static final String YES_ANSWER = "Yes";
    private static final String NO_ANSWER = "No";
    private static final String NA_ANSWER = "N/A";

    @Autowired
    private ClientAssessmentResultSpecificationGenerator clientAssessmentResultSpecificationGenerator;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public InTuneReportClientInfo getClientInfo(Long clientId, PermissionFilter permissionFilter) {
        var info = new InTuneReportClientInfo();

        var lastAssessments = getLastAssessments(permissionFilter, null, clientId, 2);
        info.setHasAssessments(!lastAssessments.isEmpty());

        info.setHasChangesInTheLastTwoAssessments(
            lastAssessments.size() == 2
                && !getQuestionsThatHaveChangedAnswers(lastAssessments.get(0), lastAssessments.get(1)).isEmpty()
        );

        return info;
    }

    @Override
    public InTuneReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new InTuneReport();
        report.setSingleClientReport(false);
        populateReportingCriteriaFields(filter, report);

        var clients = clientDao.findAll(
            clientSpecificationGenerator.hasDetailsAccess(permissionFilter)
                .and(clientSpecificationGenerator.byCommunities(filter.getAccessibleCommunityIdsAndNames())),
            ClientNameAndCommunityAware.class
        );

        var rows = filter.getAccessibleCommunityIdsAndNames().stream()
            .sorted(Comparator.comparing(IdNameAware::getName, Comparator.nullsLast(Comparator.naturalOrder())))
            .map(community -> {
                var row = new InTuneReportRow();
                row.setCommunityName(community.getName());
                var communityClients = clients.stream()
                    .filter(it -> Objects.equals(it.getCommunityId(), community.getId()))
                    .collect(Collectors.toList());

                var clientRows = collectClientReportRows(permissionFilter, communityClients, filter.getInstantFrom());

                row.setClientRows(clientRows);

                return row;
            })
            .collect(Collectors.toList());

        report.setRows(rows);

        return report;
    }

    @Override
    public InTuneReport generateSingleClientReport(Long clientId, PermissionFilter permissionFilter) {
        var report = new InTuneReport();
        report.setSingleClientReport(true);
        var client = clientService.findById(clientId, ClientNameAndCommunityAware.class);
        var row = new InTuneReportRow();
        var clientRows = collectClientReportRows(permissionFilter, List.of(client), null);
        if (clientRows.isEmpty()) {
            var clientRow = new InTuneReportRowClient();
            clientRow.setClientName(client.getFullName());
            clientRow.setAnalyzedAssessmentDates(List.of());
            clientRow.setQuestions(List.of());
            row.setClientRows(List.of(clientRow));
        } else {
            row.setClientRows(clientRows);
        }
        report.setRows(List.of(row));
        return report;
    }

    private List<InTuneReportRowClient> collectClientReportRows(PermissionFilter permissionFilter, List<ClientNameAndCommunityAware> clients, Instant fromDate) {
        return clients.stream()
            .sorted(CLIENT_SORT_COMPARATOR)
            .map(client -> {
                var assessments = getLastAssessments(permissionFilter, fromDate, client.getId(), 2);
                var questions = assessments.size() == 2
                    ? getQuestionsThatHaveChangedAnswers(assessments.get(0), assessments.get(1))
                    : List.<String>of();
                return Pair.of(client, questions);
            })
            .filter(clientAndQuestions -> !clientAndQuestions.getSecond().isEmpty())
            .map(clientAndQuestions -> {
                var client = clientAndQuestions.getFirst();
                var questions = clientAndQuestions.getSecond();

                var assessmentMap = getLastAssessments(permissionFilter, fromDate, client.getId(), 7).stream()
                    .collect(Collectors.toMap(IdAware::getId, it -> it));

                var parsedAssessmentMap = assessmentMap.entrySet().stream()
                    .map(it -> Pair.of(it.getKey(), parseAssessment(it.getValue())))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

                var row = new InTuneReportRowClient();
                row.setClientName(client.getFullName());
                row.setAnalyzedAssessmentDates(assessmentMap.values().stream().map(AssessmentDetails::getDateCompleted).collect(Collectors.toList()));

                row.setQuestions(
                    questions.stream()
                        .map(question -> {
                            var questionRow = new InTuneReportRowQuestion();
                            questionRow.setQuestion(question);

                            var assessmentsWithNoAnswer = getAssessmentIdsWithAnswers(parsedAssessmentMap, question, NO_ANSWER);
                            questionRow.setNoAnswerCount(assessmentsWithNoAnswer.size());
                            questionRow.setNoAnswerDates(
                                assessmentsWithNoAnswer.stream()
                                    .map(assessmentMap::get)
                                    .map(AssessmentDetails::getDateCompleted)
                                    .collect(Collectors.toList())
                            );

                            var assessmentsWithYesAnswer = getAssessmentIdsWithAnswers(parsedAssessmentMap, question, YES_ANSWER);
                            questionRow.setYesAnswerCount(assessmentsWithYesAnswer.size());
                            questionRow.setYesAnswerDates(
                                assessmentsWithYesAnswer.stream()
                                    .map(assessmentMap::get)
                                    .map(AssessmentDetails::getDateCompleted)
                                    .collect(Collectors.toList())
                            );

                            return questionRow;
                        })
                        .collect(Collectors.toList())
                );

                return row;
            })
            .collect(Collectors.toList());
    }

    private List<Long> getAssessmentIdsWithAnswers(Map<Long, JsonNode> assessmentMap, String question, String answer) {
        return assessmentMap.entrySet().stream()
            .filter(it -> {
                var assessmentId = it.getKey();
                var assessment = it.getValue();
                return StringUtils.equalsIgnoreCase(answer, getAnswer(assessmentId, assessment, question));
            })
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private List<AssessmentDetails> getLastAssessments(PermissionFilter permissionFilter, Instant fromDate, Long clientId, int count) {
        var spec =
            clientAssessmentResultSpecificationGenerator.hasAccess(permissionFilter)
                .and(clientAssessmentResultSpecificationGenerator.ofClient(clientId))
                .and(clientAssessmentResultSpecificationGenerator.completed())
                .and(clientAssessmentResultSpecificationGenerator.byType(Assessment.IN_TUNE));

        if (fromDate != null) spec = spec.and(clientAssessmentResultSpecificationGenerator.completedFromDate(fromDate));

        return clientAssessmentDao.findAll(spec, AssessmentDetails.class, Sort.by(Sort.Order.desc(ClientAssessmentResult_.DATE_COMPLETED)), count);
    }

    private List<String> getQuestionsThatHaveChangedAnswers(AssessmentDetails assessment1, AssessmentDetails assessment2) {
        var parsedAssessment1 = parseAssessment(assessment1);
        var parsedAssessment2 = parseAssessment(assessment2);
        return TRIGGER_QUESTIONS.stream()
            .filter(question -> !StringUtils.equalsIgnoreCase(
                getAnswer(assessment1.getId(), parsedAssessment1, question),
                getAnswer(assessment2.getId(), parsedAssessment2, question)
            ))
            .collect(Collectors.toList());
    }

    private String getAnswer(Long assessmentId, JsonNode assessment, String question) {
        var answer = assessment.get(question);
        if (answer != null) {
            return answer.textValue();
        } else {
            logger.warn("Assessment result #{} doesn't have answer on '{}' question", assessmentId, question);
            return NA_ANSWER;
        }
    }

    private JsonNode parseAssessment(AssessmentDetails assessment) {
        try {
            return objectMapper.readTree(assessment.getResult());
        } catch (JsonProcessingException e) {
            throw new InternalServerException(InternalServerExceptionType.ASSESSMENT_EXPORT_FAILURE, e);
        }
    }

    private interface AssessmentDetails extends IdAware {
        String getResult();
        Instant getDateCompleted();
    }
}
