package com.scnsoft.eldermark.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.projection.AssessmentScoringCalculable;
import com.scnsoft.eldermark.dao.AssessmentDao;
import com.scnsoft.eldermark.dao.AssessmentScoringGroupDao;
import com.scnsoft.eldermark.dao.AssessmentScoringValueDao;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.AssessmentScoringValue;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AssessmentScoringServiceImpl implements AssessmentScoringService {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(AssessmentScoringServiceImpl.class);

    @Autowired
    private AssessmentDao assessmentDao;

    @Autowired
    private AssessmentScoringValueDao assessmentScoringValueDao;

    @Autowired
    private AssessmentScoringGroupDao assessmentScoringGroupDao;

    @Override
    public Long calculateScore(Long assessmentId, String resultJson) {
        var assessmentScoringValues = getAssessmentScoringValuesByAssessmentId(assessmentId);
        return calculateScore(assessmentScoringValues, resultJson);
    }

    @Override
    public Long calculateScore(Long assessmentId, Map<String, Object> resultJson) {
        var assessmentScoringValues = getAssessmentScoringValuesByAssessmentId(assessmentId);
        return calculateScore(assessmentScoringValues, resultJson);
    }

    @Override
    public <A extends AssessmentScoringCalculable> List<Pair<A, Long>> calculateScores(List<A> assessmentResults) {
        if (CollectionUtils.isEmpty(assessmentResults)) {
            return Collections.emptyList();
        }

        var assessmentIds = assessmentResults.stream().map(AssessmentScoringCalculable::getAssessmentId)
                .distinct().collect(Collectors.toList());

        var scoringValuesMap = assessmentScoringValueDao.getAllByAssessmentIdIn(assessmentIds).stream()
                .collect(Collectors.groupingBy(AssessmentScoringValue::getAssessmentId));

        return assessmentResults.stream().map(assessmentResult -> {
            var assessmentScoringValues = scoringValuesMap.get(assessmentResult.getAssessmentId());
            return new Pair<>(assessmentResult, calculateScore(assessmentScoringValues, assessmentResult.getResult()));
        }).collect(Collectors.toList());
    }

    @Override
    public Long calculateScore(ClientAssessmentResult clientAssessmentResult) {
        return calculateScore(clientAssessmentResult.getAssessment().getId(), clientAssessmentResult.getResult());
    }

    private List<AssessmentScoringValue> getAssessmentScoringValuesByAssessmentId(Long assessmentId) {
        Assessment assessment = assessmentDao.getOne(assessmentId);
        return assessmentScoringValueDao.getAllByAssessment(assessment);
    }

    private Long calculateScore(List<AssessmentScoringValue> assessmentScoringValues, String resultJson) {
        try {
            var questionAnswerMap = mapper.readValue(
                resultJson,
                new TypeReference<HashMap<String, Object>>() {
                }
            );
            return calculateScore(assessmentScoringValues, questionAnswerMap);
        } catch (IOException e) {
            logger.warn("Exception during assessment result score calculation", e);
            return 0L;
        }
    }

    private Long calculateScore(List<AssessmentScoringValue> assessmentScoringValues, Map<String, Object> resultJson) {
        return resultJson.entrySet().stream()
            .filter(entry -> entry.getValue() instanceof String)
            .map(x -> findScoringValue(assessmentScoringValues, x.getKey(), (String)x.getValue()))
            .collect(Collectors.summarizingLong(x -> x)).getSum();
    }

    private Pair<Long, Map<String,String>> calculateScoreWithPositiveScoringAnswers(List<AssessmentScoringValue> assessmentScoringValues, String resultJson) {
        try {
            var result = new Pair<Long, Map<String,String>>(0L, new HashMap<>());
            var questionAnswerMap = mapper.readValue(resultJson,
                    new TypeReference<HashMap<String, Object>>() {
                    });

            questionAnswerMap.entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof String)
                    .forEach(x -> {
                        Long scoringValue = findScoringValue(assessmentScoringValues, x.getKey(), (String)x.getValue());
                        if (scoringValue > 0) {
                            result.getSecond().put(x.getKey(), (String)x.getValue());
                            result.setFirst(result.getFirst() + scoringValue);
                        }
                    });
            return result;
        } catch (IOException e) {
            logger.warn("Exception during assessment result score calculation", e);
            return new Pair<>(0L, null);
        }
    }

    @Override
    public Boolean isRiskIdentified(Long assessmentId, Long score) {
        var group = assessmentScoringGroupDao.findTop1ByAssessment_IdAndScoreLowLessThanEqualAndScoreHighGreaterThanEqual(assessmentId, score, score).orElseThrow();
        return group.getIsRiskIdentified();
    }

    @Override
    public String findSeverityOfScore(Long assessmentId, Long score) {
        var group = assessmentScoringGroupDao.findTop1ByAssessment_IdAndScoreLowLessThanEqualAndScoreHighGreaterThanEqual(assessmentId, score, score).orElseThrow();
        return group.getSeverityShort();
    }

    @Override
    public Pair<Long, Map<String, String>> calculateScoreWithPositiveScoringAnswers(ClientAssessmentResult clientAssessmentResult) {
        var assessment = clientAssessmentResult.getAssessment();
        List<AssessmentScoringValue> assessmentScoringValues = assessmentScoringValueDao
                .getAllByAssessment(assessment);
        return calculateScoreWithPositiveScoringAnswers(assessmentScoringValues, clientAssessmentResult.getResult());
    }

    private Long findScoringValue(List<AssessmentScoringValue> assessmentScoringValues, String question,
                                  String answer) {
       return assessmentScoringValues.stream()
                .filter(scoringValue -> scoringValue.getAnswerName().equalsIgnoreCase(answer)
                        && (scoringValue.getQuestionName() == null || scoringValue.getQuestionName().equalsIgnoreCase(question))
                        && scoringValue.getValue() != null)
                .findAny().map(AssessmentScoringValue::getValue).orElse(0L);
    }
}
