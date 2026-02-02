package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.AssessmentScoringGroupDao;
import com.scnsoft.eldermark.entity.AssessmentScoringGroup;
import com.scnsoft.eldermark.shared.carecoordination.assessments.SADPersonsAssessmentScoringResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SADPersonsScoringResultServiceImpl implements SADPersonsScoringResultService {

    @Autowired
    private AssessmentScoringGroupDao assessmentScoringGroupDao;

    @Override
    public Page<SADPersonsAssessmentScoringResult> getScoringGroups(Long assessmentId, Pageable pageable) {
        Page<AssessmentScoringGroup> scoringGroups = assessmentScoringGroupDao.getAllByAssessment_IdOrderByScoreLowAsc(assessmentId, pageable);
        List<SADPersonsAssessmentScoringResult> sadScoringResultList = new ArrayList<>();
        for (AssessmentScoringGroup sc : scoringGroups){
            SADPersonsAssessmentScoringResult scoringResult = new SADPersonsAssessmentScoringResult();
            scoringResult.setScoreRange(sc.getScoreLow().toString() + "-" + sc.getScoreHigh().toString());
            scoringResult.setDescription(sc.getSeverity());
            sadScoringResultList.add(scoringResult);
        }
        return new PageImpl<>(sadScoringResultList, pageable, sadScoringResultList.size());
    }


}
