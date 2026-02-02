package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.EventSecurityAwareEntity;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventDashboardItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventService extends SecurityAwareEntityService<EventSecurityAwareEntity, Long>, ProjectingService<Long> {

    Event save(Event event);

    Event saveWithoutSendingNotifications(Event event);

    void sendEventNotification(Event savedEvent);

    Event findById(Long eventId);

    boolean isViewableForAnyEmployee(Collection<Employee> employees, Long eventId);

    boolean isViewableForAnyEmployeeIds(Collection<Long> employeeIds, Long eventId);

    EventSecurityAwareEntity findSecurityAwareEntity(Long id);

    List<EventDashboardItem> find(Long clientId, PermissionFilter permissionFilter, Integer limit, Sort sort);

    Optional<Event> findLastByAppointmentChainIdAndEventTypeCodes(Long appointmentChainId, List<String> eventTypeCodes);

    <P> List<P> find(Specification<Event> specification, Class<P> projectionClass);
}
