package com.scnsoft.eldermark.dump.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dump.dao.AssessmentDao;
import com.scnsoft.eldermark.dump.dao.AssessmentScoringValueDao;
import com.scnsoft.eldermark.dump.entity.assessment.Assessment;
import com.scnsoft.eldermark.dump.entity.assessment.AssessmentScoringValue;
import com.scnsoft.eldermark.dump.entity.assessment.ClientAssessmentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AssessmentScoringServiceImpl implements AssessmentScoringService {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private AssessmentDao assessmentDao;

    @Autowired
    private AssessmentScoringValueDao assessmentScoringValueDao;

    @Override
    public Long calculateScore(Long assessmentId, String resultJson) {
        try {
            Assessment assessment = assessmentDao.getOne(assessmentId);
            Map<String, String> questionAnswerMap = mapper.readValue(resultJson,
                    new TypeReference<HashMap<String, String>>() {
                    });
            List<AssessmentScoringValue> assessmentScoringValues = assessmentScoringValueDao.getAllByAssessment(assessment);

            return questionAnswerMap.entrySet().stream()
                    .mapToLong(x -> findScoringValue(assessmentScoringValues, x.getKey(), x.getValue()))
                    .sum();
        } catch (IOException e) {
            return 0L;
        }
    }

    @Override
    public Long calculateScore(ClientAssessmentResult clientAssessmentResult) {
        return calculateScore(clientAssessmentResult.getAssessment().getId(), clientAssessmentResult.getResult());
    }

    private Long findScoringValue(List<AssessmentScoringValue> assessmentScoringValues, String question,
                                  String answer) {
        return assessmentScoringValues.stream()
                .filter(x -> x.getAnswerName().equalsIgnoreCase(answer)
                        && (x.getQuestionName() == null || x.getQuestionName().equalsIgnoreCase(question))
                        && x.getValue() != null)
                .findAny().map(AssessmentScoringValue::getValue).orElse(0L);
    }
}
