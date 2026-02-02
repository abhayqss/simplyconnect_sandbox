package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.service.PlanOfCareService;
import com.scnsoft.eldermark.service.validation.AccessibleResidentsCareReceiverProvider;
import com.scnsoft.eldermark.service.validation.AccessibleResidentsUserProvider;
import com.scnsoft.eldermark.web.entity.PlanOfCareDto;
import com.scnsoft.eldermark.web.entity.PlanOfCareInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PlanOfCareFacade extends BasePhrFacade {

    @Autowired
    private AccessibleResidentsCareReceiverProvider accessibleResidentsCareReceiverProvider;

    @Autowired
    private AccessibleResidentsUserProvider accessibleResidentsUserProvider;

    @Autowired
    private Converter<PlanOfCareActivity, PlanOfCareDto> planOfCareInfoConverter;

    @Autowired
    private Converter<PlanOfCareActivity, PlanOfCareInfoDto> planOfCareConverter;

    @Autowired
    private PlanOfCareService planOfCareService;

    public Page<PlanOfCareInfoDto> getPlanOfCareActivitiesForUser(Long userId, Pageable pageable){
        return planOfCareService.getPlansOfCare(accessibleResidentsUserProvider
                .getAccessibleResidentsOrThrow(userId, AccessRight.Code.MY_PHR), pageable)
                .map(getPlanOfCareConverter());
    }

    public Page<PlanOfCareInfoDto> getPlanOfCareActivitiesForReceiver(Long receiverId, Pageable pageable){
        return planOfCareService.getPlansOfCare(accessibleResidentsCareReceiverProvider
                .getAccessibleResidentsOrThrow(receiverId, AccessRight.Code.MY_PHR), pageable)
                .map(getPlanOfCareConverter());
    }

    public PlanOfCareDto getPlanOfCareActivity(Long planOfCareActivityId) {
        final PlanOfCareActivity planOfCareActivity = planOfCareService.getPlanOfCareActivity(planOfCareActivityId);
        validateAssociation(planOfCareActivity.getPlanOfCare().getResident().getId(), AccessRight.Code.MY_PHR);
        return planOfCareInfoConverter.convert(planOfCareActivity);
    }

    public Converter<PlanOfCareActivity, PlanOfCareInfoDto> getPlanOfCareConverter() {
        return planOfCareConverter;
    }
}
