package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dao.ReferralInfoRequestDao;
import com.scnsoft.eldermark.dao.ReferralRequestDao;
import com.scnsoft.eldermark.dto.referral.ReferralCommunicationDto;
import com.scnsoft.eldermark.entity.referral.ReferralInfoRequest;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ReferralInfoEntityConverter implements Converter<ReferralCommunicationDto, ReferralInfoRequest> {

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ReferralRequestDao referralRequestDao;

    @Autowired
    private ReferralInfoRequestDao referralInfoRequestDao;

    @Override
    public ReferralInfoRequest convert(ReferralCommunicationDto dto) {
        ReferralInfoRequest target;
        if (dto.getId() == null) {
            //request
            target = new ReferralInfoRequest();

            target.setRequestDatetime(Instant.now());
            target.setRequesterEmployee(loggedUserService.getCurrentEmployee());

            target.setSubject(dto.getSubject());
            target.setRequestMessage(dto.getRequest().getText());
            target.setRequesterName(dto.getRequest().getAuthorFullName());
            target.setRequesterPhoneNumber(dto.getRequest().getAuthorPhone());

            var request = referralRequestDao.getOne(dto.getReferralRequestId());

            request.getInfoRequests().add(target);
            target.setReferralRequest(request);
        } else {
            //response
            target = referralInfoRequestDao.findById(dto.getId()).orElseThrow();
            if (target.getResponseDatetime() != null) {
                throw new BusinessException("Referral info request is already 'Replied'");
            }

            target.setResponderEmployee(loggedUserService.getCurrentEmployee());
            target.setResponseDatetime(Instant.now());

            target.setResponderName(dto.getResponse().getAuthorFullName());
            target.setResponderPhoneNumber(dto.getResponse().getAuthorPhone());
            target.setResponseMessage(dto.getResponse().getText());
        }

        return target;
    }
}
