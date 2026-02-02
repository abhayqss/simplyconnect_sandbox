package com.scnsoft.eldermark.service;

public interface DeactivateEmployeeNotificationService {

    void send(Iterable<Long> employeeIds);
}
