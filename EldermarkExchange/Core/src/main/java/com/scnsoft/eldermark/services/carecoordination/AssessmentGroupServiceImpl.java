package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.carecoordination.AssessmentGroupDao;
import com.scnsoft.eldermark.entity.Assessment;
import com.scnsoft.eldermark.entity.AssessmentGroup;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentGroupDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssessmentGroupServiceImpl implements AssessmentGroupService {

    @Autowired
    private AssessmentGroupDao assessmentGroupDao;

    @Override
    public List<AssessmentGroupDto> getAllAssessmentGroups(Long patientDatabaseId) {
        return convertList(assessmentGroupDao.findAll(), patientDatabaseId);
    }

    private List<AssessmentGroupDto> convertList(List<AssessmentGroup> assessmentGroups, Long patientDatabaseId) {
        List<AssessmentGroupDto> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assessmentGroups)) {
            for (AssessmentGroup assessmentGroup : assessmentGroups) {
                AssessmentGroupDto assessmentGroupDto = convert(assessmentGroup, patientDatabaseId);
                if (CollectionUtils.isNotEmpty(assessmentGroupDto.getAssessments())) {
                    result.add(assessmentGroupDto);
                }
            }
        }
        return result;
    }

    private AssessmentGroupDto convert(AssessmentGroup assessmentGroup, Long patientDatabaseId) {
        AssessmentGroupDto result = new AssessmentGroupDto();
        result.setId(assessmentGroup.getId());
        result.setName(assessmentGroup.getName());
        List<Pair<Long, String>> assessments = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assessmentGroup.getAssessments())) {
            for (Assessment assessment : assessmentGroup.getAssessments()) {
                //TODO quick fix for LSA, revise the logic in affiliated view and refactor it in general
                if (CollectionUtils.isNotEmpty(assessment.getDatabases())) {
                    boolean sharedWithPatient = false;
                    for (Database database : assessment.getDatabases()) {
                        if (database.getId().equals(patientDatabaseId) && !SecurityUtils.isAffiliatedView()) {
                            sharedWithPatient = true;
                            break;
                        }
                    }
                    if (!sharedWithPatient) {
                        continue;
                    }
                }
                Pair<Long, String> assessmentDto = new Pair<>();
                assessmentDto.setFirst(assessment.getId());
                assessmentDto.setSecond(assessment.getName());
                assessments.add(assessmentDto);
            }
        }
        result.setAssessments(assessments);
        return result;
    }
}
