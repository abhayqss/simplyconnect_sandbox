package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AdmittanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface JpaAdmittanceHistoryDao extends JpaRepository<AdmittanceHistory, Long> {

    Page<AdmittanceHistory> getAllByResidentIdAndAdmitDateIsNotNull(Long residentId, Pageable pageable);
    AdmittanceHistory getByResidentIdAndAdmitDate(Long residentId, Date admitDate);
    AdmittanceHistory getByResidentIdAndDischargeDate(Long residentId, Date dischargeDate);
    List<AdmittanceHistory> getByResident_IdAndOrganizationId(Long residentId, Long organizationId);
}
