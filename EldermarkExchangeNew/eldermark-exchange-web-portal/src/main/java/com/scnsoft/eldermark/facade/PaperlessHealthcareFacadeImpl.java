package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.RequestDemoDto;
import com.scnsoft.eldermark.dto.support.SubmitDemoRequestDto;
import com.scnsoft.eldermark.service.DemoRequestService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PaperlessHealthcareSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class PaperlessHealthcareFacadeImpl implements PaperlessHealthcareFacade {

    @Autowired
    private PaperlessHealthcareSecurityService securityService;

    @Autowired
    private DemoRequestService demoRequestService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    @PreAuthorize("@experienceCenterSecurityService.canView()")
    public Long createDemoRequest(RequestDemoDto dto) {
        return demoRequestService.submit(new SubmitDemoRequestDto(
                loggedUserService.getCurrentEmployee(),
                dto.getTileName()
        )).getId();
    }

    @Override
    public boolean canView() {
        return securityService.canView();
    }
}
