package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.MedicalDeviceType;

public interface MedicalDeviceTypeDao extends JpaRepository<MedicalDeviceType, Long> {

}
