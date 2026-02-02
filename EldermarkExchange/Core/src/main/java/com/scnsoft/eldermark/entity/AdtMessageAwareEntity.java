package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.xds.message.AdtMessage;

public interface AdtMessageAwareEntity {

    AdtMessage getAdtMessage();

    void setAdtMessage(AdtMessage adtMessage);

}
