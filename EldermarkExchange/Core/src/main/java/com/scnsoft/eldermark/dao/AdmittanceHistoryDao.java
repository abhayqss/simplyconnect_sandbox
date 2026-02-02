package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AdmittanceHistory;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdmittanceHistoryDao extends ResidentAwareDao<AdmittanceHistory> {
    @Override
    List<AdmittanceHistory> listByResidentIds(List<Long> residentIds, Pageable pageable);
}
