package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(value = "companySourceDao")
public class CompanySourceDaoImpl extends StandardSourceDaoImpl<CompanyData, String> {
    public CompanySourceDaoImpl() {
        super(CompanyData.class, String.class, Constants.SYNC_STATUS_COLUMN);
    }
}
