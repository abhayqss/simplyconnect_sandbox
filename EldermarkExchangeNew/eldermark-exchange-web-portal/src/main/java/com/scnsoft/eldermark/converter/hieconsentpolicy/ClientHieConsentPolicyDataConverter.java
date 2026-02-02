package com.scnsoft.eldermark.converter.hieconsentpolicy;

import com.scnsoft.eldermark.dto.ClientDto;
import com.scnsoft.eldermark.dto.hieconsentpolicy.ClientHieConsentPolicyData;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ClientHieConsentPolicyDataConverter implements Converter<ClientDto, ClientHieConsentPolicyData> {

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    public ClientHieConsentPolicyData convert(ClientDto clientDto) {
        var target = new ClientHieConsentPolicyData();
        target.setObtainedFrom(clientDto.getHieConsentPolicyObtainedFrom());
        target.setUpdateDateTime(DateTimeUtils.toInstant(clientDto.getHieConsentPolicyObtainedDate()));
        target.setObtainedBy(clientDto.getHieConsentPolicyObtainedBy());
        target.setType(clientDto.getHieConsentPolicyName());
        target.setSource(HieConsentPolicySource.WEB);
        target.setAuthor(loggedUserService.getCurrentEmployee());
        return target;
    }
}
