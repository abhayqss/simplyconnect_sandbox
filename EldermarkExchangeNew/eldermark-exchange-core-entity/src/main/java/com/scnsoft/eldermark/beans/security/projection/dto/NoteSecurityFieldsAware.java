package com.scnsoft.eldermark.beans.security.projection.dto;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.EventIdAware;

import java.util.List;

public interface NoteSecurityFieldsAware extends ClientIdAware, EventIdAware {

    List<Long> getClientIds();
}
