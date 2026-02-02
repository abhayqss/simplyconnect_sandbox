package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentDto;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentGroupDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AssessmentFacadeImpl implements AssessmentFacade {

    @Override
    public void sortAssessmentGroups(List<AssessmentGroupDto> assessmentGroupDtos) {
        for (AssessmentGroupDto assessmentGroupDto : assessmentGroupDtos) {
            List<Pair<Long, String>> assessments = assessmentGroupDto.getAssessments();
            List<AssessmentDto> assessmentDtoList = new ArrayList<>();
            for (Pair<Long, String> pair : assessments){
                AssessmentDto assessment = new AssessmentDto();
                assessment.setAssessmentId(pair.getFirst());
                assessment.setName(pair.getSecond());
                assessmentDtoList.add(assessment);
            }

            if (assessmentDtoList.size() > 0) {
                Collections.sort(assessmentDtoList, new Comparator<AssessmentDto>() {
                    @Override
                    public int compare(final AssessmentDto object1, final AssessmentDto object2) {
                        return object1.getName().compareTo(object2.getName());
                    }
                });
            }

            List<Pair<Long, String>> pairs = new ArrayList<>();
            for (AssessmentDto assessmentDto : assessmentDtoList){
                Pair<Long, String> pair = new Pair<>();
                pair.setFirst(assessmentDto.getAssessmentId());
                pair.setSecond(assessmentDto.getName());
                pairs.add(pair);
            }
            assessmentGroupDto.setAssessments(pairs);
        }
    }

}
