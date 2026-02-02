package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.RegistrationConfirmationMailDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseInvitationService {

    @Value("${reset.password.request.url}")
    private String resetPasswordRequestUrl;

    @Value("${reset.password.external.request.url}")
    private String resetPasswordExternalRequestUrl;

    @Autowired
    private ExchangeMailService exchangeMailService;

    protected void sendRegistrationConfirmationEmail(Employee targetEmployee, boolean isExternal) {
        RegistrationConfirmationMailDto dto = new RegistrationConfirmationMailDto();
        dto.setToEmail(targetEmployee.getLoginName());
        //phase 2
//                targetEmployee.getPerson().getTelecoms().stream()
//                        .filter(x -> x.getUseCode().equals(PersonTelecomCode.EMAIL.name()))
//                        .map(PersonTelecom::getValue)
//                        .findFirst()
//                        .orElseThrow()

        dto.setFullName(targetEmployee.getFullName());
        dto.setUsername(targetEmployee.getLoginName());
        dto.setCompanyId(targetEmployee.getOrganization().getSystemSetup().getLoginCompanyId());
        dto.setPasswordResetUrl(isExternal ? resetPasswordExternalRequestUrl : resetPasswordRequestUrl);

        if (isExternal) {
            exchangeMailService.sendExternalRegistrationConfirmation(dto);
        } else {
            exchangeMailService.sendRegistrationConfirmation(dto);
        }
    }
}
