package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.entity.ResultDto;
import com.scnsoft.eldermark.web.entity.ResultInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResultsFacade {

    Page<ResultInfoDto> getResultsForUser(Long userId, Pageable pageable);

    Page<ResultInfoDto> getResultsForReceiver(Long receiverId, Pageable pageable);

    ResultDto getResult(Long resultObservationId);
}
