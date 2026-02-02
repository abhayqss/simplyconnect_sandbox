package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.entity.AdmitDateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdmitIntakeHistoryFacade {

    Page<AdmitDateDto> getAdmitIntakeDatesForReceiver(Long receiverId, Pageable pageable);

    Page<AdmitDateDto> getAdmitIntakeDatesForUser(Long userId, Pageable pageable);

    Page<AdmitDateDto> getAdmitIntakeDatesForResident(Long residentId, Pageable pageable);
}
