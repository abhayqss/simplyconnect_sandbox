package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.AssessmentHistoryDao;
import com.scnsoft.eldermark.dao.carecoordination.AssessmentScoringGroupDao;
import com.scnsoft.eldermark.entity.AssessmentScoringGroup;
import com.scnsoft.eldermark.entity.ResidentAssessmentResult;
import com.scnsoft.eldermark.services.converters.AssessmentHistoryConverter;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentHistoryDto;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentScoringGroupListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssessmentScoringGroupServiceImpl implements AssessmentScoringGroupService {

    @Autowired
    private AssessmentScoringGroupDao assessmentScoringGroupDao;

    @Autowired
    private AssessmentHistoryDao assessmentHistoryDao;

    @Autowired
    private AssessmentHistoryConverter assessmentHistoryConverter;

    @Override
    public Page<AssessmentScoringGroupListDto> getScoringGroups(Long assessmentId, Pageable pageable) {
        Page<AssessmentScoringGroup> result = assessmentScoringGroupDao.getAllByAssessment_IdOrderByScoreLowAsc(assessmentId, pageable);
        return new PageImpl<>(convertToListDto(result.getContent()), pageable, result.getTotalElements());
    }

    @Override
    public Page<AssessmentHistoryDto> getHistory(Long assessmentId, Pageable pageable) {
        final Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "lastModifiedDate"));
        final Pageable localPageable;
        if (pageable == null) {
            localPageable = new PageRequest(0, Integer.MAX_VALUE, sort);
        } else {
            localPageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        final ResidentAssessmentResult rar = assessmentHistoryDao.findOne(assessmentId);
        final Long searchId = rar.getChainId() != null ? rar.getChainId() : assessmentId;
        return assessmentHistoryDao.getAllByIdOrChainId(searchId, searchId, localPageable)
                .map(getAssessmentHistoryConverter());
    }

    private List<AssessmentScoringGroupListDto> convertToListDto(List<AssessmentScoringGroup> source) {
        List<AssessmentScoringGroupListDto> assessmentScoringGroupListDtos = new ArrayList<>();
        for (AssessmentScoringGroup assessmentScoringGroup : source) {
            assessmentScoringGroupListDtos.add(convert(assessmentScoringGroup));
        }
        return assessmentScoringGroupListDtos;
    }

    private AssessmentScoringGroupListDto convert(AssessmentScoringGroup assessmentScoringGroup) {
        AssessmentScoringGroupListDto dto = new AssessmentScoringGroupListDto();
        dto.setComments(assessmentScoringGroup.getComments());
        dto.setSeverityShort(assessmentScoringGroup.getSeverityShort());
        dto.setSeverity(assessmentScoringGroup.getSeverity());
        dto.setScore(generateScoreRange(assessmentScoringGroup.getScoreLow(), assessmentScoringGroup.getScoreHigh()));
        return dto;
    }

    private String generateScoreRange(Long scoreLow, Long scoreHigh){
        return scoreHigh == scoreLow ? scoreHigh.toString() : scoreLow.toString() + "-" + scoreHigh.toString();
    }

    public AssessmentHistoryConverter getAssessmentHistoryConverter() {
        return assessmentHistoryConverter;
    }
}
