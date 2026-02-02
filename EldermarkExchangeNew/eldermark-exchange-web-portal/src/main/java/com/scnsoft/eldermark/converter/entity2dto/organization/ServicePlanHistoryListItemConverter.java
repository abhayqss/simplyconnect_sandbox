package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.serviceplan.ServicePlanHistoryDto;

@Component
public class ServicePlanHistoryListItemConverter
        implements ListAndItemConverter<ServicePlan, ServicePlanHistoryDto> {

    @Override
    public ServicePlanHistoryDto convert(ServicePlan source) {
        if (source == null) {
            return null;
        }
        ServicePlanHistoryDto target = new ServicePlanHistoryDto();
        target.setId(source.getId());
        target.setDateModified(DateTimeUtils.toEpochMilli(source.getLastModifiedDate()));
        target.setStatus(source.getAuditableStatus() != null ? source.getAuditableStatus().getDisplayName() : null);
        target.setAuthor(source.getEmployee().getFullName());
        target.setAuthorRole(source.getEmployee().getCareTeamRole().getName());
        target.setIsArchived(source.getArchived());
        return target;
    }

}
