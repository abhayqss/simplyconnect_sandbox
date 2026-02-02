package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.AssessmentScoringGroupDao;
import com.scnsoft.eldermark.entity.AssessmentScoringGroup;
import com.scnsoft.eldermark.shared.carecoordination.assessments.SlumsAssessmentScoringGroupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SlumsAssessmentScoringGroupServiceImpl implements SlumsAssessmentScoringGroupService {

    @Autowired
    private AssessmentScoringGroupDao assessmentScoringGroupDao;

    @Override
    public Page<SlumsAssessmentScoringGroupDto> getScoringGroups(Long assessmentId, Pageable pageable) {
        Page<AssessmentScoringGroup> scoringGroups = assessmentScoringGroupDao.getAllByAssessment_IdOrderByScoreLowAsc(assessmentId, pageable);
        Map<String, SlumsAssessmentScoringGroupDto> slumsScoringGroupsMap = new HashMap();
        for (AssessmentScoringGroup sc : scoringGroups.getContent()){
            String severityShort = sc.getSeverityShort();
            SlumsAssessmentScoringGroupDto slumsAssessment;
            if (slumsScoringGroupsMap.get(severityShort) == null){
                slumsAssessment = new SlumsAssessmentScoringGroupDto();
                slumsAssessment.setSeverityShort(severityShort);
                populateScoreRange(slumsAssessment, sc);
            } else {
                slumsAssessment = slumsScoringGroupsMap.get(severityShort);
                populateScoreRange(slumsAssessment, sc);
            }
            slumsScoringGroupsMap.put(severityShort, slumsAssessment);
        }
        List<SlumsAssessmentScoringGroupDto> slumsScoringGroupList = new ArrayList<>();
        slumsScoringGroupList.addAll(slumsScoringGroupsMap.values());
        sortAssessmentGroups(slumsScoringGroupList);
        return new PageImpl<>(slumsScoringGroupList, pageable, slumsScoringGroupsMap.size());
    }

    private void populateScoreRange(SlumsAssessmentScoringGroupDto slumsAssessment, AssessmentScoringGroup assessmentScoringGroup){
        if (assessmentScoringGroup.getPassedHighEducation()){
            slumsAssessment.setScoreForPatientsWithHighSchoolEducation(calculateScoreRange(assessmentScoringGroup));
        } else {
            slumsAssessment.setScoreForPatientsWithoutHighSchoolEducation(calculateScoreRange(assessmentScoringGroup));
        }
    }

    private String calculateScoreRange(AssessmentScoringGroup assessmentScoringGroup){
        return assessmentScoringGroup.getScoreLow().toString() + "-" + assessmentScoringGroup.getScoreHigh().toString();
    }

    private void sortAssessmentGroups(List<SlumsAssessmentScoringGroupDto> slumsAssessmentGroups){
        Collections.sort(slumsAssessmentGroups,new Comparator<SlumsAssessmentScoringGroupDto>() {

            @Override
            public int compare(SlumsAssessmentScoringGroupDto a, SlumsAssessmentScoringGroupDto b) {
                Integer highScoreA = parseValueFromString(a.getScoreForPatientsWithHighSchoolEducation());
                Integer highScoreB = parseValueFromString(b.getScoreForPatientsWithHighSchoolEducation());
                if (highScoreA == highScoreB) {
                    return 0;
                }
                return highScoreA > highScoreB ? 1 : -1;
            }
        });
    }

    private Integer parseValueFromString(String scoreRange){
        return Integer.parseInt(scoreRange.split("-")[0]);
    }

}
