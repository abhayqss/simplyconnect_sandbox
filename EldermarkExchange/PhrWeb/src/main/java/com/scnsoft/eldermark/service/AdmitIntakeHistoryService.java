package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.AdmitIntakeResidentDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdmitIntakeHistoryService {

    Page<AdmitIntakeResidentDate> getAdmitIntakeHistoryForResident(Long residentId, Pageable pageable);

}
