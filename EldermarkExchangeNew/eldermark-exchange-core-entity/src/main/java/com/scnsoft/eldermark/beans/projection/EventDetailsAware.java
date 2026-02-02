package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.event.EventAuthor;

import java.time.Instant;

public interface EventDetailsAware extends ClientIdNamesAware, ClientCommunityIdNameAware, ClientOrganizationIdNameAware {

    Instant getEventDateTime();

    String getEventTypeDescription();

    boolean getIsErVisit();

    boolean getIsOvernightIn();

    String getLocation();

    String getSituation();

    String getBackground();

    String getAssessment();

    boolean getIsFollowup();

    String getFollowup();

    boolean getIsInjury();

    String getEventAuthorFirstName();

    String getEventAuthorLastName();
}
