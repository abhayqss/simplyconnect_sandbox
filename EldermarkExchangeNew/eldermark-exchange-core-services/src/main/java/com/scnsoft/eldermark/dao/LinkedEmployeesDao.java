package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.LinkedEmployees;

public interface LinkedEmployeesDao extends JpaRepository<LinkedEmployees, Long> {

    List<LinkedEmployees> findByFirstEmployeeIdOrSecondEmployeeId(Long firstEmployeeId, Long secondEmployeeId);
}
