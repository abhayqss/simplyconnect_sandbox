package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.entity.ResultObservation;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.facade.BasePhrFacade;
import com.scnsoft.eldermark.facade.ResultsFacade;
import com.scnsoft.eldermark.service.ResultService;
import com.scnsoft.eldermark.web.entity.ResultDto;
import com.scnsoft.eldermark.web.entity.ResultInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class ResultsFacadeImpl extends BasePhrFacade implements ResultsFacade {

    @Autowired
    private ResultService resultService;

    @Autowired
    private Converter<ResultObservation, ResultInfoDto> resultInfoDtoConverter;

    @Autowired
    private Converter<ResultObservation, ResultDto> resultDtoConverter;

    @Override
    public Page<ResultInfoDto> getResultsForUser(Long userId, Pageable pageable) {
        return resultService.getResults(getUserResidentIds(userId, AccessRight.Code.MY_PHR), pageable)
                .map(resultInfoDtoConverter);
    }

    @Override
    public Page<ResultInfoDto> getResultsForReceiver(Long receiverId, Pageable pageable) {
        return resultService.getResults(getReceiverResidentIds(receiverId, AccessRight.Code.MY_PHR), pageable)
                .map(resultInfoDtoConverter);
    }

    @Override
    public ResultDto getResult(Long resultObservationId) {
        final ResultObservation resultObservation = resultService.getResult(resultObservationId);
        validateAssociation(resultObservation.getResult().getResident().getId(), AccessRight.Code.MY_PHR);
        return resultDtoConverter.convert(resultObservation);
    }
}
