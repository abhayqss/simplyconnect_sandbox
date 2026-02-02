package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.audit.AuditLogFilter;
import com.scnsoft.eldermark.beans.audit.AuditLogFilterDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class AuditLogFilterConverter implements Converter<AuditLogFilterDto, AuditLogFilter> {

    @Override
    public AuditLogFilter convert(AuditLogFilterDto source) {
        var target = new AuditLogFilter();
        target.setOrganizationId(source.getOrganizationId());
        target.setCommunityIds(source.getCommunityIds());
        target.setEmployeeIds(source.getEmployeeIds());
        if (CollectionUtils.isNotEmpty(source.getActivityIds())) {
            target.setActions(AuditLogActivity.getAuditLogActionWithParamsByIds(source.getActivityIds()));
        } else {
            target.setActions(Arrays.stream(AuditLogActivity.values()).map(AuditLogActivity::getActionWithParams).collect(Collectors.toList()));
        }
        target.setClientIds(source.getClientIds());
        target.setFromDate(source.getFromDate());
        target.setToDate(source.getToDate());
        target.setIncludeInactiveCommunities(source.getIncludeInactiveCommunities());
        target.setIncludeInactiveEmployees(source.getIncludeInactiveEmployees());
        target.setIncludeInactiveClients(source.getIncludeInactiveClients());
        return target;
    }
}
