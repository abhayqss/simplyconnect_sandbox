package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import org.springframework.data.domain.Sort;

public interface ClientAssessmentDao extends AppJpaRepository<ClientAssessmentResult, Long>, CustomClientAssessmentResultDao {

    Sort ORDER_BY_DATE_STARTED = Sort.by(Sort.Direction.ASC, ClientAssessmentResult_.DATE_STARTED);

    Sort ORDER_BY_CLIENT_ID_ASC = Sort.by(Sort.Direction.ASC, ClientAssessmentResult_.CLIENT);

}
