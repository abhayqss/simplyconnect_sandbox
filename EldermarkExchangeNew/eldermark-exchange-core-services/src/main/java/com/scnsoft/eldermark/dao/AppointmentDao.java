package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.Appointment;

@Repository
public interface AppointmentDao extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    
}
