package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.DemoRequestDao;
import com.scnsoft.eldermark.dto.support.SubmitDemoRequestDto;
import com.scnsoft.eldermark.entity.DemoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class DemoRequestServiceImpl implements DemoRequestService {

    @Autowired
    private DemoRequestDao demoRequestDao;

    @Autowired
    private DemoRequestNotificationService demoRequestNotificationService;

    @Override
    @Transactional
    public DemoRequest submit(SubmitDemoRequestDto dto) {
        var demoRequest = createDemoRequest(dto);
        demoRequestNotificationService.sendDemoRequestSubmittedNotifications(demoRequest);
        return demoRequest;
    }

    private DemoRequest createDemoRequest(SubmitDemoRequestDto dto) {
        var demoRequest = new DemoRequest();
        demoRequest.setDemoTitle(dto.getDemoTitle());
        demoRequest.setAuthor(dto.getAuthor());
        demoRequest.setCreatedDate(Instant.now());
        return demoRequestDao.save(demoRequest);
    }
}
