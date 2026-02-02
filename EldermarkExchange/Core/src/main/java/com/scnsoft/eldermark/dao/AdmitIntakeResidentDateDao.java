package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AdmitIntakeResidentDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdmitIntakeResidentDateDao extends JpaRepository<AdmitIntakeResidentDate, Long> {

    Page<AdmitIntakeResidentDate> getAllByResidentId(Long residentId, Pageable pageable);

    List<AdmitIntakeResidentDate> getAllByResidentId(Long residentId, Sort sort);

}
