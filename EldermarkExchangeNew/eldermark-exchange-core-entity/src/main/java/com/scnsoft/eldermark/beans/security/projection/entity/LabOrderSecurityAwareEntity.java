package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderStatus;

public interface LabOrderSecurityAwareEntity extends ClientIdAware {
    LabResearchOrderStatus getStatus();
}
