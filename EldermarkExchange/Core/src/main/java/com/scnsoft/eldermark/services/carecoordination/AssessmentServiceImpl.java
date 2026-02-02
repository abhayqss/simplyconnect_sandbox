package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.AssessmentDao;
import com.scnsoft.eldermark.entity.Assessment;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentDao assessmentDao;

    @Override
    public AssessmentDto getAssessmentDetails(Long assessmentId) {
        Assessment assessment = assessmentDao.findOne(assessmentId);
        return convert(assessment);
    }

    private AssessmentDto convert(Assessment assessment) {
        AssessmentDto result = new AssessmentDto();
        result.setJsonContent(assessment.getContent());
        result.setAssessmentId(assessment.getId());
        result.setName(assessment.getName());
        result.setScoringEnabled(assessment.getScoringEnabled() == null ? Boolean.FALSE : assessment.getScoringEnabled());
        result.setShortName(assessment.getShortName());
        result.setHasNumeration(assessment.getHasNumeration());
        result.setType(assessment.getType());
        return result;
    }
}
