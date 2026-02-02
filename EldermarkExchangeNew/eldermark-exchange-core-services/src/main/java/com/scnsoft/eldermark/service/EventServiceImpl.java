package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.EventSecurityAwareEntity;
import com.scnsoft.eldermark.dao.EventDao;
import com.scnsoft.eldermark.dao.specification.EventSpecificationGenerator;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventDashboardItem;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.jms.producer.EventCreatedQueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private EventDao eventDao;

    @Autowired
    private EventSpecificationGenerator eventSpecificationGenerator;

    @Autowired
    private EventNotificationService eventNotificationService;

    @Autowired
    private EventCreatedQueueProducer eventUpdateQueueProducer;

    @Override
    @Transactional
    public Event save(Event event) {
        var savedEvent = eventDao.save(event);
        sendEventNotification(savedEvent);
        return savedEvent;
    }

    @Override
    public void sendEventNotification(Event savedEvent) {
        eventNotificationService.send(savedEvent);
        eventUpdateQueueProducer.putToEventCreatedQueue(savedEvent.getId());
    }

    @Override
    public Event saveWithoutSendingNotifications(Event event) {
        return eventDao.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Event findById(Long eventId) {
        return eventDao.findById(eventId).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isViewableForAnyEmployee(Collection<Employee> employees, Long eventId) {
        return isViewableForAnyEmployeeIds(employees.stream().map(Employee::getId).collect(Collectors.toList()), eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isViewableForAnyEmployeeIds(Collection<Long> employeeIds, Long eventId) {
        var viewableEvents = eventSpecificationGenerator.viewableForAnyEmployee(employeeIds);
        var byId = eventSpecificationGenerator.byId(eventId);

        return eventDao.count(byId.and(viewableEvents)) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public EventSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return eventDao.findById(id, EventSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return eventDao.findByIdIn(ids, EventSecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDashboardItem> find(Long clientId, PermissionFilter permissionFilter, Integer limit, Sort sort) {
        var hasAccess = eventSpecificationGenerator.hasAccess(permissionFilter);
        var byClient = eventSpecificationGenerator.byClientIdAndMerged(clientId);
        var excludeService = eventSpecificationGenerator.excludeService();
        return eventDao.findAll(byClient.and(hasAccess).and(excludeService), EventDashboardItem.class, sort, limit);
    }

    @Override
    public Optional<Event> findLastByAppointmentChainIdAndEventTypeCodes(Long appointmentChainId, List<String> eventTypeCodes) {
        return eventDao.findFirst(
                eventSpecificationGenerator.byAppointmentChainId(appointmentChainId)
                        .and(eventSpecificationGenerator.byEventTypeCodes(eventTypeCodes)),
                Event.class,
                Sort.by(Sort.Direction.DESC, Event_.EVENT_DATE_TIME)
        );
    }

    @Override
    public <P> List<P> find(Specification<Event> specification, Class<P> projectionClass) {
        return eventDao.findAll(specification, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return eventDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return eventDao.findByIdIn(ids, projection);
    }
}
