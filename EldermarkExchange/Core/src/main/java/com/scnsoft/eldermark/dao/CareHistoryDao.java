package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.CareHistory;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CareHistoryDao extends ResidentAwareDao<CareHistory> {
    @Override
    List<CareHistory> listByResidentIds(List<Long> residentIds, Pageable pageable);
}
