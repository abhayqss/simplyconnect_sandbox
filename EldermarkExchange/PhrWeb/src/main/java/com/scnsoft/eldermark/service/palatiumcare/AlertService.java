package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.dao.palatiumcare.AlertDao;
import com.scnsoft.eldermark.shared.palatiumcare.AlertDto;
import com.scnsoft.eldermark.entity.palatiumcare.Alert;
import com.scnsoft.eldermark.entity.palatiumcare.AlertStatus;
import com.scnsoft.eldermark.entity.palatiumcare.AlertType;
import com.scnsoft.eldermark.mapper.palatiumcare.alert.AlertMapper;
import com.scnsoft.eldermark.service.PrivilegesService;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.shared.carecoordination.events.NotifyEventDto;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.util.EldermarkCollectionUtils;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class AlertService {

    private AlertDao alertDao;

    private PrivilegesService privilegesService;

    private EventService eventService;

    private AlertMapper alertMapper = new AlertMapper();

    @Autowired
    public void setPrivilegesService(PrivilegesService privilegesService) {
        this.privilegesService = privilegesService;
    }

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @Autowired
    public void setAlertDao(AlertDao alertDao) {
        this.alertDao = alertDao;
    }


    private void canViewAlertOrThrow() {
        if (!privilegesService.canViewAlert()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    private void canChangeAlertStatusOrThrow() {
        if (!privilegesService.canChangeAlertStatus()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    @Transactional
    public void saveAlert(Alert alert) {
       // Alert saved = alertDao.save(alert);

    }

    public Page<AlertDto>  getAlertList(Integer page, Integer pageSize) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"status"),
                new Sort.Order(Sort.Direction.DESC,"id")); ///@todo: fix, sorting by id is incorrect
        Pageable pageableWithSort = new PageRequest(0, Integer.MAX_VALUE, sort);
        if (page >= 0 && pageSize > 0) {
            pageableWithSort = new PageRequest(page, pageSize, sort);
        }
        Page<Alert> alertPage = alertDao.findAll(pageableWithSort);
        return new PageImpl<>(alertMapper.entityListToDtoList(alertPage.getContent()),
                new PageRequest(page, pageSize),
                alertPage.getTotalElements());
    }

    public List<AlertDto> getOrderedAlertList() {
        // canViewAlertOrThrow();
        List<Alert> alertList = EldermarkCollectionUtils.listFromIterable(alertDao.loadAlertList());
        Collections.reverse(alertList);
        return alertMapper.entityListToDtoList(alertList);
    }

    public AlertDto getAlertById(Long id) {
        // canViewAlertOrThrow();
        Alert alert = alertDao.findOne(id);
        return alertMapper.entityToDto(alert);
    }

    private NotifyEventDto createNotifyEvent(Alert alert) {
        NotifyEventDto eventDto = new NotifyEventDto(
                alert.getResponder().getEmployeeId(),
                alert.getEvent().getResident().getId(),
                alert.getEvent().getEventDateTime()
        );
        return eventDto;
    }

    private void validateAlertFlow(Alert alert, AlertStatus updatedStatus) throws IncorrectAlertFlowException {
        AlertStatus currentStatus  = alert.getStatus();
        AlertType alertType = alert.getAlertType();
        if(updatedStatus == currentStatus) throw new IncorrectAlertFlowException(currentStatus, updatedStatus);
        if(alertType == AlertType.ACTIVE) {
            switch (currentStatus) {
                case NOT_TAKEN_YET:
                    if (updatedStatus != AlertStatus.TAKEN) {
                        throw new IncorrectAlertFlowException(currentStatus, updatedStatus);
                    }
                    break;
                case TAKEN:
                    if (updatedStatus != AlertStatus.NOT_TAKEN_YET && updatedStatus != AlertStatus.COMPLETED) {
                        throw new IncorrectAlertFlowException(currentStatus, updatedStatus);
                    }
                    break;
                case COMPLETED:
                    throw new IncorrectAlertFlowException(currentStatus, updatedStatus);
                case CLOSED:
                    throw new IncorrectAlertFlowException(currentStatus, updatedStatus);
            }
        }
        else if(alertType == AlertType.SYSTEM && updatedStatus != AlertStatus.CLOSED) {
            throw new IncorrectAlertFlowException(currentStatus, updatedStatus);
        }
    }


    @Transactional
    public void changeAlertStatus(Long id, AlertStatus updatedStatus) throws IncorrectAlertFlowException {
        // canChangeAlertStatusOrThrow();
        Alert alert = alertDao.findOne(id);
        AlertType alertType = alert.getAlertType();
        validateAlertFlow(alert, updatedStatus);
        Long currentUserId = null;
        if(updatedStatus != AlertStatus.NOT_TAKEN_YET) {
            currentUserId = PhrSecurityUtils.getCurrentUserId();
        }
        alertDao.changeAlertStatus(id, currentUserId, updatedStatus.toString());
        if((alertType == AlertType.SYSTEM && updatedStatus == AlertStatus.CLOSED) ||
                (alertType == AlertType.ACTIVE && updatedStatus == AlertStatus.COMPLETED)) {
            NotifyEventDto eventDto = createNotifyEvent(alert);
            eventService.createNotifyEvent(eventDto);
        }
    }




}
