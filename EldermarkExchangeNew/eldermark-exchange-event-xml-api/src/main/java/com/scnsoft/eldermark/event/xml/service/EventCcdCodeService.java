package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.event.xml.entity.emuns.Gender;

public interface EventCcdCodeService {

    CcdCode getGenderCcdCode(Gender gender);

    CcdCode getMaritalStatus(String maritalStatus);
}
