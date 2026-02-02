package com.scnsoft.eldermark.entity.note;

import com.scnsoft.eldermark.beans.projection.ClientCommunityNameAware;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.ClientNamesAware;

import java.time.Instant;
import java.util.List;

public interface ClientProgramNoteAware extends ClientIdAware, ClientNamesAware, ClientCommunityNameAware {

    String getClientProgramNoteTypeDescription();

    String getServiceProvider();

    Instant getStartDate();

    Instant getEndDate();

    List<Long> getNoteClientIds();
}
