package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.InquiryData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("inquirySourceDao")
public class InquirySourceDaoImpl extends StandardSourceDaoImpl<InquiryData, Long> {
    public InquirySourceDaoImpl() {
        super(InquiryData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}
