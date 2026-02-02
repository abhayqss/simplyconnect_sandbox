package com.scnsoft.eldermark.services.task;

import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pzhurba on 05-Nov-15.
 */
@Transactional
public interface EmployeeRequestCleaner {
    void cleanExpiredInvitation();
}
