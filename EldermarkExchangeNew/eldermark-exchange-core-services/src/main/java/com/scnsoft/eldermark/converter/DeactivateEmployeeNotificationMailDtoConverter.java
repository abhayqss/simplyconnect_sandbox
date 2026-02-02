package com.scnsoft.eldermark.converter;

import com.scnsoft.eldermark.dto.notification.deactivate.DeactivateEmployeeNotificationMailDto;
import com.scnsoft.eldermark.entity.DeactivateEmployeeNotification;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Component
@Transactional
public class DeactivateEmployeeNotificationMailDtoConverter implements Converter<DeactivateEmployeeNotification, DeactivateEmployeeNotificationMailDto> {

    private final ZoneId zoneId =  ZoneId.of( "America/Chicago" );

    @Value("${deactivate.employee.prior.email.notification.minutes}")
    private long priorPeriodMinutes;

    @Override
    public DeactivateEmployeeNotificationMailDto convert(DeactivateEmployeeNotification source) {
        var target = new DeactivateEmployeeNotificationMailDto();
        target.setReceiverFullName(source.getEmployee().getFullName());
        target.setReceiverEmail(source.getDestination());
        target.setUsername(source.getEmployee().getLoginName());
        target.setCompanyId(source.getEmployee().getOrganization().getSystemSetup().getLoginCompanyId());
        target.setDeactivateDate(DateTimeUtils.formatDate(source.getCreatedDatetime().plus(priorPeriodMinutes, ChronoUnit.MINUTES), zoneId));
        return target;
    }
}
