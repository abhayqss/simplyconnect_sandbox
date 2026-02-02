package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.SecurityGroupData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(value = "securityGroupSourceDao")
public class SecurityGroupSourceDao extends StandardSourceDaoImpl<SecurityGroupData, Long> {
    public SecurityGroupSourceDao() {
        super(SecurityGroupData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
