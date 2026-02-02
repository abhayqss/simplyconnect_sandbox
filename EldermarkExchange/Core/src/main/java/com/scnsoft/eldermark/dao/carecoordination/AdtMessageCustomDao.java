package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.AdtTypeEnum;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;

public interface AdtMessageCustomDao {
    AdtMessage getMessageById(Long msgId, AdtTypeEnum adtType);
}
