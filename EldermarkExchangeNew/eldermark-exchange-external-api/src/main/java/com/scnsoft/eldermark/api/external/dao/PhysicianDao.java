package com.scnsoft.eldermark.api.external.dao;

import com.scnsoft.eldermark.api.external.entity.Physician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PhysicianDao extends JpaRepository<Physician, Long> {

    Optional<Physician> findByIdAndDiscoverableTrueAndVerifiedTrue(Long id);

    @Query("SELECT id FROM Physician p WHERE p.employee.id = :employeeId")
    Long getIdByEmployeeId(@Param("employeeId") Long employeeId);

}
