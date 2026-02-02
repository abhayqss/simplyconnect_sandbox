package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ClientAssessmentCount;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomClientAssessmentResultDao {

    List<ClientAssessmentCount> countGroupedByStatus(Specification<ClientAssessmentResult> specification);
}
