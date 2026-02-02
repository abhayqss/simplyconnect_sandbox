package com.scnsoft.eldermark.mobile.projection.careteam.invitation;

import com.scnsoft.eldermark.beans.projection.ClientNamesAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitationStatus;

import java.time.Instant;
import java.time.LocalDate;

public interface ClientCareTeamInvitationDetails extends IdAware,
        TargetEmployeeInvitationDataAware,
        ClientAvatarInvitationDataAware,
        ClientNamesAware {

    String getFirstName();

    String getLastName();

    LocalDate getBirthDate();

    String getEmail();

    ClientCareTeamInvitationStatus getStatus();

    Instant getCreatedAt();
}
