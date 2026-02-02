package com.scnsoft.eldermark.services.ccd.section;

import com.scnsoft.eldermark.entity.CcdSection;

public interface CcdSectionService {

    String getFreeTextById(Long Id);
    CcdSection getSection();
}
