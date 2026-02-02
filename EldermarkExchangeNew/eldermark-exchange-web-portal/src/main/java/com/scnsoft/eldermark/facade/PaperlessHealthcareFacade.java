package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.RequestDemoDto;

public interface PaperlessHealthcareFacade {

    boolean canView();

    Long createDemoRequest(RequestDemoDto dto);
}
