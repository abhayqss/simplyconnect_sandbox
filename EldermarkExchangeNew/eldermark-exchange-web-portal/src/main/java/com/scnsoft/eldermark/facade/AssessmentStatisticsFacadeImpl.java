package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientAssessmentCount;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.StatusCountDto;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AssessmentStatisticsFacadeImpl implements AssessmentStatisticsFacade {

    @Autowired
    private ListAndItemConverter<ClientAssessmentCount, StatusCountDto> assessmentCountDtoConverter;

    @Autowired
    private ClientAssessmentResultService clientAssessmentResultService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewList()")
    public Long count() {
        return clientAssessmentResultService.count(permissionFilterService.createPermissionFilterForCurrentUser());
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewList()")
    public List<StatusCountDto> countGroupedByStatus() {
        var assessmentStatus = clientAssessmentResultService
                .countGroupedByStatus(permissionFilterService.createPermissionFilterForCurrentUser());
        return assessmentCountDtoConverter.convertList(assessmentStatus);
    }

}
