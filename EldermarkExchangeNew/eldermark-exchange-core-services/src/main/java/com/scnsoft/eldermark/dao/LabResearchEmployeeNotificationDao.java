package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.lab.LabResearchResultsEmployeeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabResearchEmployeeNotificationDao extends JpaRepository<LabResearchResultsEmployeeNotification, Long> {
    List<LabResearchResultsEmployeeNotification> findBySentDatetimeIsNull();
    boolean existsByEmployeeAndSentDatetimeIsNull(Employee employee);
}
