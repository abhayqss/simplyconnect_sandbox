package com.scnsoft.eldermark.web.controller.provider;

import com.scnsoft.eldermark.entity.CcdSection;

import java.util.Map;

public interface CcdModelAttributesProvider {

    Map<String, Object> getAttributesForAdd(Long residentId);

    Map<String, Object> getAttributesForEdit(Long residentId, Long ccdEntryId);

    Map<String, Object> getAttributesForView(Long residentId, Long ccdEntryId);

    CcdSection getSection();

}
