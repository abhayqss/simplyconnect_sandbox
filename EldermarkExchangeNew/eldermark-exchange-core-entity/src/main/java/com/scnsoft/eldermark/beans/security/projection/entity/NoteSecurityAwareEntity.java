package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.ClientCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.ClientHieConsentPolicyTypeAware;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.EventIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.Set;

public interface NoteSecurityAwareEntity extends IdAware, EventIdAware, ClientIdAware, ClientCommunityIdAware, ClientHieConsentPolicyTypeAware, EmployeeIdAware {

    Boolean getSubTypeManual();

    Set<Long> getNoteClientIds();

    default Set<Long> resolveNoteClientIds() {
        if (CollectionUtils.isEmpty(getNoteClientIds())) {
            return Collections.singleton(getClientId());
        }
        return getNoteClientIds();
    }
}
