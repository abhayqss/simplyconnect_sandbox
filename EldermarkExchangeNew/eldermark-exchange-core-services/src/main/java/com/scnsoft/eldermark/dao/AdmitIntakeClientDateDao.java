package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AdmitIntakeClientDate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdmitIntakeClientDateDao extends JpaRepository<AdmitIntakeClientDate, Long> {

    List<AdmitIntakeClientDate> getAllByClientId(Long clientId, Sort sort);

}
