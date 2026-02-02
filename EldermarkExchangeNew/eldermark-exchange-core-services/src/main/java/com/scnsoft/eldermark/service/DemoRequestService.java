package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.support.SubmitDemoRequestDto;
import com.scnsoft.eldermark.entity.DemoRequest;

public interface DemoRequestService {
    DemoRequest submit(SubmitDemoRequestDto dto);
}
