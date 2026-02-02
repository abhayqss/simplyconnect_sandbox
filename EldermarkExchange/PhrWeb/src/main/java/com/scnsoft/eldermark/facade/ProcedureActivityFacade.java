package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.entity.ProcedureActivity;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.service.ProcedureActivityService;
import com.scnsoft.eldermark.service.validation.AccessibleResidentsCareReceiverProvider;
import com.scnsoft.eldermark.service.validation.AccessibleResidentsUserProvider;
import com.scnsoft.eldermark.web.entity.ProcedureDetailsDto;
import com.scnsoft.eldermark.web.entity.ProcedureListItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProcedureActivityFacade extends BasePhrFacade {

    @Autowired
    private AccessibleResidentsCareReceiverProvider accessibleResidentsCareReceiverProvider;

    @Autowired
    private AccessibleResidentsUserProvider accessibleResidentsUserProvider;

    @Autowired
    private ProcedureActivityService procedureActivityService;

    @Autowired
    private Converter<ProcedureActivity, ProcedureDetailsDto> procedureDetailsDtoConverter;

    @Autowired
    private Converter<ProcedureActivity, ProcedureListItemDto> procedureListConverter;

    public Page<ProcedureListItemDto> getProcedureActivitiesForUser(Long userId, Pageable pageable) {
        return procedureActivityService.getProcedureActivities(accessibleResidentsUserProvider
                .getAccessibleResidentsOrThrow(userId, AccessRight.Code.MY_PHR), pageable)
                .map(getProcedureListConverter());
    }

    public Page<ProcedureListItemDto> getProcedureActivitiesForReceiver(Long receiverId, Pageable pageable) {
        return procedureActivityService.getProcedureActivities(accessibleResidentsCareReceiverProvider
                .getAccessibleResidentsOrThrow(receiverId, AccessRight.Code.MY_PHR), pageable)
                .map(getProcedureListConverter());
    }

    public ProcedureDetailsDto getProcedureActivity(Long procedureActivityId) {
        final ProcedureActivity procedureActivity = procedureActivityService.getProcedureActivity(procedureActivityId);
        validateAssociation(procedureActivity.getProcedure().getResident().getId(), AccessRight.Code.MY_PHR);
        return procedureDetailsDtoConverter.convert(procedureActivity);
    }

    public Converter<ProcedureActivity, ProcedureListItemDto> getProcedureListConverter() {
        return procedureListConverter;
    }
}
