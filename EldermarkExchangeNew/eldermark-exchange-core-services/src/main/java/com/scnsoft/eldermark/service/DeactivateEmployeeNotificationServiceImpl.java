package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.DeactivateEmployeeNotificationDao;
import com.scnsoft.eldermark.entity.DeactivateEmployeeNotification;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.service.notification.sender.DeactivateEmployeeNotificationSender;
import com.scnsoft.eldermark.util.StreamUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
@Transactional
public class DeactivateEmployeeNotificationServiceImpl implements DeactivateEmployeeNotificationService {

    private final Logger logger = LoggerFactory.getLogger(DeactivateEmployeeNotificationServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DeactivateEmployeeNotificationDao deactivateEmployeeNotificationDao;

    @Autowired
    private DeactivateEmployeeNotificationSender deactivateEmployeeNotificationSender;

    @Override
    public void send(Iterable<Long> employeeIds) {
        StreamUtils.stream(employeeService.findAllById(employeeIds))
                .map(this::createNotification)
                .filter(Objects::nonNull)
                .forEach(this::sendNotification);
    }

    private DeactivateEmployeeNotification createNotification(Employee employee) {
        logger.info("Create notification for employee ID: {}", employee.getId());
        if (EmployeeStatus.ACTIVE != employee.getStatus()) {
            logger.info("Can not send notification, because employee with ID {} is not active", employee.getId());
            return null;
        }
        var notification = new DeactivateEmployeeNotification();
        notification.setCreatedDatetime(Instant.now());
        notification.setEmployee(employee);
        var email = PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.EMAIL).orElse(null);
        if (email != null) {
            notification.setDestination(email);
            return deactivateEmployeeNotificationDao.save(notification);
        } else {
            logger.info("Can not create and send notification, because employee with ID {} doesn't have an email", employee.getId());
            return null;
        }
    }

    private void sendNotification(DeactivateEmployeeNotification notification) {
        deactivateEmployeeNotificationSender.send(notification.getId());
    }
}
