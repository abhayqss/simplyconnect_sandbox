package com.scnsoft.eldermark.entity.client;

import java.time.Instant;
import java.time.LocalDate;

public interface ClientListInfo {
    Long getId();
    String getFirstName();
    String getLastName();
    String getGenderDisplayName();
    LocalDate getBirthDate();
    String getSsnLastFourDigits();
    Instant getCreatedDate();
    String getCommunityName();
    Long getCommunityId();
    String getAvatarAvatarName();
    Long getAvatarId();
    Boolean getActive();
    String getRiskScore();
    String getUnitNumber();
}
