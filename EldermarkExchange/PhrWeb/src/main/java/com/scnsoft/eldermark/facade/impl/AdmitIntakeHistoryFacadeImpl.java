package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.entity.AdmitIntakeResidentDate;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.facade.AdmitIntakeHistoryFacade;
import com.scnsoft.eldermark.facade.BasePhrFacade;
import com.scnsoft.eldermark.service.AdmitIntakeHistoryService;
import com.scnsoft.eldermark.web.entity.AdmitDateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdmitIntakeHistoryFacadeImpl extends BasePhrFacade implements AdmitIntakeHistoryFacade {

    @Autowired
    private AdmitIntakeHistoryService admitIntakeHistoryService;

    @Autowired
    private Converter<AdmitIntakeResidentDate, AdmitDateDto> admitIntakeDateDtoConverter;


    @Override
    public Page<AdmitDateDto> getAdmitIntakeDatesForReceiver(Long receiverId, Pageable pageable) {
        return admitIntakeHistoryService.getAdmitIntakeHistoryForResident(getReceiverMainResidentId(receiverId, AccessRight.Code.MY_PHR), pageable)
                .map(admitIntakeDateDtoConverter);
    }

    @Override
    public Page<AdmitDateDto> getAdmitIntakeDatesForUser(Long userId, Pageable pageable) {
        return admitIntakeHistoryService.getAdmitIntakeHistoryForResident(getUserMainResidentId(userId, AccessRight.Code.MY_PHR), pageable)
                .map(admitIntakeDateDtoConverter);
    }

    @Override
    public Page<AdmitDateDto> getAdmitIntakeDatesForResident(Long residentId, Pageable pageable) {
        validateAssociation(residentId, AccessRight.Code.MY_PHR);
        return admitIntakeHistoryService.getAdmitIntakeHistoryForResident(residentId, pageable)
                .map(admitIntakeDateDtoConverter);
    }

}
